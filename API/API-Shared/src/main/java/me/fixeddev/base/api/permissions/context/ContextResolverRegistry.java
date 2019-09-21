package me.fixeddev.base.api.permissions.context;

import me.fixeddev.base.api.permissions.Contextable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContextResolverRegistry {
    private static Lock instanceLock = new ReentrantLock();
    private static volatile ContextResolverRegistry instance;

    public static ContextResolverRegistry getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                   instance = new ContextResolverRegistry();
                }
            } finally {
                instanceLock.unlock();
            }
        }

        return instance;
    }

    private Map<String, List<ContextResolver<Object>>> contextResolvers;

    private ContextResolverRegistry() {
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

        contextResolvers = new ConcurrentHashMap<>();
    }

    public List<ContextResolver<Object>> getResolversForContext(Context context) {
        return contextResolvers.computeIfAbsent(context.getKey(), s -> new ArrayList<>());
    }

    public Map<String, List<ContextResolver<Object>>> getAllResolvers() {
        return Collections.unmodifiableMap(contextResolvers);
    }

    public boolean isApplicable(Context context, Object subject) {
        for (ContextResolver<Object> contextResolver : getResolversForContext(context)) {
            if (!contextResolver.isContextAplicable(context, subject)) {
                return false;
            }
        }

        return true;
    }

    public boolean isApplicable(Contextable contextable, Object subject) {
        for (Context context : contextable.getAllContexts()) {
            if (!isApplicable(context, subject)) {
                return false;
            }
        }

        return true;
    }
}
