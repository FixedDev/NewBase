package me.fixeddev.base.api.user.permissions;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.datamanager.RedisCache;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.api.user.User;

public abstract class AbstractPermissionDataCalculator implements PermissionDataCalculator {

    private RedisCache<PermissionsData> redisCache;
    private ObjectRepository<PermissionsData> objectRepository;

    private GroupManager groupManager;

    private ListeningExecutorService executorService;

    protected AbstractPermissionDataCalculator(RedisCache<PermissionsData> redisCache, ObjectRepository<PermissionsData> objectRepository, GroupManager groupManager, ListeningExecutorService executorService) {
        this.redisCache = redisCache;
        this.objectRepository = objectRepository;
        this.groupManager = groupManager;
        this.executorService = executorService;
    }

    @Override
    public ListenableFuture<PermissionsData> calculateForUser(User user) {
        return FutureUtils.transformAsync(redisCache.getOrFind(user.id()), permissionsData -> {
            String primaryGroupName = user.getPrimaryGroup();
            Group group = groupManager.getGroupByName(primaryGroupName).get();
            Object subject = getSubjectForUser(user);

            if (group == null) {
                throw new IllegalStateException("The group " + primaryGroupName + " of the user " + user.getLastName().orElse(user.id()) + " doesn't exist!");
            }

            SimplePermissionsData newPermissionsData = new SimplePermissionsData(user.id(), group, subject);

            if (permissionsData.isPresent()) {
                PermissionsData data = permissionsData.get();

                newPermissionsData.setPermissions(data.getPermissions());
            }

            return Futures.immediateFuture(newPermissionsData);
        }, executorService);
    }

    @Override
    public void save(PermissionsData permissionsData) {
        objectRepository.save(permissionsData);
    }

    protected abstract Object getSubjectForUser(User user);
}
