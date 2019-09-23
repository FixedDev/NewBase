package me.fixeddev.base.commons;

import me.fixeddev.base.commons.translations.TranslationsModule;
import me.fixeddev.inject.ProtectedModule;

public class CommonsModule extends ProtectedModule {
    @Override
    protected void configure() {
        install(new TranslationsModule());
    }
}