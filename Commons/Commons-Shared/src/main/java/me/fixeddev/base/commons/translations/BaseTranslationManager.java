package me.fixeddev.base.commons.translations;

import com.google.inject.Inject;
import me.fixeddev.base.api.configuration.ConfigurationFactory;
import me.fixeddev.minecraft.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BaseTranslationManager implements TranslationManager {

    private String defaultLanguage;
    private ConfigurationFactory factory;
    private Configuration defaultLanguageConfig;
    private TranslationProvider translationProvider;

    @Inject
    BaseTranslationManager(Configuration configuration, ConfigurationFactory factory) {
        this.defaultLanguage = configuration.getString("default-language", "en");
        this.factory = factory;

        defaultLanguageConfig = factory.getConfig("language-" + defaultLanguage);
        translationProvider = new TranslationProvider(factory, defaultLanguageConfig);
    }

    @NotNull
    @Override
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public Optional<TranslatableMessage> getMessage(String path) {
        if (defaultLanguageConfig.getString(path, null) == null) {
            return Optional.empty();
        }

        return Optional.of(new BaseTranslatableMessage(path, translationProvider));
    }
}
