package me.fixeddev.base.api.service;

import com.google.inject.Binder;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ServiceBinder {

    void bindService(Class<? extends Service> service, Scope scope);

    @NotNull
    static ServiceBinder newBinder(@NotNull Binder binder) {
        return new ServiceBinderImpl(binder);
    }

    class ServiceBinderImpl implements ServiceBinder {

        private Multibinder<Service> serviceMultibinder;
        private Binder binder;

        ServiceBinderImpl(Binder binder) {
            this.binder = binder;
            this.serviceMultibinder = Multibinder.newSetBinder(binder, Service.class);
        }

        @Override
        public void bindService(Class<? extends Service> service, Scope scope) {
            serviceMultibinder.addBinding().to(service);

            binder.bind(service).in(Optional.ofNullable(scope)
                    .orElse(Scopes.NO_SCOPE));
        }
    }
}
