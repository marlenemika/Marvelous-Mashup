package de.uulm.sopra.team08.data.entity;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.item.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class CharacterTest {

    private static MindStone mindStone;
    private static PowerStone powerStone;
    private static RealityStone realityStone;
    private static SoulStone soulStone;
    private static SpaceStone spaceStone;
    private static TimeStone timeStone;

    private Character character;

    @BeforeAll
    static void initAll() {
        mindStone = new MindStone(10);
        powerStone = new PowerStone(11);
        realityStone = new RealityStone(1);
        soulStone = new SoulStone(22);
        spaceStone = new SpaceStone(5);
        timeStone = new TimeStone(3);
    }


    @BeforeEach
    void initEach() {
        character = new Character("Loki", 2, 2, 100, 1, 1, 1, EntityID.P1, 100);
    }

    @Test
    void healCharacterTest() {
        character.damageCharacter(Integer.MAX_VALUE);

        character.healCharacter(1);
        Assertions.assertEquals(1, character.getCurrentHP());

        character.healCharacter(-30);
        Assertions.assertEquals(1, character.getCurrentHP());


        character.healCharacter(character.getMaxHP());
        Assertions.assertEquals(character.getMaxHP(), character.getCurrentHP());
    }

    @Test
    void damageCharacterTest() {
        character.damageCharacter(10);
        Assertions.assertEquals(90, character.getCurrentHP());

        character.damageCharacter(-1000);
        Assertions.assertEquals(90, character.getCurrentHP());

        character.damageCharacter(Integer.MAX_VALUE);
        Assertions.assertEquals(0, character.getCurrentHP());
    }

    @Test
    void updateUsedMPTest() {
        character.updateUsedMP(1);
        Assertions.assertEquals(1, character.getCurrentMP());

        character.updateUsedMP(100);
        Assertions.assertEquals(0, character.getCurrentMP());
    }

    @Test
    void updateUsedAPTest() {
        character.updateUsedAP(1);
        Assertions.assertEquals(1, character.getCurrentAP());

        character.updateUsedAP(100);
        Assertions.assertEquals(0, character.getCurrentAP());
    }

    @Test
    void refillStatsTest() {
        character.updateUsedAP(2);
        character.updateUsedMP(2);

        character.refillStats();

        Assertions.assertEquals(2, character.getCurrentMP());
        Assertions.assertEquals(2, character.getCurrentAP());
    }

    @Test
    void isKnockedOutTest() {
        character.damageCharacter(character.getMaxHP());
        Assertions.assertTrue(character.isKnockedOut());

        character.healCharacter(1);
        Assertions.assertFalse(character.isKnockedOut());
    }

    @Test
    void addToInventoryTest() {
        System.out.println(Arrays.toString(character.getInventory()));
        final InfinityStone[] inventory = {mindStone, realityStone, null, null, null, null};
        character.addToInventory(mindStone);
        character.addToInventory(mindStone);
        character.addToInventory(realityStone);

        Assertions.assertArrayEquals(inventory, character.getInventory());
    }

    @Test
    void removeFromInventory() {
        final InfinityStone[] inventory1 = {null, null, null, null, null, null};
        final InfinityStone[] inventory2 = {timeStone, spaceStone, null, null, null, null};
        character.addToInventory(timeStone);
        character.addToInventory(powerStone);
        character.addToInventory(spaceStone);

        character.removeFromInventory(powerStone);
        Assertions.assertArrayEquals(inventory2, character.getInventory());

        Assertions.assertEquals(character.removeFromInventory(0), timeStone);
        Assertions.assertEquals(character.removeFromInventory(0), spaceStone);
        character.removeFromInventory(soulStone);
        Assertions.assertArrayEquals(inventory1, character.getInventory());
    }

    @Test
    void isFullTest() {
        character.addToInventory(mindStone);
        character.addToInventory(powerStone);
        character.addToInventory(realityStone);
        character.addToInventory(soulStone);
        character.addToInventory(spaceStone);
        character.addToInventory(timeStone);
        Assertions.assertTrue(character.isFull());
    }

    @Test
    void isTurnFinished() {
        Assertions.assertFalse(character.isTurnFinished());
        character.updateUsedMP(2);
        character.updateUsedAP(2);
        Assertions.assertTrue(character.isTurnFinished());
    }

    /**
     * tests the hashCode-equals contract
     */
    @Test
    void equalsVerifier() {
        final Character character1 = new Character("test", 1, 1, 1, 1, 1, 1, EntityID.P1, 1);
        final Character character2 = new Character("test", 1, 1, 1, 1, 1, 1, EntityID.P1, 1);
        final Character character3 = new Character("test2", 1, 1, 1, 1, 1, 1, EntityID.P1, 1);
        Assertions.assertEquals(character1, character1);
        Assertions.assertNotEquals(character1, null);
        Assertions.assertEquals(character1, character2);
        Assertions.assertNotEquals(character1, character3);
        Assertions.assertEquals(character1.hashCode(), character2.hashCode());
    }

}
