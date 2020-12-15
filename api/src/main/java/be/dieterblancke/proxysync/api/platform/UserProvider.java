package be.dieterblancke.proxysync.api.platform;

import be.dieterblancke.proxysync.api.model.user.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserProvider<I, T extends User>
{

    Optional<T> get( UUID uniqueId );

    boolean has( UUID uniqueId );

    Optional<T> get( String userName );

    boolean has( String userName );

    T add( I player );

    T remove( UUID uniqueId );

    Set<T> getAll();
}
