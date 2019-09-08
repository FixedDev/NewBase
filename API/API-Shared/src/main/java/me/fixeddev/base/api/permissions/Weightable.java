package me.fixeddev.base.api.permissions;

/**
 * This interface represents something that can have a weight,
 * this applies for permissions and groups
 * The weight is basically an override system, something with more weight will override something with minus weight
 *
 * Permission 1: base.command.teleport - true - weight: 10
 * Permission 2: base.command.teleport - false - weight: 30
 * The permission 2 will override the permission 1, so "base.command.teleport" will be false
 */
public interface Weightable {
    /**
     * @return - The weight of this object
     */
    int getWeight();

    /**
     * @param weight - The weight of this object
     */
    void setWeight(int weight);
}
