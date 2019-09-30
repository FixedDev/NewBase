package me.fixeddev.base.api.bukkit.configuration;

import com.google.inject.Inject;
import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.minecraft.config.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class BukkitConfigurationFactory implements ConfigurationFactory {

    private File dataFolder;

    @Inject
    BukkitConfigurationFactory(Plugin plugin) {
        dataFolder = plugin.getDataFolder();
    }

    @Override
    public Configuration getConfig(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public Configuration getConfig(String fileName) {
        return getConfig(new File(dataFolder, fileName));
    }

    @Override
    public void saveConfiguration(Configuration configuration, File file) throws IOException {
        if (configuration instanceof YamlConfiguration) {
            ((YamlConfiguration) configuration).save(file);
        }
    }

    @Override
    public void saveConfiguration(Configuration configuration, String fileName) throws IOException {
        saveConfiguration(configuration, new File(dataFolder, fileName));
    }
}
