package me.fixeddev.base.api.user;

import com.google.common.util.concurrent.ListenableFuture;
import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.PermissionsData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface User extends SavableObject {

    UUID getMinecraftId();

    List<String> getNameHistory();
    Optional<String> getLastName();
    void addLastName(String name);

    long lastSpeakTime();
    void setLastSpeakTime(long time);

    boolean isGlobalChatVisible();
    void setGlobalChatVisible(boolean globalChatVisible);

    boolean isInStaffChat();
    void setInStaffChat(boolean inStaffChat);

    /**
     * @return The primary group of this user
     * @throws IllegalStateException if the user doesn't has a primary group set already
     */
    String getPrimaryGroup();
    void setPrimaryGroup(@NotNull String group);

    /**
     * Checks if this user has a cached instance of the {@link PermissionsData} and returns it
     * @return an optional {@link PermissionsData} for this user,
     *         absent if this user never calculated the {@link PermissionsData} or if the PermissionsData
     *         was invalidated and never re calculated
     */
    Optional<PermissionsData> getPermissionData();

    /**
     * Retrieves the {@link PermissionsData} of this user with the specified {@link PermissionDataCalculator}
     * And caches it on this user instance, so the {@link User#getPermissionData()} method can be used
     *
     * @param dataRetriever The {@link PermissionDataCalculator} to use to retrieve the {@link PermissionsData}
     * @return A future PermissionsData that's never null, even if the data for this user doesn't exist
     */
    ListenableFuture<PermissionsData> calculatePermissionsData(@NotNull PermissionDataCalculator dataCalculator);

    /**
     * Invalidates the cached {@link PermissionsData} of this user
     * After the invalidation of the cache the {@link User#getPermissionData()} method can't be used
     * until the re-calculation of the {@link PermissionsData} with the method {@link #calculatePermissionsData(PermissionDataCalculator)}
     */
    void invalidatePermissionsData();
}
