package me.fixeddev.base.api.bukkit.user.permissions;

import com.google.inject.Inject;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissibleInjector {
    @Inject
    private Plugin plugin;
    @Inject
    private ObjectLocalCache<User> userRepo;
    @Inject
    private PermissionDataCalculator dataCalculator;

    private Map<UUID, PermissibleBase> originalPermissibleMap = new ConcurrentHashMap<>();

    private Field permissibleField;

    public void injectPermissible(Player player) throws NoSuchFieldException, IllegalAccessException {
        PermissibleBase oldPermissible = getPermissibleOf(player);

        // This player already has an injected permissible, ignore it
        if (oldPermissible instanceof CustomPermissible) {
            return;
        }

        CustomPermissible newPermissible = new CustomPermissible(player, player.getUniqueId().toString(), userRepo, dataCalculator, plugin);
        setPermissible(player, newPermissible);

        originalPermissibleMap.put(player.getUniqueId(), oldPermissible);
    }

    public void setOriginalPermissible(Player player) throws NoSuchFieldException, IllegalAccessException {
        PermissibleBase originalPermissible = originalPermissibleMap.get(player.getUniqueId());

        // The player wasn't injected yet, ignore it
        if (originalPermissible == null) {
            return;
        }

        setPermissible(player, originalPermissible);
    }

    private PermissibleBase getPermissibleOf(Player player) throws NoSuchFieldException, IllegalAccessException {
        if (permissibleField == null) {
            permissibleField = player.getClass().getDeclaredField("perm");
        }

        boolean isAccesible = permissibleField.isAccessible();
        permissibleField.setAccessible(true);

        PermissibleBase permissible = (PermissibleBase) permissibleField.get(player);
        permissibleField.setAccessible(isAccesible);

        return permissible;
    }

    private void setPermissible(Player player, PermissibleBase permissibleBase) throws NoSuchFieldException, IllegalAccessException {
        if (permissibleField == null) {
            permissibleField = player.getClass().getField("perm");
        }

        boolean isAccesible = permissibleField.isAccessible();
        permissibleField.setAccessible(true);

        permissibleField.set(player, permissibleBase);
        permissibleField.setAccessible(isAccesible);
    }
}
