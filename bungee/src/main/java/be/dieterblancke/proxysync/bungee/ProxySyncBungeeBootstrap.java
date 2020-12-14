package be.dieterblancke.proxysync.bungee;

import be.dieterblancke.proxysync.api.ProxySyncApi;
import be.dieterblancke.proxysync.api.event.user.UserJoinEvent;
import be.dieterblancke.proxysync.api.platform.PlatformType;
import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import be.dieterblancke.proxysync.common.plugin.bootstrap.ProxySyncBootstrap;
import be.dieterblancke.proxysync.common.plugin.logging.JavaLoggerWrapper;
import be.dieterblancke.proxysync.common.plugin.logging.PluginLogger;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class ProxySyncBungeeBootstrap extends Plugin implements ProxySyncBootstrap {

    private final PluginLogger logger;
    private final SchedulerAdapter schedulerAdapter;
    private final ProxySyncBungeePlugin plugin;

    public ProxySyncBungeeBootstrap() {
        this.logger = new JavaLoggerWrapper(this.getLogger());
        this.schedulerAdapter = new BungeeSchedulerAdapter(this);
        this.plugin = new ProxySyncBungeePlugin(this);
    }

    @Override
    public void onEnable() {
        this.plugin.enable();

        this.getProxy().getPluginManager().registerCommand(this, new DebugCommand(this.plugin));
        this.plugin.getEventBus().subscribe(UserJoinEvent.class, (event) -> {
            this.getProxy().broadcast(new TextComponent(ChatColor.LIGHT_PURPLE + "Event listener triggered!"));
        });
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    public SchedulerAdapter getSchedulerAdapter() {
        return this.schedulerAdapter;
    }

    public PlatformType getPlatformType() {
        return PlatformType.BUNGEECORD;
    }

    public Path getDataDirectory() {
        return super.getDataFolder().toPath();
    }

    public InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    private static class DebugCommand extends Command {

        private final ProxySyncPlugin plugin;

        public DebugCommand(ProxySyncPlugin plugin) {
            super("debug");
            this.plugin = plugin;
        }

        @Override
        public void execute(CommandSender commandSender, String[] args) {

            if (args.length == 0) {
                commandSender.sendMessage(new TextComponent(ChatColor.GOLD + "=== Debug Info ==="));

                ProxySyncApi api = this.plugin.getApi();
                commandSender.sendMessage(new TextComponent(ChatColor.GRAY + "Total user count: " + ChatColor.GOLD + api.getTotalUserCount()));

                Set<String> proxiesColored = api.getProxyManager().getActiveProxies().stream().map(proxyId -> (api.getProxyManager().getCurrentProxy().getId().equals(proxyId) ? ChatColor.GOLD : ChatColor.GRAY ) + proxyId).collect(Collectors.toSet());
                commandSender.sendMessage(new TextComponent(ChatColor.GRAY + "Proxies: " + String.join(ChatColor.GRAY + ", ", proxiesColored)));
            } else if(args.length == 1 && args[0].equals("event")) {
                this.plugin.getEventBus().post(new UserJoinEvent(null));
            }
        }
    }
}
