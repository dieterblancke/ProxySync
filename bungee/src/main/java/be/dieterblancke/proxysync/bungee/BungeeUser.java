package be.dieterblancke.proxysync.bungee;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.UUID;

public class BungeeUser implements User
{

    private final ProxySyncPlugin plugin;
    private final ProxiedPlayer player;

    public BungeeUser( ProxySyncPlugin plugin, ProxiedPlayer player )
    {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public UUID getUniqueId()
    {
        return this.player.getUniqueId();
    }

    @Override
    public String getUsername()
    {
        return this.player.getName();
    }

    @Override
    public String getIp()
    {
        return player.getSocketAddress().toString();
    }

    @Override
    public Proxy getProxy()
    {
        return this.plugin.getCurrentProxy();
    }

    @Override
    public String getServer()
    {
        Server server = this.player.getServer();
        if ( server == null )
        {
            return null;
        }
        return server.getInfo().getName();
    }

    @Override
    public boolean isOnline()
    {
        return this.player != null;
    }

    @Override
    public void sendMessage( Component component )
    {
        ( (ProxySyncBungeePlugin) plugin ).getBungeeAudiences().player( player ).sendMessage(
                Identity.nil(),
                component
        );
    }
}
