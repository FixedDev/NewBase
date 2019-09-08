package me.fixeddev.base.api.messager;

import org.redisson.api.listener.MessageListener;

import java.util.Objects;
import java.util.UUID;

class RedissonChannelWrapper<T> implements MessageListener<ObjectWrapper<T>> {

    private String channelName;
    private UUID channelInstanceId;
    private ChannelListener<T> messageListener;

    public RedissonChannelWrapper(String channelName, UUID randomUUID, ChannelListener<T> messageListener) {
        this.channelName = channelName;
        this.channelInstanceId = randomUUID;
        this.messageListener = messageListener;
    }

    @Override
    public void onMessage(CharSequence channel, ObjectWrapper<T> object) {
        if (!channel.equals(channelName)) {
            return;
        }

        if (channelInstanceId.equals(object.getChannelInstanceId())) {
            return;
        }

        messageListener.onReceive(channel.toString(), object.getObject());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedissonChannelWrapper)) return false;
        RedissonChannelWrapper wrapper = (RedissonChannelWrapper) o;
        return Objects.equals(messageListener, wrapper.messageListener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageListener);
    }
}