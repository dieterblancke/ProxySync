package be.dieterblancke.proxysync.common.plugin.subscribers;

import be.dieterblancke.proxysync.api.ProxySyncApi;
import be.dieterblancke.proxysync.api.event.redis.RedisMessageEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.function.Consumer;

public class ProxySyncDefaultChannelSubscriber implements Consumer<RedisMessageEvent>
{

    private static final String PROXYSYNC_PREFIX = "proxysync:";
    private static final String BROADCAST_PREFIX = "broadcast-";
    private static final String COMMAND_PREFIX = "command-";
    private static final String MESSAGE_PREFIX = "message-";
    private static final String USER_PREFIX = "user:";

    private final ProxySyncApi proxySyncApi;

    public ProxySyncDefaultChannelSubscriber( ProxySyncApi proxySyncApi )
    {
        this.proxySyncApi = proxySyncApi;
    }

    @Override
    public void accept( final RedisMessageEvent event )
    {
        if ( !event.getChannel().equalsIgnoreCase( "proxysync-all" )
                && !event.getChannel().equalsIgnoreCase( "proxysync-" + proxySyncApi.getProxyManager().getCurrentProxy().getId() ) )
        {
            return;
        }
        final String message = event.getMessage();

        if ( message.startsWith( PROXYSYNC_PREFIX ) )
        {
            final String action = message.replaceFirst( PROXYSYNC_PREFIX, "" );

            if ( action.startsWith( BROADCAST_PREFIX ) )
            {
                this.handleBroadcastMessage( action.replaceFirst( BROADCAST_PREFIX, "" ) );
            }
            else if ( action.startsWith( COMMAND_PREFIX ) )
            {
                this.handleCommandMessage( action.replaceFirst( COMMAND_PREFIX, "" ) );
            }
            else if ( action.startsWith( USER_PREFIX ) )
            {
                this.handleUserMessage( action.replaceFirst( USER_PREFIX, "" ) );
            }
        }
    }

    private void handleBroadcastMessage( final String message )
    {
        final Component component = GsonComponentSerializer.gson().deserialize( message );

        proxySyncApi.getProxyManager().getCurrentProxy().broadcastMessage( component );
    }

    private void handleCommandMessage( final String message )
    {
        proxySyncApi.getProxyManager().getCurrentProxy().executeCommand( message );
    }

    private void handleUserMessage( final String message )
    {
        final String uuid = message.split( ":" )[0];
        final String action = message.replaceFirst( uuid + ":", "" );

        if ( action.startsWith( MESSAGE_PREFIX ) )
        {
            final String jsonMessage = action.replaceFirst( MESSAGE_PREFIX, "" );
            final Component component = GsonComponentSerializer.gson().deserialize( jsonMessage );

            proxySyncApi.getProxyManager().getCurrentProxy().getUser( uuid ).ifPresent( user -> user.sendMessage( component ) );
        }
    }
}
