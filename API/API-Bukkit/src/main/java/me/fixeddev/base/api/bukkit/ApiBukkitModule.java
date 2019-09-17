package me.fixeddev.base.api.bukkit;

import com.google.inject.Scopes;
import lombok.AllArgsConstructor;
import me.fixeddev.base.api.SharedApiModule;
import me.fixeddev.base.api.bukkit.user.permissions.BukkitPermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.AbstractPermissionDataCalculator;
import me.fixeddev.inject.ProtectedModule;
import me.fixeddev.minecraft.config.Configuration;

@AllArgsConstructor
public class ApiBukkitModule extends ProtectedModule {

    private Configuration config;

    @Override
    protected void configure() {
        bind(Configuration.class).toInstance(config);
        install(new SharedApiModule());

        bind(AbstractPermissionDataCalculator.class).to(BukkitPermissionDataCalculator.class).in(Scopes.SINGLETON);
    }
}
