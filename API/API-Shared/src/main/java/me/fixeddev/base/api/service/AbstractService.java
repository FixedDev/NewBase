package me.fixeddev.base.api.service;

import java.util.concurrent.atomic.AtomicBoolean;

public interface AbstractService extends Service {

    void doStart() throws Exception;

    void doStop();

    @Override
    default void start() throws Exception {
        if (!isStarted().compareAndSet(false, true)) {
            throw new IllegalStateException("The service is already initialized");
        }

        doStart();
    }

    @Override
    default void stop() {
        if (!isStarted().compareAndSet(true, false)) {
            throw new IllegalStateException("The service is already initialized");
        }

        doStop();
    }

    AtomicBoolean isStarted();
}
