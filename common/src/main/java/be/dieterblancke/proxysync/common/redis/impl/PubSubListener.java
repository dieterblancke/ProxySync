package be.dieterblancke.proxysync.common.redis.impl;

import be.dieterblancke.proxysync.api.event.redis.RedisMessageEvent;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import io.lettuce.core.pubsub.RedisPubSubAdapter;

public class PubSubListener extends RedisPubSubAdapter<String, String>
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