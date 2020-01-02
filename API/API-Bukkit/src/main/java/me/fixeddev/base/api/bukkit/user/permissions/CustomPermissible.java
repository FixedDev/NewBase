package me.fixeddev.base.api.bukkit.user.permissions;

import com.google.common.util.concurrent.ListenableFuture;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.Tristate;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.PermissionsData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Map;
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
        if(inName.isEmpty()){
            return true;
        }

        Tristate permissionValue = hasPermissionInternal(inName);

        if (isOp() && permissionValue != Tristate.FALSE) {
            return true;
        }

        return permissionValue == Tristate.UNDEFINED ? bukkitHasPermission(inName) : permissionValue.toBoolean();
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

            Optional<PermissionsData> optionalPermissionsData = user.getOrInvalidatePermissionData();

            // The permissions data isn't calculated yet
            if (!optionalPermissionsData.isPresent()) {
                // Calculate the data and cache it
                onPermissionCalculate(user.calculatePermissionsData(dataCalculator));
                // Check if we have any cached data
                if (cachedData == null) {
                    return new HashSet<>();
                }

                // We have cached data, use that for the moment
                optionalPermissionsData = Optional.of(cachedData);
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

    // This is there because the default bukkit method doesn't work with the override of isPermissionSet
    // That I did
    private boolean bukkitHasPermission(String inName){
        if (inName == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else {
            String name = inName.toLowerCase();

            Map<String, PermissionAttachmentInfo> permissions = super.getEffectivePermissions().stream().collect(Collectors.toMap(PermissionAttachmentInfo::getPermission, (o) -> o));

            if (super.isPermissionSet(name)) {
                return permissions.get(name).getValue();
            } else {
                Permission perm = Bukkit.getServer().getPluginManager().getPermission(name);
                return perm != null ? perm.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
            }
        }
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

        Optional<PermissionsData> optionalPermissionsData = user.getOrInvalidatePermissionData();

        // The permissions data isn't calculated yet
        if (!optionalPermissionsData.isPresent()) {
            // Calculate the data and cache it
            onPermissionCalculate(user.calculatePermissionsData(dataCalculator));

            // Check if we have any cached data
            if (cachedData == null) {
                return Tristate.UNDEFINED;
            }

            // We have cached data, use that for the moment
            optionalPermissionsData = Optional.of(cachedData);
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
