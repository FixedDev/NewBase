package me.fixeddev.base.api.bukkit.user.permissions;

import com.google.common.util.concurrent.ListenableFuture;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.Tristate;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.PermissionsData;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CustomPermissible extends PermissibleBase {
    private ObjectLocalCache<User> userRepo;
    private PermissionDataCalculator dataCalculator;
    private String userId;

    private Player player;

    // The attachment used for creating the AttachmentInfo for every effective permission
    private PermissionAttachment pluginAttachment;

    // Ugly hack to prevent the freaking call of the super constructor to recalculatePermissions ._.
    private boolean constructorCalled = false;

    // Only used if the cached user is invalidated, so we don't have cached data on the user
    private PermissionsData cachedData;

    CustomPermissible(Player player, String userId, ObjectLocalCache<User> userRepo, PermissionDataCalculator dataCalculator, Plugin plugin) {
        super(player);

        this.player = player;

        this.userId = userId;
        this.userRepo = userRepo;
        this.dataCalculator = dataCalculator;
        pluginAttachment = this.addAttachment(plugin);

        constructorCalled = true;
        recalculatePermissions();
    }

    @Override
    public boolean isPermissionSet(String name) {
        Tristate permissionValue = hasPermissionInternal(name);

        return permissionValue == Tristate.UNDEFINED ? super.isPermissionSet(name) : permissionValue.toBoolean();
    }

    @Override
    public boolean hasPermission(String inName) {
        Tristate permissionValue = hasPermissionInternal(inName);

        return permissionValue == Tristate.UNDEFINED ? super.hasPermission(inName) : permissionValue.toBoolean();
    }

    @Override
    public void recalculatePermissions() {
        if (!constructorCalled) {
            return;
        }

        ListenableFuture<Optional<User>> futureUser = userRepo.getOrFind(userId);

        FutureUtils.addCallback(futureUser, optional ->
                optional.ifPresent(user -> {
                    onPermissionCalculate(user.calculatePermissionsData(dataCalculator));
                }));
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        ListenableFuture<Optional<User>> futureUser = userRepo.getOrFind(userId);

        // The user isn't cached, we can't stop all the server just to load an user
        // So, we return false, the user is not loaded yet
        if (!futureUser.isDone() || futureUser.isCancelled()) {
            return new HashSet<>();
        }

        try {
            Optional<User> optionalUser = futureUser.get();

            // The user doesn't exist, so that means that it doesn't has any permission
            if (!optionalUser.isPresent()) {
                return new HashSet<>();
            }

            User user = optionalUser.get();

            Optional<PermissionsData> optionalPermissionsData = user.getOrCalculatePermissionData(dataCalculator);

            // The permissions data isn't calculated yet
            if (!optionalPermissionsData.isPresent()) {
                // Check if we have any cached data
                if (cachedData == null) {
                    return new HashSet<>();
                }

                // We have cached data, use that
                optionalPermissionsData = Optional.of(cachedData);
                // Recalculate the data and change the cached data
                onPermissionCalculate(user.calculatePermissionsData(dataCalculator));
            }

            PermissionsData permissionsData = optionalPermissionsData.get();

            return permissionsData.getEffectivePermissions(player).stream().map(perm ->
                    new PermissionAttachmentInfo(this, perm.getName(), pluginAttachment, !perm.isDenied())
            ).collect(Collectors.toSet());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return new HashSet<>();
    }

    private Tristate hasPermissionInternal(String permission) {
        Optional<User> cachedUser = userRepo.getIfCached(userId);

        // The user isn't cached, we can't stop all the server just to load an user
        // So, we return UNDEFINED, the user is not loaded yet
        if (!cachedUser.isPresent()) {
            userRepo.loadIfAbsent(userId);

            return Tristate.UNDEFINED;
        }

        User user = cachedUser.get();

        Optional<PermissionsData> optionalPermissionsData = user.getPermissionData();

        // The permissions data isn't calculated yet
        if (!optionalPermissionsData.isPresent()) {
            // Check if we have any cached data
            if (cachedData == null) {
                return Tristate.UNDEFINED;
            }

            // We have cached data, use that
            optionalPermissionsData = Optional.of(cachedData);
            // Recalculate the data and change the cached data
            onPermissionCalculate(user.calculatePermissionsData(dataCalculator));
        }

        PermissionsData permissionsData = optionalPermissionsData.get();

        return permissionsData.hasPermission(permission, player);
    }

    // The name of this method isn't the best, but it's used to add a callback to the recalculated permissions data
    private void onPermissionCalculate(ListenableFuture<PermissionsData> futurePermissionsData) {
        FutureUtils.addCallback(futurePermissionsData, perms -> {
            if (perms == null) {
                return;
            }

            this.cachedData = perms;
        });
    }
}
