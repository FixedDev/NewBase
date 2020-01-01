package me.fixeddev.base.api.bukkit.configuration;

import me.fixeddev.minecraft.config.Configuration;

import java.util.Collection;

public class ConfigurationWrapper implements Configuration {
    private org.bukkit.configuration.Configuration config;

    public ConfigurationWrapper(org.bukkit.configuration.Configuration config) {
        this.config = config;
    }

    @Override
    public int getInt(String s, int i) {
        return config.getInt(s, i);
    }

    @Override
    public String getString(String s, String s1) {
        return config.getString(s, s1);
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return config.getBoolean(s, b);
    }

    @Override
    public Collection<?> getList(String s, Collection<?> collection) {
        return config.getList(s, collection);
    }
}
