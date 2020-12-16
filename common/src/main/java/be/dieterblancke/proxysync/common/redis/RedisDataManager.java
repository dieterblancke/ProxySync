package be.dieterblancke.proxysync.common.redis;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.model.proxy.SyncedProxy;
import be.dieterblancke.proxysync.common.model.user.BridgedUser;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import be.dieterblancke.proxysync.common.util.StreamUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisDataManager
{

    public static final String PREFIX_PROXY_ONLINE = "proxy:online:";
    public static final String PREFIX_PROXY_HEARTBEAT = "proxy:heartbeats:";
    public static final String PREFIX_USER = "user:";
    public static final String PREFIX_UUID = "uuid:";

    public static final String FIELD_USER_IP = "ip";
    public static final String FIELD_USER_NAME = "username";
    public static final String FIELD_USER_PROXY = "proxy";
    public static final String FIELD_USER_SERVER = "server";

    private final ProxySyncPlugin plugin;
    private final RedisManager redisManager;

    private final Cache<UUID, String> playerProxyCache;
    private final Cache<UUID, String> playerNameCache;
    private final Cache<String, UUID> playerUuidCache;
    private final Cache<UUID, String> playerIpCache;

    private LuaScript getTotalPlayerCountScript;
    private LuaScript getActiveProxies;

    public RedisDataManager( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
        this.redisManager = plugin.getRedisManager();

        this.playerProxyCache = CacheBuilder.newBuilder()
                .maximumSize( 5000 )
                .expireAfterAccess( 30, TimeUnit.MINUTES )
                .build();

        this.playerNameCache = CacheBuilder.newBuilder()
                .maximumSize( 5000 )
                .expireAfterAccess( 30, TimeUnit.MINUTES )
                .build();

        this.playerUuidCache = CacheBuilder.newBuilder()
                .maximumSize( 5000 )
                .expireAfterAccess( 30, TimeUnit.MINUTES )
                .build();

        this.playerIpCache = CacheBuilder.newBuilder()
                .maximumSize( 5000 )
                .expireAfterAccess( 30, TimeUnit.MINUTES )
                .build();

        registerScripts();

        // Force cleaning caches every 5 minutes
        plugin.getSchedulerAdapter().asyncRepeating( this::cleanupCaches, 5, TimeUnit.MINUTES );
    }

    private void registerScripts()
    {
        this.getTotalPlayerCountScript = registerScript( "/lua/get_total_player_count.lua" );
        this.getActiveProxies = registerScript( "/lua/get_active_proxies.lua" );
    }

    private LuaScript registerScript( String resourcePath )
    {
        final InputStream is = this.plugin.getBootstrap().getResourceStream( resourcePath );
        final String script = StreamUtil.streamToString( is );
        if ( script == null )
        {
            return null;
        }
        return this.redisManager.loadScript( script );
    }

    public void addUser( User user )
    {
        final String key = PREFIX_USER + user.getUniqueId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + user.getProxy().getId();

        this.redisManager.execute( commands ->
        {
            final Map<String, String> playerData = new HashMap<>();
            playerData.put( FIELD_USER_NAME, user.getUsername() );
            playerData.put( FIELD_USER_IP, user.getIp() );
            playerData.put( FIELD_USER_PROXY, user.getProxy().getId() );
            playerData.put( FIELD_USER_SERVER, user.getServer() );
            commands.hset( key, playerData );

            commands.set( PREFIX_UUID + user.getUsername(), user.getUniqueId().toString() );

            commands.sadd( proxyOnlineKey, user.getUniqueId().toString() );
        } );
    }

    public void removeUser( User user )
    {
        final String key = PREFIX_USER + user.getUniqueId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + user.getProxy().getId();

        this.redisManager.execute( commands ->
        {
            commands.hdel( key, FIELD_USER_NAME, FIELD_USER_IP, FIELD_USER_PROXY, FIELD_USER_SERVER );
            commands.srem( proxyOnlineKey, user.getUniqueId().toString() );
        } );
    }

    public String getPlayerServer( UUID id )
    {
        final String userKey = PREFIX_USER + id.toString();
        return this.redisManager.execute( commands ->
        {
            return commands.hget( userKey, FIELD_USER_SERVER );
        } );
    }

    public String getPlayerIp( UUID id )
    {
        try
        {
            return this.playerIpCache.get( id, () -> this.redisManager.execute( commands ->
            {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget( userKey, FIELD_USER_IP );
            } ) );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( "Unable to get player ip for " + id, e );
        }
    }

    public String getPlayerName( final UUID uuid )
    {
        try
        {
            return this.playerNameCache.get( uuid, () -> this.redisManager.execute( commands ->
            {
                final String userKey = PREFIX_USER + uuid.toString();
                return commands.hget( userKey, FIELD_USER_NAME );
            } ) );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( "Unable to get player name for " + uuid, e );
        }
    }

    public UUID getPlayerUuid( final String userName )
    {
        try
        {
            return this.playerUuidCache.get( userName, () -> this.redisManager.execute( commands ->
            {
                final String uuid = commands.get( PREFIX_UUID + userName );
                return uuid == null ? null : UUID.fromString( uuid );
            } ) );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( "Unable to get player uuid for " + userName, e );
        }
    }

    public Proxy getPlayerProxy( UUID id )
    {
        try
        {
            String playerProxyId = this.playerProxyCache.get( id, () -> this.redisManager.execute( commands ->
            {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget( userKey, FIELD_USER_PROXY );
            } ) );

            return new SyncedProxy( playerProxyId, this );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( "Unable to get proxy for " + id, e );
        }
    }

    public void changeUserServer( User user, String serverName )
    {
        final String key = PREFIX_USER + user.getUniqueId();

        this.redisManager.execute( commands ->
        {
            commands.hset( key, FIELD_USER_SERVER, serverName );
        } );
    }

    public void updateProxyHeartbeat()
    {
        final String key = PREFIX_PROXY_HEARTBEAT + this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
        final long heartbeatInterval = this.plugin.getConfiguration().getProxyConfiguration().getHeartbeatInterval() * 2;

        this.redisManager.execute( commands ->
        {
            commands.setex( key, heartbeatInterval, "1" );
        } );
    }

    public void cleanupProxy()
    {
        final String key = PREFIX_PROXY_HEARTBEAT + this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + this.plugin.getConfiguration().getProxyConfiguration().getProxyId();

        this.redisManager.execute( commands ->
        {
            commands.del( key );
            commands.del( proxyOnlineKey );
        } );
    }

    public Set<User> getUsersOfProxy( String proxyId )
    {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;

        return this.redisManager.execute( commands ->
        {
            return commands.smembers( proxyOnlineKey )
                    .stream()
                    .map( UUID::fromString )
                    .map( id -> new BridgedUser( id, this ) )
                    .collect( Collectors.toSet() );
        } );
    }

    public boolean isUserConnectedToProxy( String proxyId, UUID playerId )
    {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;
        return this.redisManager.execute( commands ->
        {
            return commands.sismember( proxyOnlineKey, playerId.toString() );
        } );
    }

    public boolean isUserConnectedToProxy( String proxyId, String playerName )
    {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;
        return this.redisManager.execute( commands ->
        {
            final UUID uuid = this.getPlayerUuid( playerName );

            if ( uuid == null )
            {
                return false;
            }

            return commands.sismember( proxyOnlineKey, uuid.toString() );
        } );
    }

    public int getUserCountOfProxy( String proxyId )
    {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;
        return this.redisManager.execute( commands ->
        {
            return commands.scard( proxyOnlineKey ).intValue();
        } );
    }

    public int getTotalPlayerCount()
    {
        ArrayList<Long> results = getTotalPlayerCountScript.evalCast();
        if ( results.isEmpty() )
        {
            return -1;
        }
        return results.get( 0 ).intValue();
    }

    public Set<String> getActiveProxies()
    {
        return new HashSet<>( getActiveProxies.evalCast() );
    }

    public boolean isPlayerOnline( UUID playerId )
    {
        final String key = PREFIX_USER + playerId;
        return this.redisManager.execute( commands -> commands.exists( key ) == 1L );
    }

    private void cleanupCaches()
    {
        playerProxyCache.cleanUp();
        playerNameCache.cleanUp();
        playerIpCache.cleanUp();
    }

    public void broadcastToProxy( final String proxyId, final Component component )
    {
        this.redisManager.publishToChannel(
                "proxysync-" + proxyId,
                "proxysync:broadcast-" + GsonComponentSerializer.gson().serialize( component )
        );
    }
}