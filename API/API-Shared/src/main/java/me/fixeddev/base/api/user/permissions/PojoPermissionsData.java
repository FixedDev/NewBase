package me.fixeddev.base.api.user.permissions;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.fixeddev.base.api.permissions.Tristate;
import me.fixeddev.base.api.permissions.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.time.LocalTime;
import java.util.List;

/**
 * This is an internal class used only to store and retrieve the data of the db, shouldn't be used outside
 * this class
 * Almost all the functionality is not available, only functionality like getPermissions and id return
 * a valid value
 */
@JsonSerialize(as = PermissionsData.class)
class PojoPermissionsData implements PermissionsData {

    private String id;
    private LocalTime calculationTime;
    private List<Permission> permissions;

    @ConstructorProperties({"_id", "permissions"})
    public PojoPermissionsData(String id, List<Permission> permissions) {
        this.id = id;
        this.calculationTime = LocalTime.now();
        this.permissions = permissions;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String getPrimaryGroup() {
        return "";
    }

    @Override
    public LocalTime getCalculationTime() {
        return calculationTime;
    }

    @NotNull
    @Override
    public Tristate hasPermission(@NotNull String permission, Object subject) {
        return Tristate.UNDEFINED;
    }

    @Override
    public void setPermission(Permission permission) {
    }

    @NotNull
    @Override
    public List<Permission> getPermissions() {
        return permissions;
    }

    @NotNull
    @Override
    public List<Permission> getEffectivePermissions(Object subject) {
        return getPermissions();
    }
}
