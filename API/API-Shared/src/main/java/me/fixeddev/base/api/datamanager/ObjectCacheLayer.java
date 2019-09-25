package me.fixeddev.base.api.datamanager;

import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ObjectCacheLayer<O extends SavableObject> {

    /**
     * This method tries to get the cached object or find it at the data store
     * if this can't get the cached version or a version of the object in the datastore
     * returns an empty optional
     * This also replaces the cached version if it's finded at the datastore
     *
     * @param id - The id of the object to search
     * @return - An optional object of the generic type O, the object won't be present if it isn't cached
     * and it isn't on the datastore
     */
    ListenableFuture<Optional<O>> getOrFind(@NotNull String id);

    void loadIfAbsent(@NotNull String id);

    /**
     * This caches the specified object into this cache,
     * If a object with the id of this object already exists, will be replaced
     *
     * @param object The object to cache
     */
    void cacheObject(@NotNull O object);

    Optional<O> getIfCached(@NotNull String id);

    void refresh(String id);

    void refreshAll();

    int count();
}