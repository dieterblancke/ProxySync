package be.dieterblancke.proxysync.common.plugin;

import be.dieterblancke.proxysync.api.ProxySyncApi;
import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.config.Configuration;
import be.dieterblancke.proxysync.common.event.AbstractEventBus;
import be.dieterblancke.proxysync.common.plugin.bootstrap.ProxySyncBootstrap;
import be.dieterblancke.proxysync.common.plugin.logging.PluginLogger;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerAdapter;
import be.dieterblancke.proxysync.common.redis.RedisDataManager;
import be.dieterblancke.proxysync.common.redis.RedisManager;

public interface ProxySyncPlugin
{

    ProxySyncBootstrap getBootstrap();

    PluginLogger getLogger();

    Configuration getConfiguration();

    Proxy getCurrentProxy();

    SchedulerAdapter getSchedulerAdapter();

    RedisManager getRedisManager();

    RedisDataManager getRedisDataManager();

    <I, T extends User> UserProvider<I, T> getUserProvider();

    ProxySyncApi getApi();

    AbstractEventBus<?> getEventBus();

}

