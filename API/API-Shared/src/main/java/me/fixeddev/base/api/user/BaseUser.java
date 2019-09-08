package me.fixeddev.base.api.user;

import com.google.common.collect.Iterables;
import me.fixeddev.base.api.user.permissions.PermissionsData;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BaseUser implements User {

    private UUID minecraftId;
    private List<String> nameHistory;
    private long lastSpeakTime;
    private boolean globalChatVisible;
    private boolean staffChatVisibility;

    private PermissionsData permissionsData;

    public BaseUser(UUID minecraftId,
                    List<String> nameHistory,
                    long lastSpeakTime,
                    boolean globalChatVisible,
                    boolean staffChatVisibility,
                    PermissionsData permissionsData) {
        this.minecraftId = minecraftId;
        this.nameHistory = nameHistory;
        this.lastSpeakTime = lastSpeakTime;
        this.globalChatVisible = globalChatVisible;
        this.staffChatVisibility = staffChatVisibility;
        this.permissionsData = permissionsData;
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
    public PermissionsData getPermissionsData() {
        return permissionsData;
    }

}
