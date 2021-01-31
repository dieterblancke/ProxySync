package be.dieterblancke.proxysync.api.model.user;

import java.util.Set;
import java.util.UUID;

public interface UserManager
{
    /**
     * Get player by id.
     *
     * @param uniqueId of the user to retrieve
     * @return user or null when not found or online
     */
    User getUser( UUID uniqueId );

    /**
     * Get user by username.
     *
     * @param username of the user to retrieve
     * @return user or null when not found or online
     */
    User getUser( String username );

    /**
     * Check if a user is online.
     *
     * @param uniqueId of the user
     * @return true if user is online
     */
    boolean isUserOnline( UUID uniqueId );

    /**
     * Get all users that are online in the network
     *
     * @return a set of all online users
     */
    Set<User> getOnlineUsers();

    /**
     * Get the uuid that is mapped to a certain username
     *
     * @param userName the username to search the uuid for
     * @return the uuid that is related to this username, null if none is found.
     */
    UUID getUuidFromUsername( String userName );
}
