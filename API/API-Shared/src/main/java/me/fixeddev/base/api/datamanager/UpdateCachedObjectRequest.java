package me.fixeddev.base.api.datamanager;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

@Getter
public class UpdateCachedObjectRequest<O extends SavableObject> {
    @NotNull
    private Map<String, O> updatedObjects;

    @ConstructorProperties("updatedObjects")
    public UpdateCachedObjectRequest(@NotNull Map<String, O> updatedObjects) {
        this.updatedObjects = updatedObjects;
    }
}
