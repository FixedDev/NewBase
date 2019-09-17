package me.fixeddev.base.api.bukkit.user.permissions;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.datamanager.RedisCache;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.permissions.AbstractPermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.PermissionsData;
import org.bukkit.Bukkit;

public class BukkitPermissionDataCalculator extends AbstractPermissionDataCalculator {
    @Inject
    protected BukkitPermissionDataCalculator(RedisCache<PermissionsData> redisCache, ObjectRepository<PermissionsData> objectRepository, GroupManager groupManager, ListeningExecutorService executorService) {
        super(redisCache, objectRepository, groupManager, executorService);
    }

    @Override
    protected Object getSubjectForUser(User user) {
        return Bukkit.getPlayer(user.getMinecraftId());
    }
}
