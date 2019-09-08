package me.fixeddev.base.api.permissions.group;

import com.google.inject.Scopes;
import me.fixeddev.base.api.inject.DataManagerBinder;
import me.fixeddev.inject.ProtectedModule;

public class GroupModule extends ProtectedModule {
    @Override
    protected void configure() {
        DataManagerBinder dataManagerBinder = DataManagerBinder.createBinder(binder());
        dataManagerBinder.bindObjectRepository(Group.class, "permission:groups");

        bind(GroupManager.class).to(BaseGroupManager.class).in(Scopes.SINGLETON);
    }
}
