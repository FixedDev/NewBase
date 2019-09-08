package me.fixeddev.base.api.future;

import com.google.common.util.concurrent.FutureCallback;

public interface Callback<O> {
    void call(O obj);

    default void fail(Throwable throwable) {
    }


    static <O> FutureCallback<O> convertToGuavaCallback(Callback<O> callback) {
        return new FutureCallback<O>(){

            @Override
            public void onSuccess(O o) {
                callback.call(o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.fail(throwable);
            }
        };
    }
}
