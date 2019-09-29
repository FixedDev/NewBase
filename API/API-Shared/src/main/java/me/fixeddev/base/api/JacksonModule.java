package me.fixeddev.base.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Provides;
import me.fixeddev.inject.ProtectedModule;

public class JacksonModule extends ProtectedModule {
    @Provides
    public ObjectMapper provideObjectMapper(){
        return new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
    }

    @Override
    protected void configure() {
        expose(ObjectMapper.class);
    }
}
