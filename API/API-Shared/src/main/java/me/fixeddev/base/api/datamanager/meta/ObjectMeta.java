package me.fixeddev.base.api.datamanager.meta;

import com.google.inject.TypeLiteral;
import me.fixeddev.base.api.datamanager.SavableObject;


public interface ObjectMeta<T extends SavableObject> {
    TypeLiteral<T> getType();

    String getDataPath();

    static <T extends SavableObject> ObjectMeta<T> createObjectMeta(Class<T> type) {
        return createObjectMeta(TypeLiteral.get(type));
    }

    static <T extends SavableObject> ObjectMeta<T> createObjectMeta(TypeLiteral<T> type) {
        return new SimpleObjectMeta<>(type);
    }
}
