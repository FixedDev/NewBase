package me.fixeddev.base.api.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import me.fixeddev.base.api.service.AbstractService;
import me.fixeddev.minecraft.config.Configuration;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.concurrent.atomic.AtomicBoolean;

public class MongoService implements AbstractService {

    private MongoClient mongoClient;
    private MongoDatabase database;

    private Configuration config;
    private AtomicBoolean started;

    @Inject
    public MongoService(Configuration configuration) {
        config = configuration;

        this.started = new AtomicBoolean(false);
    }

    public MongoClient getMongo() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void doStart() {
        String connectionString;
        if (config.getString("mongo.auth.user", "").isEmpty() || config.getString("mongo.auth.password", "").isEmpty()) {
            connectionString = "mongodb://{host}:{port}/";
        } else {
            connectionString = "mongodb://{user}:{password}@{host}:{port}/?authSource={database}";
        }

        connectionString = connectionString
                .replace("{user}", config.getString("mongo.auth.user", ""))
                .replace("{password}", config.getString("mongo.auth.password", ""))
                .replace("{host}", config.getString("mongo.host", "localhost"))
                .replace("{port}", config.getInt("mongo.port", 27017) + "")
                .replace("{database}", config.getString("mongo.database", "network"));

        ConnectionString connectionStringObject = new ConnectionString(connectionString);

        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

        CodecRegistry codecRegistry = CodecRegistries.fromProviders(new JacksonCodecProvider(mapper));

        mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(connectionStringObject)
                .codecRegistry(codecRegistry)
                .applyToSslSettings(builder -> builder.enabled(config.getBoolean("mongo.ssl", false)))
                .build());

        database = mongoClient.getDatabase(config.getString("mongo.database", "network"));
    }

    @Override
    public void doStop() {
        mongoClient.close();
    }

    @Override
    public AtomicBoolean isStarted() {
        return started;
    }
}
