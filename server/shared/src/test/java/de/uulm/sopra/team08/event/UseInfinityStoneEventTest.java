package de.uulm.sopra.team08.event;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UseInfinityStoneEventTest {

    @Test
    void testInit() {
        final Tuple<EntityID, Integer> entity = new Tuple<>(EntityID.P1, 1);
        final Tuple<Integer, Integer> field = new Tuple<>(1, 1);
        assertDoesNotThrow(() -> new UseInfinityStoneEvent(entity, entity, field, field, new Tuple<>(EntityID.INFINITYSTONES, 1)));

        // all other EntityID's
        assertThrows(IllegalArgumentException.class, () -> new UseInfinityStoneEvent(entity, entity, field, field, new Tuple<>(EntityID.P1, 1)));
        assertThrows(IllegalArgumentException.class, () -> new UseInfinityStoneEvent(entity, entity, field, field, new Tuple<>(EntityID.P2, 1)));
        assertThrows(IllegalArgumentException.class, () -> new UseInfinityStoneEvent(entity, entity, field, field, new Tuple<>(EntityID.NPC, 1)));
        assertThrows(IllegalArgumentException.class, () -> new UseInfinityStoneEvent(entity, entity, field, field, new Tuple<>(EntityID.ROCKS, 1)));

    }

}