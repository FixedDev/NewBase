package me.fixeddev.base.api.datamanager.meta;

import com.google.inject.TypeLiteral;
import me.fixeddev.base.api.datamanager.SavableObject;

class SimpleObjectMeta<T extends SavableObject> implements ObjectMeta<T> {

    private TypeLiteral<T> type;
    private String dataPath;

    public SimpleObjectMeta(TypeLiteral<T> type) {
        this.type = type;

        Class<? super T> rawType = type.getRawType();

        if (rawType.isAnnotationPresent(ObjectName.class)) {
            ObjectName objectName = rawType.getAnnotation(ObjectName.class);

            if(!objectName.value().isEmpty()){
                dataPath = objectName.value();

                return;
            }
        }

        dataPath = rawType.getSimpleName();
    }

    @Override
    public TypeLiteral<T> getType() {
        return type;
    }

    @Override
    public String getDataPath() {
        return dataPath;
    }
}
