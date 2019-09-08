package me.fixeddev.base.api.datamanager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;

public interface ObjectRepository<O extends SavableObject> {
    ListenableFuture<O> findOne(String id);

    default ListenableFuture<List<O>> find(String... ids) {
        return find(Arrays.asList(ids));
    }

    ListenableFuture<List<O>> find(List<String> ids);

    default ListenableFuture<List<O>> find(){
        return find(Integer.MAX_VALUE);
    }

    default ListenableFuture<List<O>> find(int limit){
        return find(limit, 0);
    }

    ListenableFuture<List<O>> find(int limit, int skip);

    ListenableFuture<Void> save(O object);

    ListenableFuture<Void> delete(O object);
}
