package me.fixeddev.base.api.configuration;

import me.fixeddev.minecraft.config.Configuration;

import java.io.File;
import java.io.IOException;

public interface ConfigurationFactory {
    Configuration getConfig(File file) throws IOException;

    Configuration getConfig(String fileName) throws IOException;

    void saveConfiguration(Configuration configuration, File file) throws IOException;

    void saveConfiguration(Configuration configuration, String fileName) throws IOException;

}
