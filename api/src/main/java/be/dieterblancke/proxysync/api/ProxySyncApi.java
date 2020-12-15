package be.dieterblancke.proxysync.api;

import be.dieterblancke.proxysync.api.event.redis.RedisMessageEvent;
import be.dieterblancke.proxysync.api.model.proxy.ProxyManager;
import be.dieterblancke.proxysync.api.model.user.UserManager;
import net.kyori.adventure.text.Component;

public interface ProxySyncApi
{

    /**
     * Get total player count.
     *
     * @return combined player count of all proxies
     */
    int getTotalUserCount();

    /**
     * Gets the UserManager.
     *
     * @return UserManager
     */
    UserManager getUserManager();

    /**
     * Gets the ProxyManager.
     *
     * @return ProxyManager.
     */
    ProxyManager getProxyManager();

    /**
     * Broadcast a message to all proxies.
     *
     * @param component to send
     */
    void broadcastToAllProxies( Component component );

    /**
     * Subscribe to specific redis channels to trigger a {@link RedisMessageEvent} for.
     *
     * @param channels to subscribe to
     */
    void subscribeToChannels( String... channels );

    /**
     * Send a message over a specific redis channel.
     *
     * @param channel to send the message over
     * @param message to send
     */
    void publishToChannel( String channel, String message );

}
