package me.fixeddev.base.commons.translations;

import me.fixeddev.inject.ProtectedModule;

public class TranslationsModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(TranslationManager.class);
    }
}
