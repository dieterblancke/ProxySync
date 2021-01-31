package be.dieterblancke.proxysync.common.plugin;

import be.dieterblancke.proxysync.api.ProxySyncApi;
import be.dieterblancke.proxysync.api.ProxySyncApiProvider;
import be.dieterblancke.proxysync.api.event.redis.RedisMessageEvent;
import be.dieterblancke.proxysync.api.model.proxy.ProxyManager;
import be.dieterblancke.proxysync.api.model.user.UserManager;
import be.dieterblancke.proxysync.common.config.Configuration;
import be.dieterblancke.proxysync.common.model.proxy.StandardProxyManager;
import be.dieterblancke.proxysync.common.model.user.StandardUserManager;
import be.dieterblancke.proxysync.common.plugin.logging.PluginLogger;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerAdapter;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerTask;
import be.dieterblancke.proxysync.common.plugin.subscribers.ProxySyncDefaultChannelSubscriber;
import be.dieterblancke.proxysync.common.redis.RedisDataManager;
import be.dieterblancke.proxysync.common.redis.RedisManager;
import be.dieterblancke.proxysync.common.redis.impl.RedisManagerFactory;
import be.dieterblancke.proxysync.common.tasks.HeartbeatTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.concurrent.TimeUnit;

public abstract class AbstractProxySyncPlugin implements ProxySyncPlugin, ProxySyncApi
{

    private Configuration configuration;

    private RedisManager redisManager;
    private RedisDataManager redisDataManager;

    private UserManager userManager;
    private ProxyManager proxyManager;

    private SchedulerTask heartbeatTask;

    public final void enable()
    {
        // load configuration
        getLogger().info( "Loading configuration..." );
        this.configuration = Configuration.load( this );

        // redis connection
        this.redisManager = RedisManagerFactory.create( this );
        this.redisDataManager = new RedisDataManager( this );
        this.subscribeToChannels(
                "proxysync-all",
                "proxysync-" + getConfiguration().getProxyConfiguration().getProxyId()
        );

        // managers
        this.userManager = new StandardUserManager( this );
        this.proxyManager = new StandardProxyManager( this );

        ProxySyncApiProvider.setApiInstance( this );

        // tasks
        int heartbeatInterval = this.getConfiguration().getProxyConfiguration().getHeartbeatInterval();
        this.heartbeatTask = this.getSchedulerAdapter().asyncRepeating( new HeartbeatTask( this ), heartbeatInterval, TimeUnit.SECONDS );

        // platform listeners
        this.registerPlatformListeners();
        this.getEventBus().subscribe( RedisMessageEvent.class, new ProxySyncDefaultChannelSubscriber( this ) );

        getLogger().info( "Successfully enabled." );
    }

    public final void disable()
    {
        getLogger().info( "Starting shutdown process..." );

        // tasks
        this.heartbeatTask.cancel();
        this.getSchedulerAdapter().shutdown();

        // event bus
        this.getEventBus().close();

        // redis connection
        this.redisDataManager.cleanupProxy();
        this.redisManager.closeConnections();

        getLogger().info( "Goodbye!" );
    }

    public abstract void registerPlatformListeners();

    @Override
    public Configuration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public PluginLogger getLogger()
    {
        return getBootstrap().getPluginLogger();
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter()
    {
        return getBootstrap().getSchedulerAdapter();
    }

    @Override
    public RedisManager getRedisManager()
    {
        return this.redisManager;
    }

    @Override
    public RedisDataManager getRedisDataManager()
    {
        return this.redisDataManager;
    }

    @Override
    public UserManager getUserManager()
    {
        return this.userManager;
    }

    @Override
    public ProxyManager getProxyManager()
    {
        return this.proxyManager;
    }

    @Override
    public int getTotalUserCount()
    {
        return this.redisDataManager.getTotalPlayerCount();
    }

    @Override
    public ProxySyncApi getApi()
    {
        return this;
    }

    @Override
    public void broadcastToAllProxies( Component component )
    {
        final String content = GsonComponentSerializer.gson().serialize( component );
        if ( content.isEmpty() )
        {
            return;
        }
        this.publishToChannel( "proxysync-all", "proxysync:broadcast-" + content );
    }

    @Override
    public void subscribeToChannels( String... channels )
    {
        this.getRedisManager().subscribeToChannels( channels );
    }

    @Override
    public void publishToChannel( String channel, String message )
    {
        this.getRedisManager().publishToChannel( channel, message );
    }
}
