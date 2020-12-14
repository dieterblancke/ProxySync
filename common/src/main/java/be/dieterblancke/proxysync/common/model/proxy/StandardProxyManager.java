package be.dieterblancke.proxysync.common.model.proxy;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.proxy.ProxyManager;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;

import java.util.Set;

public class StandardProxyManager implements ProxyManager {

    private final ProxySyncPlugin plugin;

    public StandardProxyManager(ProxySyncPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Proxy getProxy(String proxyId) {
        if (this.plugin.getCurrentProxy().getId().equals(proxyId)) {
            return this.plugin.getCurrentProxy();
        }
        return new SyncedProxy(proxyId, this.plugin.getRedisDataManager());
    }

    @Override
    public Proxy getCurrentProxy() {
        return this.plugin.getCurrentProxy();
    }

    @Override
    public int getProxyUserCount(String proxyId) {
        return this.plugin.getRedisDataManager().getUserCountOfProxy(proxyId);
    }

    @Override
    public Set<String> getActiveProxies() {
        return this.plugin.getRedisDataManager().getActiveProxies();
    }
}
