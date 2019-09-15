package me.fixeddev.base.api.user.permissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.fixeddev.base.api.datamanager.SavableObject;
import me.fixeddev.base.api.permissions.Permissible;

import java.time.LocalTime;

@JsonDeserialize(as = PojoPermissionsData.class)
public interface PermissionsData extends Permissible, SavableObject {

    @JsonIgnore
    String getPrimaryGroup();

    @JsonIgnore
    LocalTime getCalculationTime();
}
