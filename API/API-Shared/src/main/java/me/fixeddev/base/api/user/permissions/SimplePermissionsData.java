package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.permission.Permission;

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
    private List<Group> parents;

    public SimplePermissionsData(UUID id, List<Group> parents) {
        this(id.toString(), parents);
    }

    public SimplePermissionsData(String id, List<Group> parents) {
        super(id);

        this.parents = parents;
        permissionsList = new ConcurrentHashMap<>();
    }

    @Override
    protected Map<String, List<Permission>> getRawPermissionList() {
        return permissionsList;
    }

    @Override
    public void setParents(List<Group> parents) {
        this.parents = parents;
    }

    @Override
    public List<Group> getParents() {
        return parents;
    }
}
