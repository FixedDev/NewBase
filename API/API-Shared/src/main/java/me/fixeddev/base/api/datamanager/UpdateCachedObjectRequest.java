package me.fixeddev.base.api.datamanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@AllArgsConstructor @Getter
public class UpdateCachedObjectRequest<O extends SavableObject> {
    @NotNull
    private Map<String, O> updatedObjects;
}
