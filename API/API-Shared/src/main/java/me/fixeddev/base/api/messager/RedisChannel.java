package me.fixeddev.base.api.messager;

import com.google.common.reflect.TypeToken;

import me.fixeddev.base.api.redis.RedisService;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RedisChannel<T> implements Channel<T> {

    private String name;
    private TypeToken<T> type;

    private Queue<RedissonChannelWrapper<T>> wrappedListeners;

    private Map<ChannelListener<T>, RedissonChannelWrapper<T>> channelWrapperMap;

    private RedisService redis;

    private UUID randomInstanceId = UUID.randomUUID();

    RedisChannel(String channelName, TypeToken<T> type, RedisService executor) {
        this.name = channelName;
        this.type = type;

        this.redis = executor;

        this.wrappedListeners = new ConcurrentLinkedDeque<>();
        this.channelWrapperMap = new ConcurrentHashMap<>();
    }


    @Override
    public void registerListener(ChannelListener<T> listener) {
        Objects.requireNonNull(listener, "ChannelListener must be not null");

        if (channelWrapperMap.containsKey(listener)) {
            throw new IllegalArgumentException("Listener already Registered!");
        }

        RedissonChannelWrapper<T> wrapper = new RedissonChannelWrapper<>(name, randomInstanceId, listener);

        RedissonClient redisson = redis.getRedisson();

        RTopic rTopic = redisson.getTopic(name);

        rTopic.addListener(new TypeToken<ObjectWrapper<T>>() {
        }.getRawType(), wrapper);

        wrappedListeners.offer(wrapper);
        channelWrapperMap.put(listener, wrapper);
    }


    @Override
    public void unregisterListener(ChannelListener<T> listener) {
        Objects.requireNonNull(listener, "ChannelListener must be not null");

        RedissonChannelWrapper<T> wrapper = channelWrapperMap.get(listener);

        Objects.requireNonNull(wrapper, "The provided listener is not registered");

        wrappedListeners.remove(wrapper);
        channelWrapperMap.remove(listener);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public TypeToken<T> type() {
        return type;
    }

    @Override
    public void sendMessage(T data) {
        RedissonClient redisson = redis.getRedisson();

        RTopic rTopic = redisson.getTopic(name);

        rTopic.publish(new ObjectWrapper<>(data, randomInstanceId));

        channelWrapperMap.keySet().forEach(listener -> {
            listener.onSend(data);
        });

    }

    @Override
    public boolean hasListeners() {
        return !wrappedListeners.isEmpty();
    }


}
