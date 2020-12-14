package be.dieterblancke.proxysync.api.event;

import java.util.function.Consumer;

public interface EventSubscription<T extends ProxySyncEvent> extends AutoCloseable {
    Class<T> getEventClass();

    boolean isActive();

    void close();

    Consumer<? super T> getHandler();
}
