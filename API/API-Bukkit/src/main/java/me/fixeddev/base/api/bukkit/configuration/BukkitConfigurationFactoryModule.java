package me.fixeddev.base.api.bukkit.configuration;

import com.google.inject.Scopes;
import com.google.inject.multibindings.OptionalBinder;
import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.base.api.configuration.NullConfigurationFactory;
import me.fixeddev.inject.ProtectedModule;

public class BukkitConfigurationFactoryModule extends ProtectedModule {
    @Override
    protected void configure() {
        OptionalBinder<ConfigurationFactory> factoryOptionalBinder = OptionalBinder.newOptionalBinder(binder(), ConfigurationFactory.class);
        factoryOptionalBinder.setBinding().to(BukkitConfigurationFactory.class);
    }
}
