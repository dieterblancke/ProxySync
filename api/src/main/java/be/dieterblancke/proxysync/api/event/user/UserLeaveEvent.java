package be.dieterblancke.proxysync.api.event.user;

import be.dieterblancke.proxysync.api.event.ProxySyncEvent;
import be.dieterblancke.proxysync.api.model.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserLeaveEvent implements ProxySyncEvent
{

    private final User user;

    public UserLeaveEvent(@NonNull User user) {
        this.user = user;
    }

    public @NonNull User getUser() {
        return user;
    }

}
