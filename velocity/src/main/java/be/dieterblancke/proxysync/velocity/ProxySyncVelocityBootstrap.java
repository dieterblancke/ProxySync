package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.platform.PlatformType;
import be.dieterblancke.proxysync.common.plugin.bootstrap.ProxySyncBootstrap;
import be.dieterblancke.proxysync.common.plugin.logging.PluginLogger;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerAdapter;
import be.dieterblancke.proxysync.velocity.logging.Slf4jLoggerWrapper;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

@Plugin(
        id = "proxysync",
        name = "ProxySync",
        version = "@Version@",
        authors = { "goofydev", "didjee2" }
)
public class ProxySyncVelocityBootstrap implements ProxySyncBootstrap
{

    private final PluginLogger logger;
    private final ProxyServer proxyServer;
    private final Path dataDirectory;

    private final ProxySyncVelocityPlugin plugin;
    private final VelocitySchedulerAdapter schedulerAdapter;

    @Inject
    public ProxySyncVelocityBootstrap( Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxyServer )
    {
        this.logger = new Slf4jLoggerWrapper( logger );
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
        this.plugin = new ProxySyncVelocityPlugin( this );
        this.schedulerAdapter = new VelocitySchedulerAdapter( this );
    }

    @Subscribe
    public void onEnable( ProxyInitializeEvent event )
    {
        this.plugin.enable();
    }

    @Subscribe
    public void onDisable( ProxyShutdownEvent event )
    {
        this.plugin.disable();
    }

    @Override
    public PluginLogger getPluginLogger()
    {
        return this.logger;
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter()
    {
        return this.schedulerAdapter;
    }

    @Override
    public PlatformType getPlatformType()
    {
        return PlatformType.VELOCITY;
    }

    @Override
    public Path getDataDirectory()
    {
        return this.dataDirectory;
    }

    @Override
    public InputStream getResourceStream( String path )
    {
        return getClass().getResourceAsStream( path );
    }

    public ProxyServer getProxyServer()
    {
        return proxyServer;
    }
}
