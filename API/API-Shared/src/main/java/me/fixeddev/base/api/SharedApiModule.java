package me.fixeddev.base.api;

import me.fixeddev.inject.ProtectedModule;

public class SharedApiModule extends ProtectedModule {
    @Override
    protected void configure() {
        install(new JacksonModule());
        install(new DatabaseModule());
        install(new ManagersModule());
    }
}
