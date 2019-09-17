package me.fixeddev.base.api.bukkit;

import me.fixeddev.inject.ProtectedBinder;
import org.bukkit.plugin.java.JavaPlugin;

public class BaseBukkitPlugin extends JavaPlugin {
    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new ApiBukkitModule(getConfig()));
    }
}
