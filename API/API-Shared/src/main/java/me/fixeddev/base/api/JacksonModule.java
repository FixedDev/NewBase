package me.fixeddev.base.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import me.fixeddev.inject.ProtectedModule;

public class JacksonModule extends ProtectedModule {
    @Provides
    public ObjectMapper provideObjectMapper(){
        return new ObjectMapper();
    }

    @Override
    protected void configure() {
        expose(ObjectMapper.class);
    }
}
