package be.dieterblancke.proxysync.common.model.user;

import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.api.model.user.UserManager;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;

import java.util.UUID;

public class StandardUserManager implements UserManager
{

    private final ProxySyncPlugin plugin;

    public StandardUserManager( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
    }

    @Override
    public User getUser( UUID uniqueId )
    {
        if ( this.plugin.getCurrentProxy().hasUser( uniqueId ) )
        {
            return this.plugin.getCurrentProxy().getUser( uniqueId );
        }
        return new BridgedUser( uniqueId, this.plugin.getRedisDataManager() );
    }

    @Override
    public User getUser( String username )
    {
        return null; // todo
    }

    @Override
    public boolean isUserOnline( UUID uniqueId )
    {
        if ( this.plugin.getCurrentProxy().hasUser( uniqueId ) )
        {
            return true;
        }
        return this.plugin.getRedisDataManager().isPlayerOnline( uniqueId );
    }
}
