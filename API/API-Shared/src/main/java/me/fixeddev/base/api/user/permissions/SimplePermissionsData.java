package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.permission.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the permissions data retrieved for a user
 * This shouldn't keep being used as valid data for more than 2 minutes after
 * its retrieve time
 */
public class SimplePermissionsData extends AbstractPermissionsData {

    private Map<String, List<Permission>> permissionsList;
    private String primaryGroup;

    public SimplePermissionsData(UUID id) {
        this(id.toString());
    }

    public SimplePermissionsData(String id) {
        super(id);

        permissionsList = new ConcurrentHashMap<>();
    }

    @Override
    protected Map<String, List<Permission>> getRawPermissionList() {
        return permissionsList;
    }

    public void setPermissions(Collection<Permission> permissions){
        for (Permission permission : permissions) {
            setPermission(permission.getName(), permission);
        }
    }

    public void setPermission(String key, Permission permission){
        if(!permission.getName().equals(key)){
            throw new IllegalArgumentException("The provided key isn't the same as the name permission!");
        }

        permissionsList.computeIfAbsent(key, k -> new ArrayList<>()).add(permission);
    }

    @Override
    public String getPrimaryGroup() {
        return primaryGroup;
    }

    @Override
    public void setPrimaryGroup(String group) {
        if(group == null || group.trim().isEmpty()){
            throw new IllegalArgumentException("You can't set a null group to an user");
        }

        this.primaryGroup = group;
    }

    @Override
    protected List<Permission> getPrimaryGroupPermissions() {
        return new ArrayList<>();
    }
}
