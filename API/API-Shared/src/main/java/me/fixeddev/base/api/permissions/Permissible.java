package me.fixeddev.base.api.permissions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Permissible {
    @NotNull
    Tristate hasPermission(@NotNull String permission, Object subject);

    void setPermission(Permission permission);

    /**
     * Searches permissions matching with the specified name permissionName
     * and removes the permission with the highest weight
     *
     * @param permissionName The name of the permission to search for
     * @return The removed permission or null if the permission couldn't be removed
     */
    Permission removePermission(String permissionName);

    /**
     * @return The specific permissions for this permissible, not counting the parent permissions
     */
    @NotNull
    List<Permission> getPermissions();

    /**
     * Searches through the permissions of this permissible and his parents
     * and calculates the contexts for every {@link Permission} with the specified subject
     * 
     * @param subject The subject to use when calculating the contexts
     * @return A list of the effective permissions on this context
     */
    @NotNull
    List<Permission> getEffectivePermissions(Object subject);
}
