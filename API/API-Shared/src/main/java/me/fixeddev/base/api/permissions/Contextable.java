package me.fixeddev.base.api.permissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.fixeddev.base.api.permissions.context.Context;

import java.util.Optional;
import java.util.Set;

public interface Contextable {

    @JsonIgnore
    Set<String> getAllContextsKeys();

    Set<Context> getAllContexts();

    Optional<Context> getContext(String key);

    void addContext(Context context);
}
