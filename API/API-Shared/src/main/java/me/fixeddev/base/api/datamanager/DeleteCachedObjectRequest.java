package me.fixeddev.base.api.datamanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AllArgsConstructor @Getter
public class DeleteCachedObjectRequest<O extends SavableObject> {
    @NotNull
    private List<O> deletedObjects;
}
