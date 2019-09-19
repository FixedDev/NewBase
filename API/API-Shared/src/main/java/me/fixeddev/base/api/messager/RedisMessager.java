package me.fixeddev.base.api.messager;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.fixeddev.base.api.redis.RedisService;

import javax.inject.Singleton;
import java.util.HashMap;
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
    @SuppressWarnings("unchecked")
    public <T> Channel<T> getChannel(String channelName, TypeToken<T> type) {
        Channel<T> channel = (Channel<T>) channels.computeIfAbsent(type, ignored -> new HashMap<>()).get(channelName);

        if (channel == null) {
            channel = new RedisChannel<>(channelName, type, redis);

            channels.get(type).put(channelName, channel);
        }

        return channel;
    }


}
