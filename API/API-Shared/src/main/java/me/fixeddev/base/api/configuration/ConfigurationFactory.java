package me.fixeddev.base.api.configuration;

import me.fixeddev.minecraft.config.Configuration;

import java.io.File;
import java.io.IOException;

public interface ConfigurationFactory {
    Configuration getConfig(File file);

    void saveConfiguration(Configuration configuration, File file) throws IOException;
}
