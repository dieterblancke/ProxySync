package be.dieterblancke.proxysync.bungee;

import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class BungeeUserProvider implements UserProvider<ProxiedPlayer, BungeeUser>
{

    private final List<BungeeUser> users;
    private final ProxySyncPlugin plugin;

    public BungeeUserProvider( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
        this.users = Collections.synchronizedList( new ArrayList<>() );
    }

    @Override
    public Optional<BungeeUser> get( final UUID uniqueId )
    {
        for ( BungeeUser user : users )
        {
            if ( user.getUniqueId().equals( uniqueId ) )
            {
                return Optional.of( user );
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean has( final UUID uniqueId )
    {
        return this.get( uniqueId ).isPresent();
    }

    @Override
    public Optional<BungeeUser> get( final String userName )
    {
        for ( BungeeUser user : users )
        {
            if ( user.getUsername().equals( userName ) )
            {
                return Optional.of( user );
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean has( final String userName )
    {
        return this.get( userName ).isPresent();
    }

    @Override
    public BungeeUser add( final ProxiedPlayer player )
    {
        return this.get( player.getUniqueId() )
                .orElseGet( () ->
                {
                    final BungeeUser user = new BungeeUser( this.plugin, player );

                    users.add( user );

                    return user;
                } );
    }

    @Override
    public BungeeUser remove( final UUID uniqueId )
    {
        final Optional<BungeeUser> optionalUser = this.get( uniqueId );

        if ( optionalUser.isPresent() )
        {
            final BungeeUser bungeeUser = optionalUser.get();
            this.users.remove( bungeeUser );
            return bungeeUser;
        }
        return null;
    }

    @Override
    public Set<BungeeUser> getAll()
    {
        return new HashSet<>( this.users );
    }
}
