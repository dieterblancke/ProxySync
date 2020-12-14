package be.dieterblancke.proxysync.common.config;

import be.dieterblancke.proxysync.common.plugin.ProxySyncPlugin;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class Configuration
{

    private final ProxyConfiguration proxyConfiguration;
    private final RedisConfiguration redisConfiguration;

    private Configuration( Toml toml )
    {
        this.proxyConfiguration = new ProxyConfiguration( toml.getTable( "proxy" ) );
        this.redisConfiguration = new RedisConfiguration( toml.getTable( "redis" ) );
    }

    public static Configuration load( ProxySyncPlugin plugin )
    {
        try
        {
            File folder = plugin.getBootstrap().getDataDirectory().toFile();
            File file = new File( folder, "config.toml" );
            if ( !folder.exists() )
            {
                folder.mkdirs();
            }

            if ( !file.exists() )
            {
                InputStream is = plugin.getClass().getResourceAsStream( "/" + file.getName() );
                if ( is != null )
                {
                    Files.copy( is, file.toPath() );
                }
                else
                {
                    file.createNewFile();
                }
            }

            return new Configuration( new Toml().read( file ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( "Failed to load configuration" );
        }
    }

    public ProxyConfiguration getProxyConfiguration()
    {
        return proxyConfiguration;
    }

    public RedisConfiguration getRedisConfiguration()
    {
        return redisConfiguration;
    }

}
