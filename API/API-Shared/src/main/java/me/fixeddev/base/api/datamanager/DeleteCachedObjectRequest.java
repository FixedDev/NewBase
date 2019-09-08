package me.fixeddev.base.api.datamanager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeleteCachedObjectRequest<O extends SavableObject> {
    @NotNull
    private List<O> deletedObjects;

    public DeleteCachedObjectRequest(@NotNull List<O> deletedObjects) {
        this.deletedObjects = deletedObjects;
    }

    @NotNull
    public List<O> getDeletedObjects() {
        return deletedObjects;
    }
}
