package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.inject.DataManagerBinder;
import me.fixeddev.inject.ProtectedModule;

public class PermissionsDataModule extends ProtectedModule {
    @Override
    protected void configure() {
        DataManagerBinder.createBinder(binder())
                .bindRedisCache(PermissionsData.class, "user:permissions-data");

        bind(PermissionDataCalculator.class).to(AbstractPermissionDataCalculator.class);
    }
}
