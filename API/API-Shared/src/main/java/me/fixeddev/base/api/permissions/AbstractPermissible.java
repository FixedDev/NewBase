package me.fixeddev.base.api.permissions;

import me.fixeddev.base.api.permissions.context.ContextResolverRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPermissible implements Permissible {

    // This map is used to accelerate the wildcard matches
    // This will match a permission to a wildcard, so we don't need to use regex more than 1 time
    // example: "test.hey" -> "test.*"
    private static Map<String, String> permissionToWildcard = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Tristate hasPermission(@NotNull String permission, Object subject) {
        List<Permission> effectivePermissions = getEffectivePermissions(subject);

        Optional<Permission> optionalPermission = effectivePermissions.stream()
                .filter(perm -> match(permission, perm.getName()))
                .max(Comparator.comparingInt(Permission::getWeight));

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

        return Stream.concat(getPermissionsAsStream(), getParentPermissions())
                .filter(permission -> resolverRegistry.isApplicable(permission, subject))
                .collect(Collectors.toList());
    }

    protected Stream<Permission> getPermissionsAsStream() {
        return getRawPermissionList().values()
                .stream()
                .flatMap(Collection::stream);
    }

    protected abstract Map<String, List<Permission>> getRawPermissionList();


    // This method is used to match the wildcards
    private boolean match(String text, String pattern) {
        if (pattern.equals(permissionToWildcard.get(text))) {
            return true;
        }

        boolean matches = text.matches(pattern.replace("?", ".?").replace("*", ".*?"));

        if (matches) {
            permissionToWildcard.put(text, pattern);
        }

        return matches;
    }

    /**
     * @return A stream for the permissions of the parents for this permissible
     * Empty stream if this doesn't has any parent
     */
    protected abstract Stream<Permission> getParentPermissions();
}
