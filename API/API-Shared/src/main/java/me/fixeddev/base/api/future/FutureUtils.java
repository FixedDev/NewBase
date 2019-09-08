package me.fixeddev.base.api.future;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class FutureUtils {
    public static <V> void addCallback(ListenableFuture<V> future, Callback<V> callback) {
        Futures.addCallback(future, Callback.convertToGuavaCallback(callback));
    }
}
