package me.fixeddev.base.api.inject;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import me.fixeddev.base.api.datamanager.MongoObjectRepository;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.datamanager.RedisCache;
import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.datamanager.meta.ObjectMeta;

import static me.fixeddev.base.api.util.TypeLiteralUtil.*;

class SimpleDataManagerBinder<T extends SavableObject> implements DataManagerBinder<T> {
    private Binder binder;
    private Class<T> type;

    SimpleDataManagerBinder(Binder binder, Class<T> type) {
        this.binder = binder;
        this.type = type;

        binder.bind(getParameterized(ObjectMeta.class, type))
                .toInstance(ObjectMeta.createObjectMeta(type));
    }

    public void bind() {
        bindRedisCache();
        binder.bind(getParameterized(ObjectLocalCache.class, type)).in(Scopes.SINGLETON);
    }

    @Override
    public void bindRedisCache() {
        bindObjectRepository();
        binder.bind(getParameterized(RedisCache.class, type)).in(Scopes.SINGLETON);
    }

    @Override
    public void bindObjectRepository() {
        binder.bind(getParameterized(ObjectRepository.class, type))
                .to(getParameterized(MongoObjectRepository.class, type)).in(Scopes.SINGLETON);
    }

    @Override
    public <O extends SavableObject> DataManagerBinder<O> newBinder(Class<O> type) {
        return new SimpleDataManagerBinder<>(binder, type);
    }
}
