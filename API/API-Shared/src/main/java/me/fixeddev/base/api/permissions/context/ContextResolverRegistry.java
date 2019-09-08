package me.fixeddev.base.api.permissions.context;

import me.fixeddev.base.api.permissions.Contextable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ContextResolverRegistry {
    private static Lock instanceLock = new ReentrantLock();
    private static volatile ContextResolverRegistry instance;

    public static ContextResolverRegistry getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    throw new IllegalStateException("The ContextResolverRegistry isn't initialized yet!");
                }
            } finally {
                instanceLock.unlock();
            }
        }

        return instance;
    }

    protected ContextResolverRegistry() {
        if (instance != null) {
            instanceLock.lock();
            try {
                if (instance != null) {
                    throw new IllegalStateException("The ContextResolverRegistry is already initialized!");
                }
            } finally {
                instanceLock.unlock();
            }
        }
    }

    public abstract List<ContextResolver<?>> getResolversForContext(Context context);
    public abstract Map<String, List<ContextResolver<?>>> getAllResolvers();

    public abstract boolean isApplicable(Context context, Object subject);
    public abstract boolean isApplicable(Contextable contextable, Object subject);
}
