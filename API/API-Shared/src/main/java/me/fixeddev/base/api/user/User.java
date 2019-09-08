package me.fixeddev.base.api.user;

import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.user.permissions.PermissionsData;

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

    PermissionsData getPermissionsData();
}
