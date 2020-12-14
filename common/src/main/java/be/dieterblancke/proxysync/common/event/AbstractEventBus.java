package be.dieterblancke.proxysync.common.event;

import be.dieterblancke.proxysync.api.event.EventBus;
import be.dieterblancke.proxysync.api.event.EventSubscription;
import be.dieterblancke.proxysync.api.event.ProxySyncEvent;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractEventBus<P> implements EventBus, AutoCloseable
{

    private final ProxySyncPlugin plugin;
    private final Bus bus = new Bus();

    public AbstractEventBus( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
    }

    public abstract P checkPlugin( Object plugin ) throws IllegalArgumentException;

    public void post( ProxySyncEvent event )
    {
        this.bus.post( event );
    }

    public boolean shouldPost( Class<? extends ProxySyncEvent> eventClass )
    {
        return this.bus.hasSubscribers( eventClass );
    }

    @Override
    public @NonNull <T extends ProxySyncEvent> EventSubscription<T> subscribe( @NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler )
    {
        Objects.requireNonNull( eventClass, "eventClass" );
        Objects.requireNonNull( handler, "handler" );
        return registerSubscription( eventClass, handler, null );
    }

    @Override
    public @NonNull <T extends ProxySyncEvent> EventSubscription<T> subscribe( Object plugin, @NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler )
    {
        Objects.requireNonNull( plugin, "plugin" );
        Objects.requireNonNull( eventClass, "eventClass" );
        Objects.requireNonNull( handler, "handler" );
        return registerSubscription( eventClass, handler, checkPlugin( plugin ) );
    }

    @Override
    public @NonNull <T extends ProxySyncEvent> Set<EventSubscription<T>> getSubscriptions( @NonNull Class<T> eventClass )
    {
        return this.bus.getHandlers( eventClass );
    }

    private <T extends ProxySyncEvent> EventSubscription<T> registerSubscription( Class<T> eventClass, Consumer<? super T> handler, Object plugin )
    {
        if ( !ProxySyncEvent.class.isAssignableFrom( eventClass ) )
        {
            throw new IllegalArgumentException( "class " + eventClass.getName() + " does not implement LuckPermsEvent" );
        }

        ProxySyncEventSubscription<T> eventHandler = new ProxySyncEventSubscription<>( this, eventClass, handler, plugin );
        this.bus.register( eventClass, eventHandler );
        return eventHandler;
    }

    /**
     * Removes a specific handler from the bus
     *
     * @param handler the handler to remove
     */
    public void unregisterHandler( ProxySyncEventSubscription<?> handler )
    {
        this.bus.unregister( handler );
    }

    /**
     * Removes all handlers for a specific plugin
     *
     * @param plugin the plugin
     */
    public void unregisterHandlers( P plugin )
    {
        this.bus.unregister( sub -> ( (ProxySyncEventSubscription<?>) sub ).getPlugin() == plugin );
    }

    public ProxySyncPlugin getPlugin()
    {
        return plugin;
    }

    @Override
    public void close()
    {
        this.bus.unregisterAll();
    }

    private static final class Bus extends SimpleEventBus<ProxySyncEvent>
    {
        Bus()
        {
            super( ProxySyncEvent.class );
        }

        @Override
        protected boolean shouldPost( @NonNull ProxySyncEvent event, @NonNull EventSubscriber<?> subscriber )
        {
            return true;
        }

        public <T extends ProxySyncEvent> Set<EventSubscription<T>> getHandlers( Class<T> eventClass )
        {
            //noinspection unchecked
            return super.subscribers().values().stream()
                    .filter( s -> s instanceof EventSubscription && ( (EventSubscription<?>) s ).getEventClass().isAssignableFrom( eventClass ) )
                    .map( s -> ( (EventSubscription<T>) s ) )
                    .collect( Collectors.toSet() );
        }
    }
}
