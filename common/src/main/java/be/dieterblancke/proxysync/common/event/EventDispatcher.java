package be.dieterblancke.proxysync.common.event;

import be.dieterblancke.proxysync.api.event.ProxySyncEvent;
import be.dieterblancke.proxysync.api.event.user.UserJoinEvent;
import be.dieterblancke.proxysync.api.event.user.UserLeaveEvent;
import be.dieterblancke.proxysync.api.model.user.User;

public class EventDispatcher {

    private final AbstractEventBus eventBus;

    public EventDispatcher(AbstractEventBus eventBus) {
        this.eventBus = eventBus;

    }

    private boolean shouldPost(Class<? extends ProxySyncEvent> eventClass) {
        return this.eventBus.shouldPost(eventClass);
    }

    private void post(ProxySyncEvent event) {
        if (this.shouldPost(event.getClass())) {
            this.eventBus.post(event);
        }
    }

    public void dispatchUserJoin(User user) {
        post(new UserJoinEvent(user));
    }

    public void dispatchUserLeave(User user) {
        post(new UserLeaveEvent(user));
    }

}
