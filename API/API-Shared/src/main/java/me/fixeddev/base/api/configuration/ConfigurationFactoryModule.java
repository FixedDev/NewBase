package me.fixeddev.base.api.configuration;

import com.google.inject.multibindings.OptionalBinder;
import me.fixeddev.inject.ProtectedModule;

public class ConfigurationFactoryModule extends ProtectedModule {
    @Override
    protected void configure() {
        OptionalBinder<ConfigurationFactory> factoryOptionalBinder = OptionalBinder.newOptionalBinder(binder(), ConfigurationFactory.class);
        factoryOptionalBinder.setDefault().to(NullConfigurationFactory.class);
    }
}
