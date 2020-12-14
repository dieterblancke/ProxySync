package be.dieterblancke.proxysync.api.event.redis;

import be.dieterblancke.proxysync.api.event.ProxySyncEvent;

public class RedisMessageEvent implements ProxySyncEvent
{

    private final String channel;
    private final String message;

    public RedisMessageEvent( String channel, String message )
    {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel()
    {
        return channel;
    }

    public String getMessage()
    {
        return message;
    }

}
