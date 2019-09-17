package me.fixeddev.base.api.bukkit;

import com.google.inject.Scopes;
import me.fixeddev.base.api.SharedApiModule;
import me.fixeddev.base.api.bukkit.user.permissions.BukkitPermissionDataCalculator;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.inject.ProtectedBinder;
import org.bukkit.plugin.java.JavaPlugin;

public class BaseBukkitPlugin extends JavaPlugin {
    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new SharedApiModule());

        binder.bind(PermissionDataCalculator.class).to(BukkitPermissionDataCalculator.class).in(Scopes.SINGLETON);
    }
}
