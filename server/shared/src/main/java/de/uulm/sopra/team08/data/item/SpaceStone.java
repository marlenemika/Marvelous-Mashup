package de.uulm.sopra.team08.data.item;

/**
 * This stone allows the user to teleport on a free field.
 */
public class SpaceStone extends InfinityStone {

    public final static int ID = 0;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     */
    public SpaceStone(int cooldown) {
        super(cooldown, ID);
    }

}
