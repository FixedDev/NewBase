package me.fixeddev.base.commons.translations;

import com.google.inject.Inject;
import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.minecraft.config.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationProvider {
    private ConfigurationFactory factory;
    private Configuration defaultLanguageConfiguration;

    private Map<String, Configuration> configurationCache = new ConcurrentHashMap<>();

    TranslationProvider(ConfigurationFactory factory, Configuration defaultLanguageConfiguration) {
        this.factory = factory;
        this.defaultLanguageConfiguration = defaultLanguageConfiguration;
    }

    public String getTranslation(String language, String path) {
        Configuration languageConfig = getConfiguration(language);

        if (languageConfig.getString(path, null) == null) {
            languageConfig = defaultLanguageConfiguration;
        }

        return languageConfig.getString(path,null);
    }

    private Configuration getConfiguration(String language) {
        Configuration configuration = configurationCache.get(language);

        if (configuration != null) {
            return configuration;
        }

        try {
            configuration = factory.getConfig("language-" + language);
        } catch (IOException e) {
            // We can't throw the normal exception, so we rethrow it as a RuntimeException
            throw new RuntimeException(e);
        }
        configurationCache.put(language, configuration);

        return configuration;
    }
}
