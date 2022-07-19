package de.uulm.sopra.team08.data.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class EntityTest {

    /**
     * tests the hashCode-equals contract
     */
    @Test
    void equalsVerifier() {
        final Entity e1 = new InfinityStoneEntity(1);
        final Entity e2 = new InfinityStoneEntity(1);
        final Entity e3 = new InfinityStoneEntity(2);
        Assertions.assertEquals(e1, e2);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

}