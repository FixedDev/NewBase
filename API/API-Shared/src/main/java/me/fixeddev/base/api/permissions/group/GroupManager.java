package me.fixeddev.base.api.permissions.group;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Set;

public interface GroupManager {

    String DEFAULT_GROUP = "default";

    /**
     * Finds on the database a group with the specified name
     *
     * @param name The name of the group to search
     * @return The found group or null if a group with the name doesn't exist
     */
    ListenableFuture<Group> getGroupByName(String name);

    /**
     * Creates a group with the specified name
     *
     * @param name The name of the group to create
     * @return The created group or null if the group already exists
     */
    ListenableFuture<Group> createGroup(String name);

    /**
     * Creates a group with the specified name and weight
     *
     * @param name   The name of the group to create
     * @param weight The weight of the group to create
     * @return The created group or null if the group already exists
     */
    ListenableFuture<Group> createGroup(String name, int weight);

    /**
     * Checks on the database if a group with the specified name exists
     *
     * @param name The name to search
     * @return A boolean value representing if the group with specified name exists
     */
    ListenableFuture<Boolean> existsGroupWithName(String name);

    /**
     * Gets or creates the default group with name "default"
     * The group is created if the group isn't already created
     *
     * @return A group with name "default"
     */
    ListenableFuture<Group> getDefaultGroup();

    ListenableFuture<List<Group>> getAllGroups();

    /**
     * Searches a group with the specified name and deletes it
     *
     * @param name The name of the group to be deleted
     * @return The deleted group or null if the group doesn't exists
     */
    ListenableFuture<Group> deleteGroupByName(String name);

    void saveGroup(Group group);
}
