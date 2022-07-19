package de.uulm.sopra.team08.data.item;

/**
 * The user of this stone attacks a single target in melee range with double the melee damage.
 * Reduces users hp by 10%
 */
public class PowerStone extends InfinityStone {

    public final static int ID = 3;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     */
    public PowerStone(int cooldown) {
        super(cooldown, ID);
    }

}
