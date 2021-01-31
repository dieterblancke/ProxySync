package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class VelocityUser implements User
{

    private final ProxySyncPlugin plugin;
    private final Player player;

    public VelocityUser( ProxySyncPlugin plugin, Player player )
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
        return this.player.getUsername();
    }

    @Override
    public String getIp()
    {
        return this.player.getRemoteAddress().toString();
    }

    @Override
    public Proxy getProxy()
    {
        return this.plugin.getCurrentProxy();
    }

    @Override
    public String getServer()
    {
        if ( !this.player.getCurrentServer().isPresent() )
        {
            return null;
        }
        return this.player.getCurrentServer().get().getServerInfo().getName();
    }

    @Override
    public boolean isOnline()
    {
        return this.player != null;
    }

    @Override
    public void sendMessage( Component component )
    {
        this.player.sendMessage( component );
    }
}
