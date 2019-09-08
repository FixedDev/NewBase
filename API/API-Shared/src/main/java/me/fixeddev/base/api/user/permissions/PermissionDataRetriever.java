package me.fixeddev.base.api.user.permissions;

import java.util.UUID;

public interface PermissionDataRetriever {
    PermissionsData ofPlayer(UUID id);
}
