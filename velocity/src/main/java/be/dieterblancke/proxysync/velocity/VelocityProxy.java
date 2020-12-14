package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.Component;

import java.util.Set;
import java.util.UUID;

public class VelocityProxy implements Proxy
{

    private final ProxySyncPlugin plugin;
    private final ProxyServer proxyServer;

    public VelocityProxy( ProxySyncPlugin plugin, ProxyServer proxyServer )
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
    public User getUser( UUID uniqueId )
    {
        return this.plugin.getUserProvider().get( uniqueId );
    }

    @Override
    public boolean hasUser( UUID uniqueId )
    {
        return this.plugin.getUserProvider().has( uniqueId );
    }

    @Override
    public void broadcastMessage( Component component )
    {
        this.proxyServer.broadcast( component );
    }

    @Override
    public void executeCommand( String... command )
    {
        this.proxyServer.getCommandManager().execute( ProxySyncCommandSource.instance, String.join( " ", command ) );
    }
}
