package me.fixeddev.base.api.bukkit.configuration;

import com.google.inject.Inject;
import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.minecraft.config.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BukkitConfigurationFactory implements ConfigurationFactory {

    private File dataFolder;
    private Plugin plugin;

    @Inject
    BukkitConfigurationFactory(Plugin plugin) {
        dataFolder = plugin.getDataFolder();
        this.plugin = plugin;
    }

    @Override
    public Configuration getConfig(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public Configuration getConfig(String fileName) throws IOException {
        if (!fileName.endsWith(".yml")) {
            fileName = fileName + ".yml";
        }

        File configFile = new File(dataFolder, fileName);

        if (!configFile.exists()) {
            try (InputStream stream = plugin.getResource(fileName)) {
                if (stream != null) {
                    Files.copy(stream, configFile.toPath());
                }
            }
        }

        return getConfig(configFile);
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
