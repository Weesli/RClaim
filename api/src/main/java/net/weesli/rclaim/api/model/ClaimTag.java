package net.weesli.rclaim.api.model;

import java.util.List;
import java.util.UUID;

/**
 * Represents a tag associated with a claim. Tags are used to group users and permissions
 * together, providing a flexible way to manage claim access and roles.
 */
public interface ClaimTag {

    /**
     * Retrieves the unique ID of the claim that this tag is associated with.
     *
     * @return The claim ID.
     */
    String getClaimId();

    /**
     * Gets the unique identifier of this tag.
     *
     * @return The tag ID.
     */
    String getId();

    /**
     * Gets the display name of this tag, useful for UI representation or descriptive purposes.
     *
     * @return The display name of the tag.
     */
    String getDisplayName();

    /**
     * Retrieves the list of user UUIDs associated with this tag.
     *
     * @return List of user UUIDs.
     */
    List<UUID> getUsers();

    /**
     * Retrieves the list of permissions granted to users within this tag.
     *
     * @return List of permissions.
     */
    List<String> getPermissions();

    /**
     * Checks if the tag has a specific permission.
     *
     * @param key The permission to check.
     * @return True if the permission is present in the tag.
     */
    boolean hasPermission(String key);

    /**
     * Grants a new permission to the tag.
     *
     * @param key The permission to add.
     */
    void addPermission(String key);

    /**
     * Revokes a permission from the tag.
     *
     * @param key The permission to remove.
     */
    void removePermission(String key);

    /**
     * Checks whether the given user is part of this tag.
     *
     * @param uuid The UUID of the user to check.
     * @return True if the user is in the tag.
     */
    boolean hasUser(UUID uuid);

    /**
     * Adds a user to the tag.
     *
     * @param uuid The UUID of the user to add.
     */
    void addUser(UUID uuid);

    /**
     * Removes a user from the tag.
     *
     * @param uuid The UUID of the user to remove.
     */
    void removeUser(UUID uuid);
}
