package de.uulm.sopra.team08.data.entity;

import de.uulm.sopra.team08.data.item.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GooseTest {

    @Test
    void utilFunctions() {
        final Goose goose = new Goose();
        final RealityStone realityStone = new RealityStone(1);
        // add to inventory
        goose.addToInventory(realityStone);
        final InfinityStone[] inventory = goose.getInventory();
        assertEquals(realityStone, inventory[0]);
        // contains
        assertTrue(goose.contains(realityStone));
        assertFalse(goose.contains(new MindStone(1)));
        // remove from inv
        goose.removeFromInventory(realityStone);
        assertFalse(goose.contains(realityStone));
        goose.addToInventory(realityStone);
        goose.removeFromInventory(0);
        assertFalse(goose.contains(realityStone));
        // isFull
        assertFalse(goose.isFull());
        goose.addToInventory(new RealityStone(1));
        goose.addToInventory(new MindStone(1));
        goose.addToInventory(new PowerStone(1));
        goose.addToInventory(new SoulStone(1));
        goose.addToInventory(new SpaceStone(1));
        goose.addToInventory(new TimeStone(1));
        assertTrue(goose.isFull());
        // spitOut
        goose.removeFromInventory(0);
        goose.removeFromInventory(0);
        goose.removeFromInventory(0);
        goose.removeFromInventory(0);
        goose.removeFromInventory(0);
        goose.removeFromInventory(0);
        assertThrows(IllegalStateException.class, goose::spitOut);
        goose.addToInventory(realityStone);
        assertEquals(realityStone, goose.spitOut());
    }

}