package be.dieterblancke.proxysync.common.redis.impl;

import be.dieterblancke.proxysync.common.config.RedisConfiguration;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import be.dieterblancke.proxysync.common.redis.LuaScript;
import be.dieterblancke.proxysync.common.redis.RedisManager;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClusteredRedisManager implements RedisManager
{

    private final RedisClusterClient redisClient;
    private final GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool;
    private final StatefulRedisClusterPubSubConnection<String, String> pubSubConnection;

    public ClusteredRedisManager( ProxySyncPlugin plugin, RedisConfiguration configuration ) throws RuntimeException
    {
        final List<RedisURI> connectionUris = configuration.getRedisURIs();
        if ( connectionUris.isEmpty() )
        {
            throw new IllegalStateException( "No redis connection details found, at least one is required." );
        }

        this.redisClient = RedisClusterClient.create( connectionUris );

        StatefulRedisClusterConnection<String, String> connection = null;
        try
        {
            connection = this.redisClient.connect();
            final String info = connection.sync().info();
            connection.close();

            final String version = Arrays.stream( info.split( "\r\n" ) )
                    .filter( s -> s.startsWith( "redis_version:" ) )
                    .map( s -> s.split( ":" )[1] )
                    .findFirst()
                    .orElse( null );
            if ( version == null )
            {
                throw new RuntimeException( "Unable to retrieve redis server version." );
            }

            final String[] versionNumbers = version.split( "\\." );
            final int major = Integer.parseInt( versionNumbers[0] );
            final int minor = Integer.parseInt( versionNumbers[1] );
            if ( major < 2 || ( major == 2 && minor < 6 ) )
            {
                throw new RuntimeException( "Lua is not supported on redis versions below 2.6. Please update your redis implementation." );
            }
        }
        catch ( RedisConnectionException exception )
        {
            throw new IllegalStateException( "Unable to connect to redis!" );
        }
        finally
        {
            if ( connection != null && connection.isOpen() )
            {
                connection.close();
            }
        }

        this.pool = ConnectionPoolSupport.createGenericObjectPool(
                redisClient::connect,
                configuration.getPoolingConfiguration().asObjectPoolConfig()
        );
        this.pubSubConnection = redisClient.connectPubSub();
        this.pubSubConnection.addListener( new PubSubListener( plugin ) );
    }

    @Override
    public void execute( Consumer<RedisClusterCommands<String, String>> consumer )
    {
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            consumer.accept( connection.sync() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public <R> R execute( Function<RedisClusterCommands<String, String>, R> function )
    {
        R result = null;
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            result = function.apply( connection.sync() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void executeAsync( Consumer<RedisClusterAsyncCommands<String, String>> consumer )
    {
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            consumer.accept( connection.async() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public <R> CompletableFuture<R> executeAsync( Function<RedisClusterAsyncCommands<String, String>, CompletableFuture<R>> function )
    {
        CompletableFuture<R> result = null;
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            result = function.apply( connection.async() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void closeConnections()
    {
        pubSubConnection.close();
        pool.close();
        redisClient.shutdown();
    }

    @Override
    public LuaScript loadScript( String script )
    {
        return this.execute( commands ->
        {
            final String hashed = commands.scriptLoad( script );
            return new LuaScript( script, hashed, this );
        } );
    }

    @Override
    public void subscribeToChannels( String... channels )
    {
        this.pubSubConnection.sync().subscribe( channels );
    }

    @Override
    public void publishToChannel( String channel, String message )
    {
        this.pubSubConnection.async().publish( channel, message );
    }
}
