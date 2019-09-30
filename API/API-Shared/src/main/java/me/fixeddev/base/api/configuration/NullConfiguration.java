package me.fixeddev.base.api.configuration;

import me.fixeddev.minecraft.config.Configuration;

import java.util.Collection;

public class NullConfiguration implements Configuration {
    @Override
    public int getInt(String s, int i) {
        return 0;
    }

    @Override
    public String getString(String s, String s1) {
        return s1;
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return b;
    }

    @Override
    public Collection<?> getList(String s, Collection<?> collection) {
        return collection;
    }
}
