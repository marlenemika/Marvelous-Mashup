package de.uulm.sopra.team08.data.item;

/**
 * This stone resets the user's ap and mp.
 */
public class TimeStone extends InfinityStone {

    public final static int ID = 4;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     */
    public TimeStone(int cooldown) {
        super(cooldown, ID);
    }

}
