package me.fixeddev.base.api.messager;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.fixeddev.base.api.redis.RedisService;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class RedisMessager implements Messager {

    private RedisService redis;

    private Map<TypeToken, Map<String, Channel>> channels;

    @Inject
    public RedisMessager(RedisService executor) {
        redis = executor;
        this.channels = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Channel<T> getChannel(String channelName, TypeToken<T> type) {
        Channel<T> channel = (Channel<T>) channels.get(type).get(channelName);

        if (channel == null) {
            channel = new RedisChannel<>(channelName, type, redis);

            channels.computeIfAbsent(type, typeToken -> new ConcurrentHashMap<>()).put(channelName, channel);
        }

        return channel;
    }


}
