package me.fixeddev.base.api;

import com.google.inject.Scopes;
import me.fixeddev.base.api.mongo.MongoService;
import me.fixeddev.base.api.redis.RedisService;
import me.fixeddev.base.api.service.ServiceBinder;
import me.fixeddev.inject.ProtectedModule;

public class DatabaseModule extends ProtectedModule {

    @Override
    protected void configure() {
        ServiceBinder serviceBinder = ServiceBinder.newBinder(binder());

        serviceBinder.bindService(MongoService.class, Scopes.SINGLETON);
        serviceBinder.bindService(RedisService.class, Scopes.SINGLETON);

        expose(MongoService.class);
        expose(RedisService.class);
    }
}
