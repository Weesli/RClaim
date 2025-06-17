package net.weesli.rclaim.api.model;

import net.weesli.rclaim.api.enums.Effect;

/**
 * Represents an effect applied to a claim. Effects can have levels,
 * can be enabled or disabled, and may have a maximum level limit.
 * This interface defines how a specific effect behaves within a claim context.
 */
public interface ClaimEffect {

    /**
     * Retrieves the type of effect applied to the claim.
     *
     * @return The {@link Effect} enum representing the effect.
     */
    Effect getEffect();

    /**
     * Gets the current level of the effect.
     * Higher levels may increase the effect's strength or duration.
     *
     * @return The current effect level.
     */
    int getLevel();

    /**
     * Gets the maximum allowable level for this effect.
     * This can be used to cap upgrades or bonuses.
     *
     * @return The maximum level.
     */
    int getMaxLevel();

    /**
     * Checks whether the effect is currently enabled for the claim.
     *
     * @return True if the effect is active; false otherwise.
     */
    boolean isEnabled();

    /**
     * Checks whether the effect has reached its maximum level.
     *
     * @return True if the current level is equal to the maximum level.
     */
    boolean isMaxLevel();

    /**
     * Enables or disables the effect on the claim.
     *
     * @param enabled True to enable the effect; false to disable it.
     */
    void setEnabled(boolean enabled);

    /**
     * Sets the current level of the effect.
     * Should typically be between 0 and {@link #getMaxLevel()}.
     *
     * @param level The new level for the effect.
     */
    void setLevel(int level);

    /**
     * Sets the maximum level that this effect can reach.
     *
     * @param maxLevel The upper limit for the effect's level.
     */
    void setMaxLevel(int maxLevel);
}
