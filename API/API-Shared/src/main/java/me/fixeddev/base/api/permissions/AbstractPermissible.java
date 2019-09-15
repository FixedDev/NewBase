package me.fixeddev.base.api.permissions;

import me.fixeddev.base.api.permissions.context.ContextResolverRegistry;
import me.fixeddev.base.api.permissions.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPermissible implements Permissible {

    @NotNull
    @Override
    public Tristate hasPermission(@NotNull String permission, Object subject) {
        Optional<Permission> optionalPermission = getEffectivePermissions(subject).stream()
                .filter(perm -> perm.getName().equals(permission))
                .min(Comparator.comparingInt(Permission::getWeight));

        if (!optionalPermission.isPresent()) {
            return Tristate.UNDEFINED;
        }

        return Tristate.fromBoolean(!optionalPermission.get().isDenied());
    }

    @Override
    public void setPermission(Permission permission) {
        getRawPermissionList().computeIfAbsent(permission.getName(), s -> new ArrayList<>()).add(permission);
    }

    @NotNull
    @Override
    public List<Permission> getPermissions() {
        return getPermissionsAsStream().collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Permission> getEffectivePermissions(Object subject) {
        ContextResolverRegistry resolverRegistry = ContextResolverRegistry.getInstance();

        return getPermissionsAsStream()
                .filter(permission -> resolverRegistry.isApplicable(permission, subject))
                .collect(Collectors.toList());
    }

    protected Stream<Permission> getPermissionsAsStream() {
        Stream<Permission> groupPermissionsStream = getRawPermissionList().values()
                .stream()
                .flatMap(Collection::stream);

        return Stream.concat(groupPermissionsStream, getParentPermissions());
    }

    protected abstract Map<String, List<Permission>> getRawPermissionList();

    /**
     * @return A stream for the permissions of the parents for this permissible
     *          Empty stream if this doesn't has any parent
     */
    protected abstract Stream<Permission> getParentPermissions();
}
