package de.uulm.sopra.team08.event;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.entity.Entity;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GamestateEventTest {


    @Test
    void testInit() {
        assertDoesNotThrow(() -> new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 6}, true));
        // not enough stoneCooldowns
        assertThrows(IllegalArgumentException.class, () -> new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5}, true));
        assertThrows(IllegalArgumentException.class, () -> new GamestateEvent(new ArrayList<>(), new Tuple<>(1, 1), new ArrayList<>(), new Tuple<>(EntityID.P1, 1), new ArrayList<>(), true));
        // too much stoneCooldowns
        assertThrows(IllegalArgumentException.class, () -> new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 6, 7}, true));
        assertThrows(IllegalArgumentException.class, () -> new GamestateEvent(new ArrayList<>(), new Tuple<>(1, 1), new ArrayList<>(), new Tuple<>(EntityID.P1, 1), Arrays.asList(1, 2, 3, 4, 5, 6, 7), true));
    }

    /**
     * tests the hashCode-equals contract
     */
    @Test
    void equalsVerifier() {
        final var g1 = new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 6}, true);
        final var g2 = new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 6}, true);
        final var g3 = new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 1}, true);
        Assertions.assertEquals(g1, g2);
        Assertions.assertEquals(g1, g1);
        Assertions.assertNotEquals(g1, g3);
        // check some more branches
        Assertions.assertNotEquals(g1, new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 6}, false));
        Assertions.assertNotEquals(g1, new GamestateEvent(new Entity[0], new Tuple<>(1, 1), new Tuple[0], new Tuple<>(EntityID.P1, 2), new Integer[]{1, 2, 3, 4, 5, 6}, true));
        Assertions.assertNotEquals(g1, new GamestateEvent(new Entity[0], new Tuple<>(1, 2), new Tuple[0], new Tuple<>(EntityID.P1, 1), new Integer[]{1, 2, 3, 4, 5, 6}, true));

        Assertions.assertNotEquals(g1, null);
        Assertions.assertNotEquals(g1, "");
        Assertions.assertEquals(g1.hashCode(), g2.hashCode());
    }

}