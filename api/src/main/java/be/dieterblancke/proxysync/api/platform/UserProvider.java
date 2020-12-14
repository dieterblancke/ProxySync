package be.dieterblancke.proxysync.api.platform;

import be.dieterblancke.proxysync.api.model.user.User;

import java.util.Set;
import java.util.UUID;

public interface UserProvider<I, T extends User> {

    T get(UUID uniqueId);

    boolean has(UUID uniqueId);

    T add(I player);

    T remove(UUID uniqueId);

    Set<T> getAll();
}
