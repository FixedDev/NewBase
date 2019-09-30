package me.fixeddev.base.api.configuration;

import me.fixeddev.minecraft.config.Configuration;

import java.io.File;
import java.io.IOException;

public class NullConfigurationFactory implements ConfigurationFactory{
    @Override
    public Configuration getConfig(File file) {
        return new NullConfiguration();
    }

    @Override
    public Configuration getConfig(String fileName) {
        return new NullConfiguration();
    }

    @Override
    public void saveConfiguration(Configuration configuration, File file) {
        // don't do anything
    }

    @Override
    public void saveConfiguration(Configuration configuration, String fileName) throws IOException {

    }
}
