package me.fixeddev.base.commons.bukkit;

import com.google.inject.Inject;
import me.fixeddev.base.api.bukkit.configuration.BukkitConfigurationFactoryModule;
import me.fixeddev.base.api.bukkit.configuration.ConfigurationWrapper;
import me.fixeddev.base.commons.CommonsModule;
import me.fixeddev.base.commons.bukkit.commands.PermissionsGroupCommands;
import me.fixeddev.base.commons.bukkit.commands.PermissionsUserCommands;

import me.fixeddev.ebcm.CommandManager;
import me.fixeddev.ebcm.SimpleCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitAuthorizer;
import me.fixeddev.ebcm.bukkit.BukkitCommandManager;
import me.fixeddev.ebcm.bukkit.parameter.provider.BukkitModule;
import me.fixeddev.ebcm.parameter.provider.ParameterProviderRegistry;
import me.fixeddev.ebcm.parametric.ParametricCommandBuilder;
import me.fixeddev.ebcm.parametric.ReflectionParametricCommandBuilder;
import me.fixeddev.inject.ProtectedBinder;
import me.fixeddev.minecraft.config.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonsBukkit extends JavaPlugin {
    @Inject
    private PermissionsGroupCommands groupCommands;
    @Inject
    private PermissionsUserCommands userCommands;

    @Override
    public void configure(ProtectedBinder binder) {
        binder.bind(Configuration.class).toInstance(new ConfigurationWrapper(getConfig()));

        binder.install(new BukkitConfigurationFactoryModule());
        binder.install(new CommonsModule());
    }

    @Override
    public void onLoad() {
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
    }

    @Override
    public void onEnable() {
        ParameterProviderRegistry registry = ParameterProviderRegistry.createRegistry();
        registry.installModule(new BukkitModule());

        CommandManager commandManager = new SimpleCommandManager(new BukkitAuthorizer(), registry);
        BukkitCommandManager bukkitCommandManager = new BukkitCommandManager(commandManager, this.getName());

        ParametricCommandBuilder commandBuilder = new ReflectionParametricCommandBuilder();

        bukkitCommandManager.registerCommands(commandBuilder.fromClass(groupCommands));
        bukkitCommandManager.registerCommands(commandBuilder.fromClass(userCommands));
    }
}
