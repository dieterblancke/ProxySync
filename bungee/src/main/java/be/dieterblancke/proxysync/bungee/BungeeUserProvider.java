package be.dieterblancke.proxysync.bungee;

import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class BungeeUserProvider implements UserProvider<ProxiedPlayer, BungeeUser> {

    private final Map<UUID, BungeeUser> users;
    private final ProxySyncPlugin plugin;

    public BungeeUserProvider(ProxySyncPlugin plugin) {
        this.plugin = plugin;
        this.users = new HashMap<>();
    }

    @Override
    public BungeeUser get(UUID uniqueId) {
        return this.users.get(uniqueId);
    }

    @Override
    public boolean has(UUID uniqueId) {
        return this.users.containsKey(uniqueId);
    }

    @Override
    public BungeeUser add(ProxiedPlayer player) {
        if(has(player.getUniqueId())) {
            return get(player.getUniqueId());
        }
        BungeeUser user = new BungeeUser(this.plugin, player);
        this.users.put(player.getUniqueId(), user);
        return user;
    }

    @Override
    public BungeeUser remove(UUID uniqueId) {
        return this.users.remove(uniqueId);
    }

    @Override
    public Set<BungeeUser> getAll() {
        return new HashSet<>(this.users.values());
    }
}
