package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.item.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TieBreakTest {

    @Test
    void useTieBreakTest() {
        TieBreak tieBreak = new TieBreak();

        tieBreak.updateDamageDealtToEnemies(EntityID.P1, 5);
        Assertions.assertEquals(EntityID.P1, tieBreak.useTieBreak());
        tieBreak.updateDamageDealtToEnemies(EntityID.P2, 6);
        Assertions.assertEquals(EntityID.P2, tieBreak.useTieBreak());
        tieBreak.updateDamageDealtToEnemies(EntityID.P1, 2);
        Assertions.assertEquals(EntityID.P1, tieBreak.useTieBreak());

        tieBreak.updateKnockOut(EntityID.P2);
        Assertions.assertEquals(EntityID.P2, tieBreak.useTieBreak());
        tieBreak.updateKnockOut(EntityID.P1);
        tieBreak.updateKnockOut(EntityID.P1);
        tieBreak.updateKnockOut(EntityID.P1);
        Assertions.assertEquals(EntityID.P1, tieBreak.useTieBreak());
        tieBreak.updateKnockOut(EntityID.P2);
        Assertions.assertEquals(EntityID.P1, tieBreak.useTieBreak());
        tieBreak.updateKnockOut(EntityID.P2);
        tieBreak.updateKnockOut(EntityID.P2);
        Assertions.assertEquals(EntityID.P2, tieBreak.useTieBreak());

        List<Character> characterList = new ArrayList<>();
        final Character c0 = new Character("", 0, 0, 0, 0, 0, 0, EntityID.P1, 0);
        c0.addToInventory(new SoulStone(0));
        characterList.add(c0);
        tieBreak.updateInfinityStone(characterList);
        Assertions.assertEquals(EntityID.P1, tieBreak.useTieBreak());

        final Character c1 = new Character("", 0, 0, 0, 0, 0, 0, EntityID.P2, 0);
        c1.addToInventory(new TimeStone(0));
        c1.addToInventory(new PowerStone(0));
        characterList.add(c1);
        tieBreak.updateInfinityStone(characterList);
        Assertions.assertEquals(EntityID.P2, tieBreak.useTieBreak());

        final Character c2 = new Character("", 0, 0, 0, 0, 0, 0, EntityID.P1, 1);
        c2.addToInventory(new MindStone(0));
        characterList.add(c2);
        tieBreak.updateInfinityStone(characterList);
        Assertions.assertEquals(EntityID.P2, tieBreak.useTieBreak()); // second tiebreak

        c2.addToInventory(new SpaceStone(0));
        tieBreak.updateInfinityStone(characterList);
        Assertions.assertEquals(EntityID.P1, tieBreak.useTieBreak());
    }

}
