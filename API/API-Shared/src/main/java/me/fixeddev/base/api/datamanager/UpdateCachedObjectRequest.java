package me.fixeddev.base.api.datamanager;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UpdateCachedObjectRequest<O extends SavableObject> {
    @NotNull
    private Map<String, O> updatedObjects;

    public UpdateCachedObjectRequest(@NotNull Map<String, O> updatedObjects) {
        this.updatedObjects = updatedObjects;
    }

    @NotNull
    public Map<String, O> getUpdatedObjects() {
        return updatedObjects;
    }
}
