package me.fixeddev.base.api.messager;

public interface ChannelListener<O> {

    default void onSend(O object){}

    void onReceive(String channel, O object);
}
