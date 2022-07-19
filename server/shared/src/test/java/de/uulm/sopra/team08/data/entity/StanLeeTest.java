package de.uulm.sopra.team08.data.entity;

import de.uulm.sopra.team08.EntityID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StanLeeTest {

    @Test
    void healCharactersTest1() {
        final StanLee stanLee = new StanLee();
        final Character[] characters = new Character[5];
        for (int i = 0; i < characters.length; i++) {
            Character c = new Character("", 1, 1, 100, 1, 1, 1, EntityID.P1, 1);
            c.damageCharacter(c.getMaxHP());
            characters[i] = c;
        }

        stanLee.healCharacters(characters);

        for (Character c : characters)
            Assertions.assertEquals(c.getMaxHP(), c.getCurrentHP());
    }

    @Test
    void healCharactersTest2() {
        final StanLee stanLee = new StanLee();
        final Entity[] entities = new Entity[6];

        Character c1 = new Character("", 1, 1, 100, 1, 1, 1, EntityID.P1, 1);
        Character c2 = new Character("", 1, 1, 100, 1, 1, 1, EntityID.P1, 1);


        entities[0] = new InfinityStoneEntity(1);
        entities[1] = new Goose();
        entities[2] = c1;
        entities[3] = new InfinityStoneEntity(2);
        entities[4] = new Thanos(1);
        entities[5] = c2;

        c1.damageCharacter(20);
        c2.damageCharacter(20);

        stanLee.healCharacters(entities);

        Assertions.assertEquals(c1.getMaxHP(), c1.getCurrentHP());
        Assertions.assertEquals(c2.getMaxHP(), c2.getCurrentHP());
    }

}
