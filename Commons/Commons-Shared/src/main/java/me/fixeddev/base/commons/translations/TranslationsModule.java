package me.fixeddev.base.commons.translations;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;

public class TranslationsModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(TranslationManager.class).to(BaseTranslationManager.class).in(Scopes.SINGLETON);
    }
}
