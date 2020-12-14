package be.dieterblancke.proxysync.common.plugin.scheduler;

/**
 * Represents a scheduled task
 */
public interface SchedulerTask {
    /**
     * Cancel the task.
     */
    void cancel();
}

