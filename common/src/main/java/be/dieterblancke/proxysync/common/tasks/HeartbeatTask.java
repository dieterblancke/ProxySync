package be.dieterblancke.proxysync.common.tasks;

import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;

public class HeartbeatTask implements Runnable
{

    private final ProxySyncPlugin plugin;

    public HeartbeatTask( ProxySyncPlugin plugin )
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        this.plugin.getRedisDataManager().updateProxyHeartbeat();
    }
}
