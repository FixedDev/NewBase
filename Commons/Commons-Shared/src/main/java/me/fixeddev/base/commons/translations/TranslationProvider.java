package me.fixeddev.base.commons.translations;

import com.google.inject.Inject;
import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.minecraft.config.Configuration;

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

        configuration = factory.getConfig("language-" + language);
        configurationCache.put(language, configuration);

        return configuration;
    }
}
