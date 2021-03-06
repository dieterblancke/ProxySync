package be.dieterblancke.proxysync.api.model.proxy;

import be.dieterblancke.proxysync.api.model.user.User;

import java.util.Set;

public interface ProxyManager
{

    /**
     * Get proxy by identifier.
     *
     * @param proxyId of the proxy
     * @return instance of {@link Proxy} or null if not found
     */
    Proxy getProxy( String proxyId );

    /**
     * Get the current EmeraldProxy.
     *
     * @return current proxy or null if not a proxy instance
     */
    Proxy getCurrentProxy();

    /**
     * Get player count of proxy
     *
     * @param proxyId of the proxy
     * @return count of players (0 if no players or proxy not found)
     */
    int getProxyUserCount( String proxyId );

    /**
     * Gets a list of ids of all active proxy instances.
     *
     * @return list of active proxy ids
     */
    Set<String> getActiveProxies();

    /**
     * Gets a list of users that are on this server from all proxy instances.
     *
     * @param serverName the server to get the player list for
     * @return a set of all players on this server
     */
    Set<User> getPlayersOnServer( String serverName );

}
