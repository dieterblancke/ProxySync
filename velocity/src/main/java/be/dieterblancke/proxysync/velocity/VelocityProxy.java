package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class VelocityProxy implements Proxy
{

    private final ProxySyncPlugin plugin;
    private final ProxyServer proxyServer;

    public VelocityProxy( final ProxySyncPlugin plugin, final ProxyServer proxyServer )
    {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
    }

    @Override
    public String getId()
    {
        return this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
    }

    @Override
    public Set<User> getUsers()
    {
        return this.plugin.getUserProvider().getAll();
    }

    @Override
    public Optional<User> getUser( final UUID uniqueId )
    {
        return this.plugin.getUserProvider().get( uniqueId );
    }

    @Override
    public Optional<User> getUser( final String userName )
    {
        return this.plugin.getUserProvider().get( userName );
    }

    @Override
    public boolean hasUser( final UUID uniqueId )
    {
        return this.plugin.getUserProvider().has( uniqueId );
    }

    @Override
    public boolean hasUser( final String userName )
    {
        return this.plugin.getUserProvider().has( userName );
    }

    @Override
    public void broadcastMessage( final Component component )
    {
        this.proxyServer.sendMessage( component );
    }

    @Override
    public void executeCommand( final String... command )
    {
        this.proxyServer.getCommandManager().executeAsync( ProxySyncCommandSource.instance, String.join( " ", command ) );
    }
}
