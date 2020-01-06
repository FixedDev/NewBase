package me.fixeddev.base.api.user;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Optional;
import java.util.UUID;

public interface UserManager {
    default ListenableFuture<Optional<User>> getUserById(UUID id) {
        return getUserById(id.toString());
    }

    ListenableFuture<Optional<User>> getUserById(String id);

    ListenableFuture<Optional<User>> getUserByName(String name);

    default Optional<User> getIfCached(UUID id) {
        return getIfCached(id.toString());
    }

    Optional<User> getIfCached(String id);

    default void loadUser(UUID id){
        loadUser(id);
    }

    void loadUser(String id);

    void save(User user);
}
