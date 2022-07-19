package de.uulm.sopra.team08.data.item;

/**
 * This stone revives a dead character on an adjacent field.
 */
public class SoulStone extends InfinityStone {

    public final static int ID = 5;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     */
    public SoulStone(int cooldown) {
        super(cooldown, ID);
    }

}
