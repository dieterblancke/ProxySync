package be.dieterblancke.proxysync.common.config;

import com.moandjiezana.toml.Toml;
import io.lettuce.core.RedisURI;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class RedisConfiguration
{
    private final List<RedisURI> redisURIs;
    private final RedisPoolingConfiguration poolingConfiguration;

    protected RedisConfiguration( final Toml redisTable )
    {
        this.redisURIs = new ArrayList<>();
        this.poolingConfiguration = new RedisPoolingConfiguration( redisTable.getTable( "pooling" ) );

        final List<Toml> connections = redisTable.getTables( "connection" );
        for ( Toml connectionToml : connections )
        {
            final String host = connectionToml.getString( "host" );
            final int port = connectionToml.getLong( "port", 6379L ).intValue();
            final String password = connectionToml.getString( "password", null );

            if ( host != null && !host.isEmpty() )
            {
                RedisURI.Builder builder = RedisURI.builder()
                        .withHost( host )
                        .withPort( port );

                if ( password != null )
                {
                    builder = builder.withPassword( password );
                }

                this.redisURIs.add( builder.build() );
            }
        }
    }

    public List<RedisURI> getRedisURIs()
    {
        return redisURIs;
    }

    public RedisPoolingConfiguration getPoolingConfiguration()
    {
        return poolingConfiguration;
    }

    public static final class RedisPoolingConfiguration
    {

        private final int minIdle;
        private final int maxIdle;
        private final int maxTotal;

        public RedisPoolingConfiguration( final Toml poolingTable )
        {
            minIdle = poolingTable.getLong( "minIdle", 4L ).intValue();
            maxIdle = poolingTable.getLong( "maxIdle", 8L ).intValue();
            maxTotal = poolingTable.getLong( "maxTotal", 12L ).intValue();
        }

        public int getMinIdle()
        {
            return minIdle;
        }

        public int getMaxIdle()
        {
            return maxIdle;
        }

        public int getMaxTotal()
        {
            return maxTotal;
        }

        public <T> GenericObjectPoolConfig<T> asObjectPoolConfig()
        {
            final GenericObjectPoolConfig<T> poolConfig = new GenericObjectPoolConfig<>();

            poolConfig.setMinIdle( minIdle );
            poolConfig.setMaxIdle( maxIdle );
            poolConfig.setMaxTotal( maxTotal );

            return poolConfig;
        }
    }
}
