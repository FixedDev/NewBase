package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.permissions.Permissible;
import me.fixeddev.base.api.permissions.Tristate;
import me.fixeddev.base.api.permissions.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.List;

public interface PermissionsData extends Permissible, SavableObject {

    Object getSubject();

    String getPrimaryGroup();

    LocalTime getCalculationTime();

    default Tristate hasPermission(@NotNull String permission) {
        return hasPermission(permission, getSubject());
    }

    default List<Permission> getEffectivePermissions() {
        return getEffectivePermissions(getSubject());
    }
}
