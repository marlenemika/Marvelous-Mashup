package de.uulm.sopra.team08.data.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThanosTest {

    @Test
    void refillStatsTest() {
        final Thanos thanos = new Thanos(1);

        thanos.updateUsedAP(10);
        thanos.updateUsedMP(10);

        thanos.refillStats();

        // 2 since it is Thanos second turn and he started with 1 MP
        Assertions.assertEquals(2, thanos.getCurrentMP());
        Assertions.assertEquals(thanos.getMaxAP(), thanos.getCurrentAP());

        thanos.refillStats();
        thanos.refillStats();

        // 4 since it is Thanos fourth turn and he started with 1 MP
        Assertions.assertEquals(4, thanos.getCurrentMP());
    }

    @Test
    void damageCharacterTest() {
        final Thanos thanos = new Thanos(1);
        thanos.damageCharacter(10000);

        Assertions.assertEquals(thanos.getMaxHP(), thanos.getCurrentHP());
    }

    @Test
    void healCharacterTest() {
        final Thanos thanos = new Thanos(1);
        thanos.healCharacter(10000);

        Assertions.assertEquals(thanos.getMaxHP(), thanos.getCurrentHP());
    }

}
