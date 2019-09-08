package me.fixeddev.base.api.permissions.permission;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.fixeddev.base.api.permissions.Deniable;
import me.fixeddev.base.api.permissions.Weightable;
import me.fixeddev.base.api.permissions.Contextable;
import me.fixeddev.base.api.permissions.context.Context;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Permission implements Deniable, Contextable, Weightable {

    private final String name;

    private boolean denied;
    private int weight;
    private Map<String, Context> permissionContexts;

    private Permission(String name, boolean denied, int weight, Map<String, Context> permissionContexts) {
        this.name = name;
        this.denied = denied;
        this.weight = weight;
        this.permissionContexts = new ConcurrentHashMap<>(permissionContexts);
    }

    private Permission(String name, boolean denied, int weight) {
        this.name = name;
        this.denied = denied;
        this.weight = weight;
        this.permissionContexts = new ConcurrentHashMap<>();
    }

    private Permission(String name) {
        this(name, false, 0);
    }

    public static Permission of(String permission) {
        return new Permission(permission);
    }

    public static Permission of(String permission, boolean denied, int weight) {
        return new Permission(permission, denied, weight);
    }

    public static Permission of(String permission, boolean denied, int weight, Map<String, Context> contexts) {
        return new Permission(permission, denied, weight, contexts);
    }

    public String getName() {
        return name;
    }

    @Override
    public Set<String> getAllContextsKeys() {
        return new ImmutableSet.Builder<String>().addAll(permissionContexts.keySet()).build();
    }

    @Override
    public Set<Context> getAllContexts() {
        return new ImmutableSet.Builder<Context>().addAll(permissionContexts.values()).build();
    }

    @Override
    public Optional<Context> getContext(String key) {
        return Optional.ofNullable(permissionContexts.get(key));
    }

    @Override
    public void addContext(Context context) {
        Preconditions.checkNotNull(context,"You can't add null contexts");

        permissionContexts.put(context.getKey(), context);
    }


    @Override
    public boolean isDenied() {
        return denied;
    }

    @Override
    public void setDenied(boolean denied) {
        this.denied = denied;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return denied == that.denied &&
                weight == that.weight &&
                name.equals(that.name) &&
                permissionContexts.equals(that.permissionContexts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, denied, weight, permissionContexts);
    }
}
