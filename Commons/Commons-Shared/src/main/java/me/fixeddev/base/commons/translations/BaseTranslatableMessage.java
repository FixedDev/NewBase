package me.fixeddev.base.commons.translations;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTranslatableMessage implements TranslatableMessage {

    private String path;
    private Map<String, String> variableValues;
    private TranslationProvider translationProvider;

    BaseTranslatableMessage(String path, TranslationProvider translationProvider) {
        this.path = path;
        this.translationProvider = translationProvider;

        variableValues = new ConcurrentHashMap<>();
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }

    @NotNull
    @Override
    public String getMessageForLang(@NotNull String lang) {
        String translation = translationProvider.getTranslation(lang, path);
        for (String key : variableValues.keySet()) {
            String value = variableValues.get(key);

            translation = translation.replace("{"+key+"}", value);
        }

        return translation;
    }

    @NotNull
    @Override
    public TranslatableMessage setVariableValue(String key, String value) {
        if (value == null) {
            variableValues.remove(key);
        } else {
            variableValues.put(key, value);
        }

        return this;
    }

    @Override
    public boolean isImmutable() {
        return false;
    }
}
