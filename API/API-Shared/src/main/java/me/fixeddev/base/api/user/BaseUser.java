package me.fixeddev.base.api.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.PermissionsData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@JsonSerialize(as = User.class)
public class BaseUser implements User {

    private UUID minecraftId;
    private List<String> nameHistory;
    private long lastSpeakTime;
    private boolean globalChatVisible;
    private boolean staffChatVisibility;

    private String primaryGroup;

    private Lock asyncFieldsLock = new ReentrantLock();

    // The cached version of the PermissionsData
    @Nullable
    private PermissionsData cachedPermissionsData;

    @ConstructorProperties({"_id", "nameHistory", "lastSpeakTime", "globalChatVisible", "staffChatVisibility"})
    public BaseUser(String id,
                    List<String> nameHistory,
                    long lastSpeakTime,
                    boolean globalChatVisible,
                    boolean staffChatVisibility) {

        this.minecraftId = UUID.fromString(id);
        this.nameHistory = nameHistory;
        this.lastSpeakTime = lastSpeakTime;
        this.globalChatVisible = globalChatVisible;
        this.staffChatVisibility = staffChatVisibility;
    }

    public BaseUser(UUID minecraftId) {
        this.minecraftId = minecraftId;
        nameHistory = new ArrayList<>();
        lastSpeakTime = 0;
        globalChatVisible = true;
        staffChatVisibility = false;
    }

    @Override
    public String id() {
        return minecraftId.toString();
    }

    @Override
    public UUID getMinecraftId() {
        return minecraftId;
    }

    @Override
    public List<String> getNameHistory() {
        return nameHistory;
    }

    @Override
    public Optional<String> getLastName() {
        if (nameHistory.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(Iterables.getLast(nameHistory));
    }

    @Override
    public void addLastName(String name) {
        if (nameHistory.contains(name)) {
            return;
        }

        nameHistory.add(name);
    }

    @Override
    public long lastSpeakTime() {
        return lastSpeakTime;
    }

    @Override
    public void setLastSpeakTime(long time) {
        this.lastSpeakTime = time;
    }

    @Override
    public boolean isGlobalChatVisible() {
        return globalChatVisible;
    }

    @Override
    public void setGlobalChatVisible(boolean globalChatVisible) {
        this.globalChatVisible = globalChatVisible;
    }

    @Override
    public boolean isInStaffChat() {
        return staffChatVisibility;
    }

    @Override
    public void setInStaffChat(boolean inStaffChat) {
        this.staffChatVisibility = inStaffChat;
    }

    @Override
    public String getPrimaryGroup() {
        return primaryGroup;
    }

    @Override
    public void setPrimaryGroup(@NotNull String group) {
        Objects.requireNonNull(group);

        this.primaryGroup = group;
    }

    @Override
    public Optional<PermissionsData> getPermissionData() {
        return Optional.ofNullable(cachedPermissionsData);
    }

    @Override
    public ListenableFuture<PermissionsData> calculatePermissionsData(@NotNull PermissionDataCalculator dataRetriever) {
        Objects.requireNonNull(dataRetriever);

        ListenableFuture<PermissionsData> futureData = dataRetriever.calculateForUser(this);

        FutureUtils.addCallback(futureData, data -> {
            asyncFieldsLock.lock();
            cachedPermissionsData = data;
            asyncFieldsLock.unlock();
        });

        return futureData;
    }

    @Override
    public void invalidatePermissionsData() {
        asyncFieldsLock.lock();
        this.cachedPermissionsData = null;
        asyncFieldsLock.unlock();
    }
}
