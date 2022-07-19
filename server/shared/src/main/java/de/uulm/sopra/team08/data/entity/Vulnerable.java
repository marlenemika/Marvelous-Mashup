package de.uulm.sopra.team08.data.entity;

/**
 * This interface provides differentiation between vulnerable
 * and invulnerable entities.
 * <p>
 * Users of this interface need to provide some attack functionality
 * if an entity is vulnerable.
 *
 * @author Jan-Philipp
 */
public interface Vulnerable {

    /**
     * Returns true if this entity can be attacked.
     *
     * @return true if this entity can be attacked
     */
    default boolean isVulnerable() {
        return false;
    }

}
