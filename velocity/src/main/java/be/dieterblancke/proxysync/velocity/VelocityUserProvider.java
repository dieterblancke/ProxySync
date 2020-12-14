package be.dieterblancke.proxysync.velocity;

import be.dieterblancke.proxysync.api.platform.UserProvider;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

public class VelocityUserProvider implements UserProvider<Player, VelocityUser> {

    private final Map<UUID, VelocityUser> users;
    private final ProxySyncPlugin plugin;

    public VelocityUserProvider(ProxySyncPlugin plugin) {
        this.plugin = plugin;
        this.users = new HashMap<>();
    }

    @Override
    public VelocityUser get(UUID uniqueId) {
        return this.users.get(uniqueId);
    }

    @Override
    public boolean has(UUID uniqueId) {
        return this.users.containsKey(uniqueId);
    }

    @Override
    public VelocityUser add(Player player) {
        if(has(player.getUniqueId())) {
            return get(player.getUniqueId());
        }
        VelocityUser user = new VelocityUser(this.plugin, player);
        this.users.put(player.getUniqueId(), user);
        return user;
    }

    @Override
    public VelocityUser remove(UUID uniqueId) {
        return this.users.remove(uniqueId);
    }

    @Override
    public Set<VelocityUser> getAll() {
        return new HashSet<>(this.users.values());
    }
}
