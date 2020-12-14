package be.dieterblancke.proxysync.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.text.Component;

public class ProxySyncCommandSource implements CommandSource {

    public static final ProxySyncCommandSource instance = new ProxySyncCommandSource();

    private ProxySyncCommandSource() {
    }

    @Override
    public void sendMessage(Component component) {
    }

    @Override
    public Tristate getPermissionValue(String s) {
        return Tristate.TRUE;
    }
}
