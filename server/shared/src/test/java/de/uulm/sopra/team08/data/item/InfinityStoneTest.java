package de.uulm.sopra.team08.data.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InfinityStoneTest {

    @Test
    void reduceCDTest() {
        final MindStone mindStone = new MindStone(10);

        mindStone.reduceCD();
        Assertions.assertEquals(mindStone.getCurrentCD(), 0);

        mindStone.setCurrentCD(10);
        mindStone.reduceCD();
        Assertions.assertEquals(mindStone.getCurrentCD(), 9);
    }

    @Test
    void isOffCDTest() {
        final PowerStone powerStone = new PowerStone(5);

        Assertions.assertTrue(powerStone.isOffCD());

        powerStone.setCurrentCD(5);
        Assertions.assertFalse(powerStone.isOffCD());

        for (int i = 0; i < 5; i++) powerStone.reduceCD(); // reduce cd by 5
        Assertions.assertTrue(powerStone.isOffCD());
    }

    @Test
    void equalsTest() {
        final RealityStone realityStone = new RealityStone(2);
        final RealityStone realityStone2 = new RealityStone(2);
        final SoulStone soulStone = new SoulStone(2);

        Assertions.assertEquals(realityStone, realityStone2);
        Assertions.assertEquals(realityStone, realityStone);
        Assertions.assertNotEquals(realityStone, soulStone);
        Assertions.assertNotEquals(realityStone, null);
    }

}
