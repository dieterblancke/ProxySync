package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

public class VelocityUserProvider implements UserProvider<Player, VelocityUser>
{

    private final List<VelocityUser> users;
    private final ProxySyncPlugin plugin;

    public VelocityUserProvider( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
        this.users = Collections.synchronizedList( new ArrayList<>() );
    }

    @Override
    public Optional<VelocityUser> get( final UUID uniqueId )
    {
        for ( VelocityUser user : users )
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
    public Optional<VelocityUser> get( final String userName )
    {
        for ( VelocityUser user : users )
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
    public VelocityUser add( final Player player )
    {
        return this.get( player.getUniqueId() )
                .orElseGet( () ->
                {
                    final VelocityUser user = new VelocityUser( this.plugin, player );

                    users.add( user );

                    return user;
                } );
    }

    @Override
    public VelocityUser remove( final UUID uniqueId )
    {
        final Optional<VelocityUser> optionalUser = this.get( uniqueId );

        if ( optionalUser.isPresent() )
        {
            final VelocityUser velocityUser = optionalUser.get();
            this.users.remove( velocityUser );
            return velocityUser;
        }
        return null;
    }

    @Override
    public Set<VelocityUser> getAll()
    {
        return new HashSet<>( this.users );
    }
}
