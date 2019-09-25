package me.fixeddev.base.api.bukkit.listeners;

import com.google.inject.Inject;
import me.fixeddev.base.api.bukkit.user.permissions.PermissibleInjector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Level;

public class PermissibleInjectUserListener implements Listener {

    @Inject
    private PermissibleInjector injector;
    @Inject
    private PluginLogger pluginLogger;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            injector.injectPermissible(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            pluginLogger.log(Level.SEVERE,"Failed to inject " + player.getName() + " permissible", e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        try {
            injector.setOriginalPermissible(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            pluginLogger.log(Level.SEVERE,"Failed to remove " + player.getName() + " custom permissible", e);
        }
    }
}
