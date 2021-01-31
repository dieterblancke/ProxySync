package be.dieterblancke.proxysync.common.model.user;

import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.api.model.user.UserManager;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StandardUserManager implements UserManager
{

    private final ProxySyncPlugin plugin;

    public StandardUserManager( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
    }

    @Override
    public User getUser( final UUID uniqueId )
    {
        return this.plugin.getCurrentProxy().getUser( uniqueId )
                .orElseGet( () -> new BridgedUser( uniqueId, this.plugin.getRedisDataManager() ) );
    }

    @Override
    public User getUser( final String username )
    {
        return this.plugin.getCurrentProxy().getUser( username )
                .orElseGet( () -> new BridgedUser(
                        this.plugin.getRedisDataManager().getPlayerUuid( username ),
                        username,
                        this.plugin.getRedisDataManager()
                ) );
    }

    @Override
    public boolean isUserOnline( final UUID uniqueId )
    {
        if ( this.plugin.getCurrentProxy().hasUser( uniqueId ) )
        {
            return true;
        }
        return this.plugin.getRedisDataManager().isPlayerOnline( uniqueId );
    }

    @Override
    public Set<User> getOnlineUsers()
    {
        final Set<User> users = new HashSet<>();
        final Set<String> proxies = this.plugin.getRedisDataManager().getActiveProxies();

        for ( String proxy : proxies )
        {
            users.addAll( this.plugin.getRedisDataManager().getUsersOfProxy( proxy ) );
        }

        return users;
    }

    @Override
    public UUID getUuidFromUsername( final String userName )
    {
        return this.plugin.getRedisDataManager().getPlayerUuid( userName );
    }
}
