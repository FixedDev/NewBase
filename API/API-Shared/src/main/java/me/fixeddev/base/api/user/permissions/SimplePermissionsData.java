package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.permissions.AbstractPermissible;
import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.permission.Permission;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the permissions data retrieved for a user
 * This shouldn't keep being used as valid data for more than 2 minutes after
 * its calculation time
 */
public class SimplePermissionsData extends AbstractPermissible implements PermissionsData {

    private String id;

    private Map<String, List<Permission>> permissionsList;
    private String primaryGroup;

    private LocalTime calculationTime = LocalTime.now();

    SimplePermissionsData(UUID id, Group primaryGroup, Object subject) {
        this(id.toString(), primaryGroup, subject);
    }

    SimplePermissionsData(String id, Group primaryGroup, Object subject) {
        this.id = id;
        this.primaryGroup = primaryGroup.getName();

        permissionsList = new ConcurrentHashMap<>();

        Map<String, List<Permission>> groupPermissions = primaryGroup.getEffectivePermissions(subject).stream().collect(Collectors.groupingBy(Permission::getName));

        permissionsList.putAll(groupPermissions);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String getPrimaryGroup() {
        return primaryGroup;
    }

    @Override
    public LocalTime getCalculationTime() {
        return calculationTime;
    }

    @Override
    protected Map<String, List<Permission>> getRawPermissionList() {
        return permissionsList;
    }

    @Override
    protected Stream<Permission> getParentPermissions() {
        return Stream.of();
    }


}
