package me.fixeddev.base.api.messager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor @Data
public class ObjectWrapper<O> {
    private final O object;
    private final UUID channelInstanceId;
}
