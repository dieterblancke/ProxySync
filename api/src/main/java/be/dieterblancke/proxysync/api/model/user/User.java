package be.dieterblancke.proxysync.api.model.user;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface User
{
    /**
     * Gets the unique id of the user.
     *
     * @return uuid
     */
    UUID getUniqueId();

    /**
     * Gets the name of the user.
     *
     * @return name
     */
    String getUsername();

    /**
     * Gets the ip which the user is currently connected from.
     *
     * @return ip
     */
    String getIp();

    /**
     * Gets the proxy the user is connected to.
     *
     * @return proxy
     */
    Proxy getProxy();

    /**
     * Get the name of the server the user is connected to.
     *
     * @return server name
     */
    String getServer();

    /**
     * Checks if the user is online. This should usually return true, but it's best to check anyways.
     *
     * @return true if online, false if offline
     */
    boolean isOnline();

    /**
     * Send a message to the user.
     *
     * @param component message to send
     */
    void sendMessage( Component component );
}
