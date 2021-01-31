package be.dieterblancke.proxysync.bungee;

import be.dieterblancke.proxysync.api.platform.PlatformType;
import be.dieterblancke.proxysync.common.plugin.bootstrap.ProxySyncBootstrap;
import be.dieterblancke.proxysync.common.plugin.logging.JavaLoggerWrapper;
import be.dieterblancke.proxysync.common.plugin.logging.PluginLogger;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerAdapter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.InputStream;
import java.nio.file.Path;

public class ProxySyncBungeeBootstrap extends Plugin implements ProxySyncBootstrap
{

    private final PluginLogger logger;
    private final SchedulerAdapter schedulerAdapter;
    private final ProxySyncBungeePlugin plugin;

    public ProxySyncBungeeBootstrap()
    {
        this.logger = new JavaLoggerWrapper( this.getLogger() );
        this.schedulerAdapter = new BungeeSchedulerAdapter( this );
        this.plugin = new ProxySyncBungeePlugin( this );
    }

    @Override
    public void onEnable()
    {
        this.plugin.enable();
    }

    @Override
    public void onDisable()
    {
        this.plugin.disable();
    }

    public PluginLogger getPluginLogger()
    {
        return this.logger;
    }

    public SchedulerAdapter getSchedulerAdapter()
    {
        return this.schedulerAdapter;
    }

    public PlatformType getPlatformType()
    {
        return PlatformType.BUNGEECORD;
    }

    public Path getDataDirectory()
    {
        return super.getDataFolder().toPath();
    }

    public InputStream getResourceStream( String path )
    {
        return getClass().getResourceAsStream( path );
    }

}
