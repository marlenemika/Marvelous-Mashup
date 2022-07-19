package de.uulm.sopra.team08.data.item;


/**
 * This stone shoots a laser in a straight line without any range limit.
 * Deals damage based on character ranged damage.
 */
public class MindStone extends InfinityStone {

    public final static int ID = 1;

    /**
     * Parameterized constructor.
     *
     * @param cooldown the amount of turn this stone can not be used after usage
     */
    public MindStone(int cooldown) {
        super(cooldown, ID);
    }

}
