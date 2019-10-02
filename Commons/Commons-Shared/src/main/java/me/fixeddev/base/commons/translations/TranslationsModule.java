package me.fixeddev.base.commons.translations;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;

public class TranslationsModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(TranslationManager.class).to(BaseTranslationManager.class).in(Scopes.SINGLETON);

        // Well, if the ConfigurationFactory is not exposed, then the TranslationManager
        // Is gonna use the ConfigurationFactory of the Commons
        // You may ask why this is in this way, well, that's because I think that all the
        // translations should go into the Commons
        expose(TranslationManager.class);
    }
}
