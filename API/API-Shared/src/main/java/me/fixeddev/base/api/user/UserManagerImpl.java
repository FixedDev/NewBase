package me.fixeddev.base.api.user;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mongodb.client.model.Filters;
import me.fixeddev.base.api.datamanager.MongoObjectRepository;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.datamanager.RedisCache;

import javax.inject.Inject;
import java.util.Optional;

public class UserManagerImpl implements UserManager {

    @Inject
    private MongoObjectRepository<User> objectRepository;
    @Inject
    private RedisCache<User> redisCache;
    @Inject
    private ObjectLocalCache<User> localCache;

    @Override
    public ListenableFuture<Optional<User>> getUserById(String id) {
        return localCache.getOrFind(id);
    }

    @Override
    public ListenableFuture<Optional<User>> getUserByName(String name) {
        return Futures.transform(objectRepository.findOneByQuery(Filters.in("nameHistory", name)), (Function<? super User, ? extends Optional<User>>) Optional::ofNullable);
    }

    @Override
    public Optional<User> getIfCached(String id) {
        return localCache.getIfCached(id);
    }

    @Override
    public void save(User user) {
        objectRepository.save(user);
        localCache.cacheObject(user);
    }
}
