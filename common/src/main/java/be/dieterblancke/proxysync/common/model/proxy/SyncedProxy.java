package be.dieterblancke.proxysync.common.model.proxy;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.redis.RedisDataManager;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SyncedProxy implements Proxy
{

    private final String id;
    private final RedisDataManager redisDataManager;

    private Set<User> users;

    public SyncedProxy( final String id, final RedisDataManager redisDataManager )
    {
        this.id = id;
        this.redisDataManager = redisDataManager;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public Set<User> getUsers()
    {
        if ( this.users == null )
        {
            this.users = this.redisDataManager.getUsersOfProxy( this.id );
        }
        return this.users;
    }

    @Override
    public Optional<User> getUser( final UUID uniqueId )
    {
        if ( !hasUser( uniqueId ) )
        {
            return Optional.empty();
        }
        return this.getUsers().stream().filter( player -> player.getUniqueId() == uniqueId ).findFirst();
    }

    @Override
    public Optional<User> getUser( final String userName )
    {
        if ( !hasUser( userName ) )
        {
            return Optional.empty();
        }
        return this.getUsers().stream().filter( player -> player.getUsername().equals( userName ) ).findFirst();
    }

    @Override
    public boolean hasUser( final UUID uniqueId )
    {
        if ( this.users == null )
        {
            return this.redisDataManager.isUserConnectedToProxy( this.id, uniqueId );
        }
        return this.users.stream().anyMatch( player -> player.getUniqueId().equals( uniqueId ) );
    }

    @Override
    public boolean hasUser( final String userName )
    {
        if ( this.users == null )
        {
            return this.redisDataManager.isUserConnectedToProxy( this.id, userName );
        }
        return this.users.stream().anyMatch( player -> player.getUsername().equals( userName ) );
    }

    @Override
    public void broadcastMessage( final Component component )
    {
        this.redisDataManager.broadcastToProxy( this.id, component );
    }

    @Override
    public void executeCommand( final String command )
    {
        this.redisDataManager.executeCommandsOnProxy( this.id, command );
    }
}
