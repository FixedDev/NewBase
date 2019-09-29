package me.fixeddev.base.api.messager;

import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.UUID;

@Getter
public class ObjectWrapper<O> {
    private final O object;
    private final UUID channelInstanceId;

    @ConstructorProperties({"object","channelInstanceId"})
    public ObjectWrapper(O object, UUID channelInstanceId) {
        this.object = object;
        this.channelInstanceId = channelInstanceId;
    }
}
