package de.uulm.sopra.team08.data.item;

/**
 * This stone creates a new rock on a free field or destroys a rock.
 */
public class RealityStone extends InfinityStone {

    public final static int ID = 2;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     */
    public RealityStone(int cooldown) {
        super(cooldown, ID);
    }

}
