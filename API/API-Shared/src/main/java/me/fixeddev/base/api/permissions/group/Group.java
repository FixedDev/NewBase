package me.fixeddev.base.api.permissions.group;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.permissions.AbstractPermissible;
import me.fixeddev.base.api.permissions.Contextable;
import me.fixeddev.base.api.permissions.Permissible;
import me.fixeddev.base.api.permissions.Weightable;
import me.fixeddev.base.api.permissions.context.Context;
import me.fixeddev.base.api.permissions.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Group extends AbstractPermissible implements Weightable, Contextable, Permissible, SavableObject {

    private final String groupName;
    private String prefix;
    private String suffix;

    private List<Group> parents;

    private Map<String,List<Permission>> permissionList;

    private Map<String, Context> contexts;

    private int weight;

    Group(String groupName, String prefix, String suffix, Map<String, List<Permission>> permissionList, List<Group> parents, Map<String, Context> contexts, int weight) {
        this.groupName = groupName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.parents = parents;
        this.permissionList = permissionList;
        this.contexts = contexts;
        this.weight = weight;
    }

    Group(String groupName, String prefix, String suffix, int weight) {
        this.groupName = groupName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.parents = new ArrayList<>();
        this.permissionList = new ConcurrentHashMap<>();
        this.contexts = new ConcurrentHashMap<>();
        this.weight = weight;
    }

    Group(String name, int weight) {
        this(name, "", "", weight);
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
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
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
    public Set<String> getAllContextsKeys() {
        return new ImmutableSet.Builder<String>().addAll(contexts.keySet()).build();
    }

    @Override
    public Set<Context> getAllContexts() {
        return new ImmutableSet.Builder<Context>().addAll(contexts.values()).build();
    }

    @Override
    public Optional<Context> getContext(String key) {
        return Optional.ofNullable(contexts.get(key));
    }

    @Override
    public void addContext(Context context) {
        Preconditions.checkNotNull(context, "You can't add null contexts");

        contexts.put(context.getKey(), context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return weight == group.weight &&
                groupName.equals(group.groupName) &&
                Objects.equals(prefix, group.prefix) &&
                Objects.equals(suffix, group.suffix) &&
                permissionList.equals(group.permissionList) &&
                parents.equals(group.parents) &&
                contexts.equals(group.contexts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, prefix, suffix, permissionList, parents, contexts, weight);
    }
}
