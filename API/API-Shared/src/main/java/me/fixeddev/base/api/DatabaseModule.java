package me.fixeddev.base.api;

import me.fixeddev.base.api.mongo.MongoService;
import me.fixeddev.base.api.redis.RedisService;
import me.fixeddev.base.api.service.ServiceBinder;
import me.fixeddev.inject.ProtectedModule;

public class DatabaseModule extends ProtectedModule {

    @Override
    protected void configure() {
        ServiceBinder serviceBinder = ServiceBinder.newBinder(binder());

        bind(MongoService.class);
        bind(RedisService.class);

        serviceBinder.bindService(MongoService.class, null);
        serviceBinder.bindService(RedisService.class, null);

        expose(MongoService.class);
        expose(RedisService.class);
    }
}
