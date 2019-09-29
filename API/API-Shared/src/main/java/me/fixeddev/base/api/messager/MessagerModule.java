package me.fixeddev.base.api.messager;

import me.fixeddev.inject.ProtectedModule;

public class MessagerModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(Messager.class).to(RedisMessager.class);

        expose(Messager.class);
    }
}
