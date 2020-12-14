package be.dieterblancke.proxysync.bungee;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.event.AbstractEventBus;
import be.dieterblancke.proxysync.common.plugin.AbstractProxySyncPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProxySyncBungeePlugin extends AbstractProxySyncPlugin {

    private final ProxySyncBungeeBootstrap bootstrap;
    private final BungeeProxy currentProxy;
    private final BungeeUserProvider userProvider;
    private final BungeeEventBus eventBus;

    public ProxySyncBungeePlugin(ProxySyncBungeeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.currentProxy = new BungeeProxy(this, this.bootstrap.getProxy());
        this.userProvider = new BungeeUserProvider(this);
        this.eventBus = new BungeeEventBus(this);
    }

    @Override
    public void registerPlatformListeners() {
        this.bootstrap.getProxy().getPluginManager().registerListener(this.bootstrap, new PlayerListener(this));
    }

    @Override
    public ProxySyncBungeeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Proxy getCurrentProxy() {
        return this.currentProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserProvider<ProxiedPlayer, BungeeUser> getUserProvider() {
        return this.userProvider;
    }

    @Override
    public AbstractEventBus<?> getEventBus() {
        return this.eventBus;
    }
}
