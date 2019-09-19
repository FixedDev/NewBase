package me.fixeddev.base.api.datamanager.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ObjectName {
    /**
     * Name used as data path of this object's type on the database
     */
    String value();
}
