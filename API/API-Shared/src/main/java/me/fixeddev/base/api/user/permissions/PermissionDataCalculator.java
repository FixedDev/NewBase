package me.fixeddev.base.api.user.permissions;

import com.google.common.util.concurrent.ListenableFuture;
import me.fixeddev.base.api.user.User;

public interface PermissionDataCalculator {
    ListenableFuture<PermissionsData> calculateForUser(User user);

    void save(PermissionsData permissionsData);
}
