package be.dieterblancke.proxysync.common.redis;

public interface PubSubHandler
{
    void handle( String channel, String message );
}
