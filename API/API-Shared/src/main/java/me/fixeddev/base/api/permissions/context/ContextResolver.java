package me.fixeddev.base.api.permissions.context;

public interface ContextResolver<T> {
    boolean isContextAplicable(Context context, T subject);
}
