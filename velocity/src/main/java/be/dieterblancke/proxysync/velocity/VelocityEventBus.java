package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.common.event.AbstractEventBus;
import com.velocitypowered.api.plugin.PluginContainer;

public class VelocityEventBus extends AbstractEventBus<PluginContainer>
{

    private final ProxySyncVelocityBootstrap bootstrap;

    public VelocityEventBus( ProxySyncVelocityPlugin plugin )
    {
        super( plugin );
        this.bootstrap = plugin.getBootstrap();
    }

    @Override
    public PluginContainer checkPlugin( Object plugin ) throws IllegalArgumentException
    {
        if ( plugin instanceof PluginContainer )
        {
            return (PluginContainer) plugin;
        }

        PluginContainer pluginContainer = this.bootstrap.getProxyServer().getPluginManager().fromInstance( plugin ).orElse( null );
        if ( pluginContainer != null )
        {
            return pluginContainer;
        }

        throw new IllegalArgumentException( "Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin." );
    }
}
