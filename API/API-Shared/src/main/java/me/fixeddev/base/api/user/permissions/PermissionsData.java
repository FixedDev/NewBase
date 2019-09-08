package me.fixeddev.base.api.user.permissions;

import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.permissions.Permissible;
import me.fixeddev.base.api.permissions.group.Group;

import java.util.List;

public interface PermissionsData extends Permissible, SavableObject {

    void setParents(List<Group> parents);

    List<Group> getParents();
}
