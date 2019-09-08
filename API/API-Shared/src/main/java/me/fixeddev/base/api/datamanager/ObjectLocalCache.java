package me.fixeddev.base.api.datamanager;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import me.fixeddev.base.api.messager.Channel;
import me.fixeddev.base.api.messager.ChannelListener;
import me.fixeddev.base.api.messager.Messager;
import me.fixeddev.base.api.messager.RedisMessager;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

public class ObjectLocalCache<O extends SavableObject> implements ObjectCacheLayer<O> {

    private ObjectRepository<O> objectRepository;
    private RedisCache<O> parentCache;

    private LoadingCache<String, O> objectCache;

    public ObjectLocalCache(Class<O> type, ObjectRepository<O> repository, RedisCache<O> parentCache, Messager messager) {
        objectRepository = repository;

        this.objectCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(500)
                .softValues()
                .<String, O>removalListener((s, o, removalCause) -> {
                    if (o == null) {
                        return;
                    }

                    uncache(o);
                })
                .build(s -> {
                    Optional<O> optionalObject = Optional.empty();

                    if(parentCache != null){
                        optionalObject = parentCache.getIfCached(s);
                    }

                    O object = optionalObject.orElse(objectRepository.findOne(s).get());

                    cache(object);

                    return object;
                });


        TypeToken<UpdateCachedObjectRequest<O>> updateChannelType = new TypeToken<UpdateCachedObjectRequest<O>>() {
        }
                .where(new TypeParameter<O>() {
                }, type);

        TypeToken<DeleteCachedObjectRequest<O>> deleteChannelType = new TypeToken<DeleteCachedObjectRequest<O>>() {
        }
                .where(new TypeParameter<O>() {
                }, type);

        String updateChannelName = "updateCachedObject" + type.getName();
        String deleteChannelName = "deleteCachedObject" + type.getName();

        Channel<UpdateCachedObjectRequest<O>> updateCachedObjectChannel = messager.getChannel(updateChannelName, updateChannelType);
        Channel<DeleteCachedObjectRequest<O>> deleteCachedObjectChannel = messager.getChannel(deleteChannelName, deleteChannelType);

        updateCachedObjectChannel.registerListener(new UpdateListener());
        deleteCachedObjectChannel.registerListener(new DeleteListener());
    }

    @Override
    public ListenableFuture<Optional<O>> getOrFind(@NotNull String id) {
        return Futures.immediateFuture(Optional.ofNullable(objectCache.get(id)));
    }

    @Override
    public Optional<O> getIfCached(@NotNull String id) {
        Optional<O> optional = Optional.ofNullable(objectCache.getIfPresent(id));

        if(parentCache != null){
            optional = optional.isPresent() ? optional : parentCache.getIfCached(id);
        }

        return optional;
    }

    @Override
    public void refresh(String id) {
        objectCache.refresh(id);
    }

    @Override
    public void refreshAll() {
        objectCache.asMap().keySet().forEach(this::refresh);
    }

    @Override
    public int count() {
        return objectCache.asMap().size();
    }

    public Stream<O> all() {
        return ImmutableList.copyOf(objectCache.asMap().values()).stream();
    }

    protected void cache(@NotNull O object) {
        // Allow extendability
    }

    protected void uncache(@NotNull O object) {
        // Allow extendability
    }

    protected void delete(@NotNull O object) {
        // Allow extendability
    }

    class UpdateListener implements ChannelListener<UpdateCachedObjectRequest<O>> {

        @Override
        public void onSend(UpdateCachedObjectRequest<O> object) {
            object.getUpdatedObjects().forEach((id, obj) -> {
                objectCache.invalidate(id);

                objectCache.put(id, obj);
            });
        }

        @Override
        public void onReceive(String channel, UpdateCachedObjectRequest<O> object) {
            object.getUpdatedObjects().forEach((id, obj) -> {
                objectCache.invalidate(id);

                objectCache.put(id, obj);
            });
        }
    }

    class DeleteListener implements ChannelListener<DeleteCachedObjectRequest<O>> {

        @Override
        public void onSend(DeleteCachedObjectRequest<O> object) {
            object.getDeletedObjects().forEach(o -> {
                objectCache.invalidate(o.id());
                delete(o);
            });
        }

        @Override
        public void onReceive(String channel, DeleteCachedObjectRequest<O> object) {
            object.getDeletedObjects().forEach(o -> {
                objectCache.invalidate(o.id());
                delete(o);
            });
        }
    }
}
