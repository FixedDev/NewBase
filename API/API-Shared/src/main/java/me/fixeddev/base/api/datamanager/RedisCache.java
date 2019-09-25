package me.fixeddev.base.api.datamanager;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import me.fixeddev.base.api.datamanager.meta.ObjectMeta;
import me.fixeddev.base.api.future.Callback;
import me.fixeddev.base.api.redis.RedisService;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RedisCache<O extends SavableObject> implements ObjectCacheLayer<O> {

    private String dataPath;

    private ListeningExecutorService executorService;

    private ObjectRepository<O> objectRepo;
    private RedisService redisService;

    @Inject
    public RedisCache(ObjectRepository<O> objectRepo, RedisService redisService, ObjectMeta<O> meta, ListeningExecutorService executorService) {
        this.objectRepo = objectRepo;
        this.redisService = redisService;

        this.dataPath = meta.getDataPath();

        this.executorService = executorService;
    }

    @Override
    public ListenableFuture<Optional<O>> getOrFind(@NotNull String id) {
        Optional<O> cachedObject = getIfCached(id);

        if (!cachedObject.isPresent()) {
            return Futures.transform(objectRepo.findOne(id), (Function<O, Optional<O>>) o -> {
                if (o != null) {
                    RBucket<O> rBucket = redisService.getRedisson().getBucket(dataPath + ":" + id);
                    rBucket.setAsync(o);

                    rBucket.expire(2, TimeUnit.MINUTES);
                }

                return Optional.ofNullable(o);
            }, executorService);
        }

        return Futures.immediateFuture(cachedObject);
    }

    @Override
    public void loadIfAbsent(@NotNull String id) {
        try {
            getOrFind(id).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cacheObject(@NotNull O object) {
        executorService.submit(() -> {
            RedissonClient redis = redisService.getRedisson();
            RBucket<O> rBucket = redis.getBucket(dataPath + ":" + object.id());

            rBucket.set(object);
            rBucket.expire(2, TimeUnit.MINUTES);
        });
    }

    @Override
    public Optional<O> getIfCached(@NotNull String id) {
        RedissonClient redis = redisService.getRedisson();

        RBucket<O> rBucket = redis.getBucket(dataPath + ":" + id);

        if (!rBucket.isExists()) {
            return Optional.empty();
        }

        return Optional.ofNullable(rBucket.get());
    }

    @Override
    public void refresh(String id) {
        Futures.addCallback(objectRepo.findOne(id), Callback.convertToGuavaCallback(obj -> {
            RBucket<O> rBucket = redisService.getRedisson().getBucket(dataPath + ":" + id);

            if (obj != null) {
                rBucket.set(obj);
                rBucket.expire(2, TimeUnit.MINUTES);
            }
        }));
    }

    @Override
    public void refreshAll() {
        RedissonClient redisson = redisService.getRedisson();

        List<String> ids = new ArrayList<>();

        redisson.getKeys().getKeysByPattern(dataPath + ":*").forEach(key -> {
            ids.add(key.replace(dataPath + ":", ""));
        });

        Futures.addCallback(objectRepo.find(ids), Callback.convertToGuavaCallback(objects -> {
            Map<String, O> objectsWithKey = new HashMap<>();

            for (O object : objects) {
                objectsWithKey.put(dataPath + ":" + object.id(), object);
            }

            redisson.getBuckets().set(objectsWithKey);
        }));
    }

    @Override
    public int count() {
        RedissonClient redisson = redisService.getRedisson();
        return Iterables.size(redisson.getKeys().getKeysByPattern(dataPath + ":*"));
    }
}
