package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.permissions.Permissible;

public interface PermissionsData extends Permissible, SavableObject {
    String getPrimaryGroup();

    void setPrimaryGroup(String group);
}
