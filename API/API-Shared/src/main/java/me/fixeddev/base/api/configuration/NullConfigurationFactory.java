package me.fixeddev.base.api.configuration;

import me.fixeddev.minecraft.config.Configuration;

import java.io.File;

public class NullConfigurationFactory implements ConfigurationFactory{
    @Override
    public Configuration getConfig(File file) {
        return new NullConfiguration();
    }

    @Override
    public void saveConfiguration(Configuration configuration, File file) {
        // don't do anything
    }
}
