package me.fixeddev.base.api.permissions;

import me.fixeddev.base.api.permissions.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Permissible {
    @NotNull
    Tristate hasPermission(@NotNull String permission, Object subject);

    void setPermission(Permission permission);

    @NotNull
    List<Permission> getPermissions();

    @NotNull
    List<Permission> getEffectivePermissions(Object subject);
}
