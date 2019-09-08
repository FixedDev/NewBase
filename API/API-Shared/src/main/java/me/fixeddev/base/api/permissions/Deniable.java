package me.fixeddev.base.api.permissions;

/**
 * This interface represents something that can be denied,
 * like a permission 
 */
public interface Deniable {
    /**
     * @return - If this object is denied
     */
    boolean isDenied();

    /**
     * @param denied - This specifies if this object must be denied
     */
    void setDenied(boolean denied);
}
