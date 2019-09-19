package me.fixeddev.base.api;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.fixeddev.base.api.messager.MessagerModule;
import me.fixeddev.inject.ProtectedModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedApiModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(ExecutorService.class).to(ListeningExecutorService.class);

        install(new JacksonModule());
        install(new DatabaseModule());
        install(new ManagersModule());
    }

    @Provides
    @Singleton
    public ListeningExecutorService provideExecutorService() {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }
}

