package me.fixeddev.base.api.util;

import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeLiteralUtil {

    public static <T> TypeLiteral<T> getTypeOf(Class<T> clazz){
        return TypeLiteral.get(clazz);
    }

    public static TypeLiteral getTypeOf(Type type){
        return TypeLiteral.get(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeLiteral<T> getParameterized(Class<T> clazz, Class<?>... parameters){
        ParameterizedType type = Types.newParameterizedType(clazz, parameters);

        return getTypeOf(type);
    }
}
