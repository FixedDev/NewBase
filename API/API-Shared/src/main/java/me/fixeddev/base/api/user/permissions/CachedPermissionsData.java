package me.fixeddev.base.api.user.permissions;

import com.google.common.base.Suppliers;

import java.util.concurrent.TimeUnit;

public class CachedPermissionsData extends DelegatePermissionsData {

    private PermissionDataRetriever retriever;

    public CachedPermissionsData(PermissionsData delegate, PermissionDataRetriever retriever) {
        super(delegate);

        this.retriever = retriever;
        this.delegate = Suppliers.memoizeWithExpiration(() -> retriever.ofPlayerId(id()), 2, TimeUnit.MINUTES);
    }

    public CachedPermissionsData(String id, PermissionDataRetriever retriever) {
        this(new SimplePermissionsData(id), retriever);
    }
}
