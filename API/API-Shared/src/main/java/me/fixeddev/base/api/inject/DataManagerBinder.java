package me.fixeddev.base.api.inject;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import me.fixeddev.base.api.datamanager.MongoObjectRepository;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.datamanager.RedisCache;
import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.messager.Messager;
import me.fixeddev.base.api.mongo.MongoService;
import me.fixeddev.base.api.redis.RedisService;

public interface DataManagerBinder {
    <O extends SavableObject> void bindDataManager(Class<O> objectType, String dataPath);

    <O extends SavableObject> void bindObjectRepository(Class<O> objectType, String dataPath);

    /**
     * Binds a {@link RedisCache} and it's corresponding {@link ObjectRepository}
     */
    <O extends SavableObject> void bindRedisCache(Class<O> objectType, String dataPath);

    static DataManagerBinder createBinder(Binder binder) {
        return new DataManagerBinderImpl(binder);
    }

    class DataManagerBinderImpl implements DataManagerBinder {
        private Binder binder;
        private InjectedObjects objects;

        private DataManagerBinderImpl(Binder binder) {
            this.binder = binder;
            objects = new InjectedObjects();

            binder.requestInjection(objects);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <O extends SavableObject> void bindDataManager(Class<O> objectType, String dataPath) {
            ObjectRepository<O> objectRepository = new MongoObjectRepository<>(objects.messager, objects.mongoService, objects.executorService, dataPath, objectType);
            RedisCache<O> redisCache = new RedisCache<>(objectRepository, objects.redisService, dataPath);
            ObjectLocalCache<O> localCache = new ObjectLocalCache<>(objectType, objectRepository, redisCache, objects.messager);

            binder.bind((TypeLiteral<ObjectRepository<O>>) TypeLiteral.get(Types.newParameterizedType(ObjectRepository.class, objectType)))
                    .toInstance(objectRepository);
            binder.bind((TypeLiteral<RedisCache<O>>) TypeLiteral.get(Types.newParameterizedType(RedisCache.class, objectType)))
                    .toInstance(redisCache);
            binder.bind((TypeLiteral<ObjectLocalCache<O>>) TypeLiteral.get(Types.newParameterizedType(ObjectLocalCache.class, objectType)))
                    .toInstance(localCache);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <O extends SavableObject> void bindObjectRepository(Class<O> objectType, String dataPath) {
            ObjectRepository<O> objectRepository = new MongoObjectRepository<>(objects.messager, objects.mongoService, objects.executorService, dataPath, objectType);

            binder.bind((TypeLiteral<ObjectRepository<O>>) TypeLiteral.get(Types.newParameterizedType(ObjectRepository.class, objectType)))
                    .toInstance(objectRepository);
        }

        @Override
        public <O extends SavableObject> void bindRedisCache(Class<O> objectType, String dataPath) {
            ObjectRepository<O> objectRepository = new MongoObjectRepository<>(objects.messager, objects.mongoService, objects.executorService, dataPath, objectType);
            RedisCache<O> redisCache = new RedisCache<>(objectRepository, objects.redisService, dataPath);

            binder.bind((TypeLiteral<ObjectRepository<O>>) TypeLiteral.get(Types.newParameterizedType(ObjectRepository.class, objectType)))
                    .toInstance(objectRepository);
            binder.bind((TypeLiteral<RedisCache<O>>) TypeLiteral.get(Types.newParameterizedType(RedisCache.class, objectType)))
                    .toInstance(redisCache);
        }


        class InjectedObjects {
            @Inject
            private Messager messager;
            @Inject
            private MongoService mongoService;
            @Inject
            private ListeningExecutorService executorService;
            @Inject
            private RedisService redisService;
        }
    }
}
