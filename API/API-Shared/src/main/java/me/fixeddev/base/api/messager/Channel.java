package me.fixeddev.base.api.messager;

import com.google.common.reflect.TypeToken;

public interface Channel<O> {
    String name();

    TypeToken<O> type();

    void sendMessage(O object);

    boolean hasListeners();

    void registerListener(ChannelListener<O> listener);

    void unregisterListener(ChannelListener<O> listener);
}
