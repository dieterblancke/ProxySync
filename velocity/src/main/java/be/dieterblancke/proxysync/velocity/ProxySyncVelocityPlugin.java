package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.event.AbstractEventBus;
import be.dieterblancke.proxysync.common.plugin.AbstractProxySyncPlugin;
import com.velocitypowered.api.proxy.Player;

public class ProxySyncVelocityPlugin extends AbstractProxySyncPlugin {

    private final ProxySyncVelocityBootstrap bootstrap;
    private final VelocityProxy currentProxy;
    private final VelocityUserProvider userProvider;
    private final VelocityEventBus eventBus;

    public ProxySyncVelocityPlugin(ProxySyncVelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.currentProxy = new VelocityProxy(this, bootstrap.getProxyServer());
        this.userProvider = new VelocityUserProvider(this);
        this.eventBus = new VelocityEventBus(this);
    }

    @Override
    public void registerPlatformListeners() {
        this.bootstrap.getProxyServer().getEventManager().register(this.bootstrap, new PlayerListener(this));
    }

    @Override
    public ProxySyncVelocityBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Proxy getCurrentProxy() {
        return this.currentProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserProvider<Player, VelocityUser> getUserProvider() {
        return this.userProvider;
    }

    @Override
    public AbstractEventBus<?> getEventBus() {
        return this.eventBus;
    }
}
