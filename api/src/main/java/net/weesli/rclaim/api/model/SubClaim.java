package net.weesli.rclaim.api.model;

import org.bukkit.Location;

/**
 * Represents a sub-claim, which is a smaller protected area within a main claim.
 * Auxiliary zones represent a stack of zones purchased separately from the main zone.
 */
public interface SubClaim {

    /**
     * Gets the identifier of the main claim this sub-claim belongs to.
     *
     * @return The main claim ID.
     */
    String getMainClaim();

    /**
     * Gets the X-coordinate of the sub-claim's center or origin point.
     *
     * @return The X-coordinate.
     */
    int getX();

    /**
     * Gets the Z-coordinate of the sub-claim's center or origin point.
     *
     * @return The Z-coordinate.
     */
    int getZ();

    /**
     * Checks if a given location is within the bounds of this sub-claim.
     *
     * @param location The location to check.
     * @return True if the location is inside this sub-claim.
     */
    boolean contains(Location location);
}
