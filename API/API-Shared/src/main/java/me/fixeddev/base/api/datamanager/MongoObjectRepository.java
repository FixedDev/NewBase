package me.fixeddev.base.api.datamanager;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.fixeddev.base.api.messager.Channel;
import me.fixeddev.base.api.messager.Messager;
import me.fixeddev.base.api.messager.RedisMessager;
import me.fixeddev.base.api.mongo.MongoService;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoObjectRepository<O extends SavableObject> implements ObjectRepository<O> {

    private ListeningExecutorService executorService;
    private MongoService mongoService;

    private String dataPath;

    private Class<O> type;

    private Channel<UpdateCachedObjectRequest<O>> updateCachedObjectChannel;
    private Channel<DeleteCachedObjectRequest<O>> deleteCachedObjectChannel;

    public MongoObjectRepository(Messager messager, MongoService mongoService, ListeningExecutorService executorService, String dataPath, Class<O> type) {
        this.mongoService = mongoService;
        this.executorService = executorService;

        this.dataPath = dataPath;

        this.type = type;

        TypeToken<UpdateCachedObjectRequest<O>> updateChannelType = new TypeToken<UpdateCachedObjectRequest<O>>() {
        }
                .where(new TypeParameter<O>() {
                }, type);

        TypeToken<DeleteCachedObjectRequest<O>> deleteChannelType = new TypeToken<DeleteCachedObjectRequest<O>>() {
        }
                .where(new TypeParameter<O>() {
                }, type);

        updateCachedObjectChannel = messager.getChannel("updateCachedObject" + dataPath, updateChannelType);
        deleteCachedObjectChannel = messager.getChannel("deleteCachedObject" + dataPath, deleteChannelType);
    }

    @Override
    public ListenableFuture<O> findOne(String id) {
        return findOneByQuery(Filters.eq("_id", id));
    }

    public ListenableFuture<O> findOneByQuery(Bson query) {
        return executorService.submit(() -> {
            MongoDatabase db = mongoService.getDatabase();

            MongoCollection<O> collection = db.getCollection(dataPath, type);

            return collection.find(query).first();
        });
    }

    public ListenableFuture<List<O>> findByQuery(Bson query) {
        return executorService.submit(() -> {
            MongoDatabase db = mongoService.getDatabase();

            MongoCollection<O> collection = db.getCollection(dataPath, type);

            List<O> objects = new ArrayList<>();

            collection.find(query).into(objects);

            return objects;
        });
    }

    @Override
    public ListenableFuture<List<O>> find(List<String> ids) {
        return executorService.submit(() -> {
            MongoDatabase db = mongoService.getDatabase();

            MongoCollection<O> collection = db.getCollection(dataPath, type);

            List<O> objects = new ArrayList<>();

            for (String id : ids) {
                O object = collection.find(Filters.eq("id", id)).first();

                if (object != null) {
                    objects.add(object);
                }
            }

            return objects;
        });
    }

    @Override
    public ListenableFuture<List<O>> find(int limit, int skip) {
        return executorService.submit(() -> {
            MongoDatabase db = mongoService.getDatabase();

            MongoCollection<O> collection = db.getCollection(dataPath, type);

            List<O> objects = new ArrayList<>();

            FindIterable<O> dbIterator = collection.find();

            if (limit < Integer.MAX_VALUE && limit > 0) {
                dbIterator = dbIterator.limit(limit);
            }

            if (skip > 0) {
                dbIterator = dbIterator.skip(skip);
            }

            dbIterator.into(objects);

            return objects;
        });
    }

    @Override
    public ListenableFuture<Void> save(O object) {
        return executorService.submit(() -> {
            MongoDatabase db = mongoService.getDatabase();

            MongoCollection<O> collection = db.getCollection(dataPath, type);

            collection.replaceOne(new Document("_id", object.id()), object, new ReplaceOptions().upsert(true));

            Map<String, O> map = new HashMap<>();
            map.put(object.id(), object);

            updateCachedObjectChannel.sendMessage(new UpdateCachedObjectRequest<>(map));

            return null;
        });
    }

    @Override
    public ListenableFuture<Void> delete(O object) {
        return executorService.submit(() -> {
            MongoDatabase db = mongoService.getDatabase();

            MongoCollection<O> collection = db.getCollection(dataPath, type);

            collection.deleteOne(Filters.eq("_id", object.id()));

            deleteCachedObjectChannel.sendMessage(new DeleteCachedObjectRequest<>(Collections.singletonList(object)));

            return null;
        });
    }

}
