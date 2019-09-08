package me.fixeddev.base.api.messager;

import java.util.UUID;

public class ObjectWrapper<O> {
    private final O object;

    private final UUID channelInstanceId;

    ObjectWrapper(O object, UUID channelInstanceId) {
        this.object = object;
        this.channelInstanceId = channelInstanceId;
    }

    public O getObject() {
        return object;
    }

    public UUID getChannelInstanceId() {
        return channelInstanceId;
    }
}
