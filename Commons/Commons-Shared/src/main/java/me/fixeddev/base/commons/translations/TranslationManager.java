package me.fixeddev.base.commons.translations;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface TranslationManager {

    @NotNull
    String getDefaultLanguage();

    Optional<TranslatableMessage> getMessage(String path);
}
