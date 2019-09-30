package me.fixeddev.base.api.bukkit.configuration;

import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.minecraft.config.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BukkitConfigurationFactory implements ConfigurationFactory {
    @Override
    public Configuration getConfig(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void saveConfiguration(Configuration configuration, File file) throws IOException {
        if (configuration instanceof YamlConfiguration) {
            ((YamlConfiguration) configuration).save(file);
        }
    }
}
