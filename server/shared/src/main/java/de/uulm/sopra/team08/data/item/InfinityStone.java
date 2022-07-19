package de.uulm.sopra.team08.data.item;


import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;

import java.util.Objects;


/**
 * This class is a InfinityStone inside an inventory of an character
 */
public abstract class InfinityStone {

    protected final int cooldown;
    protected final int id;
    protected int currentCD;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     * @param id       the id of the corresponding infinityStoneEntity
     */
    public InfinityStone(int cooldown, int id) {
        this.cooldown = cooldown;
        this.currentCD = 0;

        this.id = id;
    }

    /**
     * Reduces currentCD by 1.
     */
    public void reduceCD() {
        this.currentCD = Math.max(0, this.currentCD - 1);
    }

    /**
     * Returns true if this infinity stone is ready to be used.
     *
     * @return true if this infinity stone is ready to be used
     */
    public boolean isOffCD() {
        return this.currentCD <= 0;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCurrentCD() {
        return currentCD;
    }

    protected void setCurrentCD(int currentCD) {
        this.currentCD = currentCD;
    }

    /**
     * Resets the current cooldown value to the cooldown of the infinity stone
     */
    public void resetCD() {
        this.currentCD = this.cooldown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfinityStone that = (InfinityStone) o;
        return cooldown == that.cooldown && currentCD == that.currentCD && id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cooldown, currentCD, id);
    }

    public int getId() {
        return id;
    }

    public Tuple<EntityID, Integer> getIDs() {
        return new Tuple<>(EntityID.INFINITYSTONES, id);
    }

}
