package be.dieterblancke.proxysync.common.plugin.bootstrap;

import be.dieterblancke.proxysync.api.platform.PlatformType;
import be.dieterblancke.proxysync.common.plugin.logging.PluginLogger;
import be.dieterblancke.proxysync.common.plugin.scheduler.SchedulerAdapter;

import java.io.InputStream;
import java.nio.file.Path;

public interface ProxySyncBootstrap {

    /**
     * Gets a wrapped logger instance for the platform.
     *
     * @return the wrapped plugin's logger
     */
    PluginLogger getPluginLogger();

    /**
     * Gets a wrapped scheduler instance of the platform.
     *
     * @return the wrapped plugin's scheduler
     */
    SchedulerAdapter getSchedulerAdapter();

    /**
     * Gets the platform this instance of ProxySync is running on.
     *
     * @return the platform type
     */
    PlatformType getPlatformType();

    /**
     * Gets the default data directory
     *
     * @return the platforms data folder
     */
    Path getDataDirectory();

    /**
     * Gets a bundled resource file from the jar.
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    InputStream getResourceStream(String path);
}
