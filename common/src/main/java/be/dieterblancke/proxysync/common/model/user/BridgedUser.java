package be.dieterblancke.proxysync.common.model.user;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.redis.RedisDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.UUID;

public class BridgedUser implements User
{

    private final UUID uniqueId;
    private final RedisDataManager redisDataManager;

    private String userName;
    private String ip;
    private Proxy proxy;
    private String server;

    public BridgedUser( final UUID uniqueId, final RedisDataManager redisDataManager )
    {
        this.uniqueId = uniqueId;
        this.redisDataManager = redisDataManager;
    }

    public BridgedUser( final UUID uniqueId, final String userName, final RedisDataManager redisDataManager )
    {
        this( uniqueId, redisDataManager );
        this.userName = userName;
    }

    @Override
    public UUID getUniqueId()
    {
        return this.uniqueId;
    }

    @Override
    public String getUsername()
    {
        if ( this.userName == null )
        {
            this.userName = this.redisDataManager.getPlayerName( this.uniqueId );
        }
        return this.userName;
    }

    @Override
    public String getIp()
    {
        if ( this.ip == null )
        {
            this.ip = this.redisDataManager.getPlayerIp( this.uniqueId );
        }
        return this.ip;
    }

    @Override
    public Proxy getProxy()
    {
        if ( this.proxy == null )
        {
            this.proxy = this.redisDataManager.getPlayerProxy( this.uniqueId );
        }
        return this.proxy;
    }

    @Override
    public String getServer()
    {
        if ( this.server == null )
        {
            this.server = this.redisDataManager.getPlayerServer( this.uniqueId );
        }
        return this.server;
    }

    @Override
    public boolean isOnline()
    {
        return this.uniqueId != null && this.redisDataManager.isPlayerOnline( this.uniqueId );
    }

    @Override
    public void sendMessage( Component component )
    {
        final String content = GsonComponentSerializer.gson().serialize( component );
        if ( content.isEmpty() )
        {
            return;
        }
        this.redisDataManager.getRedisManager().publishToChannel(
                "proxysync-all",
                "proxysync:user:" + uniqueId + ":message-" + content
        );
    }
}
