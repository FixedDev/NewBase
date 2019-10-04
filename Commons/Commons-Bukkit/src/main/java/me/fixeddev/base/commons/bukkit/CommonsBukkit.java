package me.fixeddev.base.commons.bukkit;

import com.google.inject.Inject;
import me.fixeddev.base.api.bukkit.configuration.BukkitConfigurationFactoryModule;
import me.fixeddev.base.commons.bukkit.commands.PermissionsGroupCommands;
import me.fixeddev.base.commons.bukkit.commands.PermissionsUserCommands;
import me.fixeddev.bcm.bukkit.BukkitCommandHandler;
import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;
import me.fixeddev.inject.ProtectedBinder;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonsBukkit extends JavaPlugin {
    @Inject
    private PermissionsGroupCommands groupCommands;
    @Inject
    private PermissionsUserCommands userCommands;

    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new BukkitConfigurationFactoryModule());
    }

    @Override
    public void onEnable() {
        ParametricCommandHandler commandHandler = new BukkitCommandHandler(getLogger(), null, ParameterProviderRegistry.createRegistry());

        commandHandler.registerCommandClass(groupCommands);
        commandHandler.registerCommandClass(userCommands);
    }
}
