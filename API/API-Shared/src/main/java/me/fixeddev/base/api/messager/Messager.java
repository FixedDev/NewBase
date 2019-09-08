package me.fixeddev.base.api.messager;

import com.google.common.reflect.TypeToken;

public interface Messager {

    default <O> Channel<O> getChannel(String name, Class<O> type){
        return getChannel(name, TypeToken.of(type));
    }

    <O> Channel<O> getChannel(String name, TypeToken<O> type);
}
