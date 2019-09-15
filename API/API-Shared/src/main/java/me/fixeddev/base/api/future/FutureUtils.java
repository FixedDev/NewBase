package me.fixeddev.base.api.future;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.function.Function;

public class FutureUtils {
    private FutureUtils() {
    }

    public static <V> void addCallback(ListenableFuture<V> future, Callback<V> callback) {
        Futures.addCallback(future, Callback.convertToGuavaCallback(callback));
    }

    public static <V, R> ListenableFuture<R> transform(ListenableFuture<V> future, Function<V, R> transformFunction, Executor executor) {
        return Futures.transform(future, transformFunction::apply, executor);
    }

    public static <V, R> ListenableFuture<R> transform(ListenableFuture<V> future, Function<V, R> transformFunction) {
        return Futures.transform(future, transformFunction::apply, Runnable::run);
    }
}
