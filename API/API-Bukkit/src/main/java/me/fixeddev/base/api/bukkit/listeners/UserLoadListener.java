package me.fixeddev.base.api.bukkit.listeners;

import com.google.inject.Inject;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.datamanager.RedisCache;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.api.user.BaseUser;
import me.fixeddev.base.api.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;
import java.util.UUID;

public class UserLoadListener implements Listener {

    @Inject
    private ObjectLocalCache<User> userLocalCache;
    @Inject
    private RedisCache<User> userRedisCache;
    @Inject
    private ObjectRepository<User> userObjectRepository;

    @Inject
    private PermissionDataCalculator calculator;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLogin(AsyncPlayerPreLoginEvent loginEvent) {
        String id = loginEvent.getUniqueId().toString();

        boolean shouldSave = false;

        // Load the user into the local cache
        userLocalCache.loadIfAbsent(id);
        // Check if the user is loaded(it should be, is loaded in the same thread)
        Optional<User> user = userLocalCache.getIfCached(id);

        // The user is not loaded, that means that it doesn't exist
        // So, create it and set the shouldSave to true
        if (!user.isPresent()) {
            user = Optional.of(createUser(loginEvent.getUniqueId()));

            shouldSave = true;
        }

        User userObject = user.get();

        if (userObject.getPrimaryGroup() == null || userObject.getPrimaryGroup().isEmpty()) {
            userObject.setPrimaryGroup(GroupManager.DEFAULT_GROUP);
        }

        // The user can have cached invalid data, so we invalidate it on join
        userObject.invalidatePermissionsData();

        // The user is now usable, so, pre-calculate the permission data for the user
        userObject.calculatePermissionsData(calculator);

        // The user should be saved, so, save it, and insert it into the cache
        if (shouldSave) {
            userObjectRepository.save(userObject);

            // Cache it
            userLocalCache.cacheObject(userObject);
            userRedisCache.cacheObject(userObject);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String currentName = player.getName();
        String idString = player.getUniqueId().toString();

        // Check if the user is loaded(it should be)
        Optional<User> user = userLocalCache.getIfCached(idString);

        // The user is not present, kick the player
        if (!user.isPresent()) {
            // The user is not loaded, so we don't have a language for this user
            player.kickPlayer(ChatColor.RED + "Failed to load your data, try joining again later.");

            return;
        }

        User userObject = user.get();

        // Try to add the current name to the user name-list
        userObject.addLastName(currentName);
    }

    private User createUser(UUID id) {
        return new BaseUser(id);
    }
}
