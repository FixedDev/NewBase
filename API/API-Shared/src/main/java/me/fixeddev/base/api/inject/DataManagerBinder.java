package me.fixeddev.base.api.inject;

import com.google.inject.Binder;

import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.datamanager.RedisCache;
import me.fixeddev.base.api.datamanager.SavableObject;


public interface DataManagerBinder<T extends SavableObject> {
    void bind();

    void bindObjectRepository();

    /**
     * Binds a {@link RedisCache} and it's corresponding {@link ObjectRepository}
     */
    void bindRedisCache();

    <O extends SavableObject> DataManagerBinder<O> newBinder(Class<O> type);

    static <T extends SavableObject> DataManagerBinder<T> createBinder(Binder binder, Class<T> type) {
        return new SimpleDataManagerBinder<>(binder, type);
    }
}
