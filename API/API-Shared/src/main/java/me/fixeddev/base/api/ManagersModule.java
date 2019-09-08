package me.fixeddev.base.api;

import me.fixeddev.base.api.permissions.group.GroupModule;
import me.fixeddev.base.api.user.UserModule;
import me.fixeddev.inject.ProtectedModule;

public class ManagersModule extends ProtectedModule {
    @Override
    protected void configure() {
        install(new GroupModule());
        install(new UserModule());
    }
}
