package be.dieterblancke.proxysync.common.redis.impl;

import be.dieterblancke.proxysync.common.config.RedisConfiguration;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import be.dieterblancke.proxysync.common.redis.RedisManager;

public final class RedisManagerFactory
{

    private RedisManagerFactory()
    {
    }

    public static RedisManager create( ProxySyncPlugin plugin )
    {
        RedisConfiguration redisConfiguration = plugin.getConfiguration().getRedisConfiguration();
        if ( redisConfiguration.getRedisURIs().size() > 1 )
        {
            return null; // cluster
        }

        return new StandaloneRedisManager( plugin, redisConfiguration );
    }
}
