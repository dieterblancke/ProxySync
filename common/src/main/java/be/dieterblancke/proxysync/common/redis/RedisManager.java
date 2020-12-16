package be.dieterblancke.proxysync.common.redis;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RedisManager
{
    void execute( Consumer<RedisClusterCommands<String, String>> consumer );

    <R> R execute( Function<RedisClusterCommands<String, String>, R> function );

    void executeAsync( Consumer<RedisClusterAsyncCommands<String, String>> consumer );

    <R> CompletableFuture<R> executeAsync( Function<RedisClusterAsyncCommands<String, String>, CompletableFuture<R>> function );

    void closeConnections();

    LuaScript loadScript( String script );

    void subscribeToChannels( String... channels );

    void publishToChannel( String channel, String message );
}
