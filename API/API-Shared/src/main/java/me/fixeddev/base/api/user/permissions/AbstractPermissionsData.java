package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.permissions.AbstractPermissible;
import me.fixeddev.base.api.permissions.permission.Permission;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class AbstractPermissionsData extends AbstractPermissible implements PermissionsData {

    private String id;

    public AbstractPermissionsData(String id) {
        this.id = id;
    }

    public AbstractPermissionsData(UUID id){
        this.id = id.toString();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    protected Stream<Permission> getParentPermissions() {
        return getPrimaryGroupPermissions().stream();
    }

    protected abstract List<Permission> getPrimaryGroupPermissions();
}
