package me.fixeddev.base.api.user;

import me.fixeddev.base.api.inject.DataManagerBinder;
import me.fixeddev.base.api.user.permissions.PermissionsDataModule;
import me.fixeddev.inject.ProtectedModule;

public class UserModule extends ProtectedModule {
    @Override
    protected void configure() {
        install(new PermissionsDataModule());

        DataManagerBinder binder = DataManagerBinder.createBinder(binder());
        binder.bindDataManager(User.class, "user");
    }
}
