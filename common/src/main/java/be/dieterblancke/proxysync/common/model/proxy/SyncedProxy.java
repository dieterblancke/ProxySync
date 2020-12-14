package be.dieterblancke.proxysync.common.model.proxy;

import be.dieterblancke.proxysync.api.model.proxy.Proxy;
import be.dieterblancke.proxysync.api.model.user.User;
import be.dieterblancke.proxysync.common.redis.RedisDataManager;
import net.kyori.text.Component;

import java.util.Set;
import java.util.UUID;

public class SyncedProxy implements Proxy {

    private final String id;
    private final RedisDataManager redisDataManager;

    private Set<User> users;

    public SyncedProxy( String id, RedisDataManager redisDataManager) {
        this.id = id;
        this.redisDataManager = redisDataManager;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Set<User> getUsers() {
        if(this.users == null) {
            this.users = this.redisDataManager.getUsersOfProxy(this.id);
        }
        return this.users;
    }

    @Override
    public User getUser(UUID uniqueId) {
        if (!hasUser(uniqueId)) {
            return null;
        }
        return this.getUsers().stream().filter(player -> player.getUniqueId() == uniqueId).findFirst().orElse(null);
    }

    @Override
    public boolean hasUser(UUID uniqueId) {
        if(this.users == null) {
            return this.redisDataManager.isUserConnectedToProxy(this.id, uniqueId);
        }
        return this.users.stream().allMatch(player -> player.getUniqueId().equals(uniqueId));
    }

    @Override
    public void broadcastMessage(Component component) {
        // todo
    }

    @Override
    public void executeCommand(String... command) {
        // todo
    }
}
