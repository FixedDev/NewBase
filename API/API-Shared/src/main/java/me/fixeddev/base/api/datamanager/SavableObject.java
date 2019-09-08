package me.fixeddev.base.api.datamanager;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SavableObject {
    @JsonProperty("_id")
    String id();
}
