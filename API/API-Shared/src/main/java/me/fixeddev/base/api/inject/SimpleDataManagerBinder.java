package me.fixeddev.base.api.inject;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
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

    private boolean objectRepoBinded;
    private boolean redisCacheBinded;
    private boolean objectLocalCacheBinded;

    SimpleDataManagerBinder(Binder binder, Class<T> type) {
        this.binder = binder;
        this.type = type;

        binder.bind(getParameterized(ObjectMeta.class, type))
                .toInstance(ObjectMeta.createObjectMeta(type));
    }

    public DataManagerBinder<T> bind() {
        bindRedisCache();
        binder.bind(getParameterized(ObjectLocalCache.class, type)).in(Scopes.SINGLETON);

        objectLocalCacheBinded = true;

        return this;
    }

    @Override
    public DataManagerBinder<T> bindRedisCache() {
        bindObjectRepository();
        binder.bind(getParameterized(RedisCache.class, type)).in(Scopes.SINGLETON);

        redisCacheBinded = true;

        return this;
    }

    @Override
    public DataManagerBinder<T> expose() {
        if(!(binder instanceof PrivateBinder)){
            throw new IllegalArgumentException("To expose something the binder should be an instance of a PrivateBinder!");
        }

        PrivateBinder binder = (PrivateBinder) this.binder;

        if(objectRepoBinded){
            binder.expose(getParameterized(ObjectRepository.class, type));
        }

        if(redisCacheBinded){
            binder.expose(getParameterized(RedisCache.class, type));
        }

        if(objectLocalCacheBinded){
            binder.expose(getParameterized(ObjectLocalCache.class, type));
        }

        return this;
    }

    @Override
    public DataManagerBinder<T> bindObjectRepository() {
        binder.bind(getParameterized(ObjectRepository.class, type))
                .to(getParameterized(MongoObjectRepository.class, type)).in(Scopes.SINGLETON);

        objectRepoBinded = true;

        return this;
    }

    @Override
    public <O extends SavableObject> DataManagerBinder<O> newBinder(Class<O> type) {
        return new SimpleDataManagerBinder<>(binder, type);
    }
}
