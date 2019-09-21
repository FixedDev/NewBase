package me.fixeddev.base.api.permissions.group;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.datamanager.meta.ObjectName;
import me.fixeddev.base.api.permissions.AbstractPermissible;
import me.fixeddev.base.api.permissions.Permissible;
import me.fixeddev.base.api.permissions.Weightable;
import me.fixeddev.base.api.permissions.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@ObjectName("group")
public class Group extends AbstractPermissible implements Permissible, SavableObject {

    private final String groupName;
    private String prefix;
    private String suffix;

    private List<Group> parents;

    private Map<String,List<Permission>> permissionList;

    Group(String groupName, String prefix, String suffix, Map<String, List<Permission>> permissionList, List<Group> parents) {
        this.groupName = groupName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.parents = parents;
        this.permissionList = permissionList;
    }

    Group(String groupName, String prefix, String suffix) {
        this.groupName = groupName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.parents = new ArrayList<>();
        this.permissionList = new ConcurrentHashMap<>();
    }

    Group(String name, int weight) {
        this(name, "", "");
    }

    Group(String groupName) {
        this(groupName, 1);
    }

    @Override
    public String id() {
        return groupName;
    }

    public String getName() {
        return groupName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Group> getParents() {
        return parents;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    protected Map<String, List<Permission>> getRawPermissionList() {
        return permissionList;
    }

    @Override
    protected Stream<Permission> getParentPermissions() {
        return parents.stream()
                .map(group -> group.getRawPermissionList().values())
                .flatMap(Collection::stream)
                .flatMap(Collection::stream);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return groupName.equals(group.groupName) &&
                Objects.equals(prefix, group.prefix) &&
                Objects.equals(suffix, group.suffix) &&
                permissionList.equals(group.permissionList) &&
                parents.equals(group.parents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, prefix, suffix, permissionList, parents);
    }
}
