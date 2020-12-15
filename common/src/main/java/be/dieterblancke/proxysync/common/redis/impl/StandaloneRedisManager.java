package be.dieterblancke.proxysync.common.redis.impl;

import be.dieterblancke.proxysync.api.event.redis.RedisMessageEvent;
import be.dieterblancke.proxysync.common.config.RedisConfiguration;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import be.dieterblancke.proxysync.common.redis.LuaScript;
import be.dieterblancke.proxysync.common.redis.RedisManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class StandaloneRedisManager implements RedisManager
{
    private final RedisClient redisClient;
    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    public StandaloneRedisManager( ProxySyncPlugin plugin, RedisConfiguration configuration ) throws RuntimeException
    {
        Optional<RedisURI> connectionUri = configuration.getRedisURIs().stream().findFirst();
        if ( !connectionUri.isPresent() )
        {
            throw new IllegalStateException( "No redis connection details found, at least one is required." );
        }

        RedisURI redisURI = connectionUri.get();
        this.redisClient = RedisClient.create( redisURI );

        StatefulRedisConnection<String, String> connection = null;
        try
        {
            connection = this.redisClient.connect();
            String info = connection.sync().info();
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

            String[] versionNumbers = version.split( "\\." );
            int major = Integer.parseInt( versionNumbers[0] );
            int minor = Integer.parseInt( versionNumbers[1] );
            if ( major < 2 || ( major == 2 && minor < 6 ) )
            {
                throw new RuntimeException( "Lua is not supported on redis versions below 2.6. Please update your redis implementation." );
            }
        }
        catch ( RedisConnectionException exception )
        {
            throw new IllegalStateException( "Unable to connect to redis! " + redisURI.getHost() + ":" + redisURI.getPort() );
        }
        finally
        {
            if ( connection != null && connection.isOpen() )
            {
                connection.close();
            }
        }

        this.pool = ConnectionPoolSupport.createGenericObjectPool( redisClient::connect, new GenericObjectPoolConfig<>() );
        this.pubSubConnection = redisClient.connectPubSub();
        this.pubSubConnection.addListener( new PubSubListener( plugin ) );
    }

    @Override
    public void execute( Consumer<RedisCommands<String, String>> consumer )
    {
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
        {
            consumer.accept( connection.sync() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public <R> R execute( Function<RedisCommands<String, String>, R> function )
    {
        R result = null;
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
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
    public void executeAsync( Consumer<RedisAsyncCommands<String, String>> consumer )
    {
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
        {
            consumer.accept( connection.async() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public <R> CompletableFuture<R> executeAsync( Function<RedisAsyncCommands<String, String>, CompletableFuture<R>> function )
    {
        CompletableFuture<R> result = null;
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
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

    private static class PubSubListener extends RedisPubSubAdapter<String, String>
    {

        private final ProxySyncPlugin plugin;

        public PubSubListener( ProxySyncPlugin plugin )
        {
            this.plugin = plugin;
        }

        @Override
        public void message( String channel, String message )
        {
            this.plugin.getEventBus().post( new RedisMessageEvent( channel, message ) );
        }
    }
}
