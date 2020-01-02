package me.fixeddev.base.api.datamanager;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.List;

@Getter
public class DeleteCachedObjectRequest<O extends SavableObject> {
    @NotNull
    private List<O> deletedObjects;

    @ConstructorProperties("deletedObjects")
    public DeleteCachedObjectRequest(@NotNull List<O> deletedObjects) {
        this.deletedObjects = deletedObjects;
    }
}
