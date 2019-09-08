package me.fixeddev.base.api.user.permissions;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import me.fixeddev.base.api.permissions.Tristate;
import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelegatePermissionsData implements PermissionsData {

    private String id;
    protected Supplier<PermissionsData> delegate;

    public DelegatePermissionsData(PermissionsData delegate) {
        id = delegate.id();
        this.delegate = Suppliers.ofInstance(delegate);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void setParents(List<Group> parents) {
        delegate.get().setParents(parents);
    }

    @Override
    public List<Group> getParents() {
        return delegate.get().getParents();
    }

    @Override
    @NotNull
    public Tristate hasPermission(@NotNull String permission, Object subject) {
        return delegate.get().hasPermission(permission, subject);
    }

    @Override
    public void setPermission(Permission permission) {
        delegate.get().setPermission(permission);
    }

    @Override
    @NotNull
    public List<Permission> getPermissions() {
        return delegate.get().getPermissions();
    }

    @Override
    @NotNull
    public List<Permission> getEffectivePermissions(Object subject) {
        return delegate.get().getEffectivePermissions(subject);
    }
}
