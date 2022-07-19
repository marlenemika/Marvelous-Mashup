package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.event.GoodbyeClient;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class ReplayTest {

    private final static String json = "{\"nameP1\":\"p1\",\"nameP2\":\"p2\",\"config\":{\"character\":{\"characters\":[{\"characterID\":6,\"name\":\"Captain America\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":14,\"name\":\"Starlord\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":3,\"name\":\"Hulk\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":23,\"name\":\"Jessica Jones \",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":8,\"name\":\"Dr. Strange\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":16,\"name\":\"Ant Man\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":19,\"name\":\"Loki\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":9,\"name\":\"Iron Man\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":17,\"name\":\"Vision\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":15,\"name\":\"Gamora\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":18,\"name\":\"Deadpool\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":13,\"name\":\"Groot\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":21,\"name\":\"Mantis\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":12,\"name\":\"Captain Marvel\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":7,\"name\":\"Spiderman\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":10,\"name\":\"Black Panther\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":22,\"name\":\"Ghost Rider \",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":11,\"name\":\"Thor\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":24,\"name\":\"Scarlet Witch\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":20,\"name\":\"Silver Surfer\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}]},\"match\":{\"maxRounds\":30,\"maxRoundTime\":300,\"maxGameTime\":1800,\"maxAnimationTime\":50,\"spaceStoneCD\":2,\"mindStoneCD\":1,\"realityStoneCD\":3,\"powerStoneCD\":1,\"timeStoneCD\":5,\"soulStoneCD\":5,\"mindStoneDMG\":12,\"maxPauseTime\":60,\"maxResponseTime\":20},\"scenario\":{\"scenario\":[[\"GRASS\",\"GRASS\",\"ROCK\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"ROCK\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"],[\"ROCK\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"]],\"name\":\"examplescenarioconfig\",\"author\":\"Alice\"}},\"events\":[{\"message\":\"test\",\"messageType\":\"GOODBYE_CLIENT\"}]}";

    @Test
    void toJson() throws IOException, ConfigValidationException {
        Replay.getInstance().resetEvents();
        final Config config = new Config(new File("src/test/resources/de/uulm/sopra/team08/server/config/partieexample.game.json"),
                new File("src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.test.json"),
                new File("src/test/resources/de/uulm/sopra/team08/server/config/scenarioexample.scenario.json"));

        Replay.getInstance().setConfig(config);
        Replay.getInstance().setNameP1("p1");
        Replay.getInstance().setNameP2("p2");
        Replay.getInstance().addEvent(new GoodbyeClient("test"));

        Replay.getInstance().setLocation(new File("src/test/resources/de/uulm/sopra/team08/server/replay"));
        assertTrue(Replay.getInstance().saveFile());

        final byte[] bytes = new FileInputStream("src/test/resources/de/uulm/sopra/team08/server/replay/p1_vs_p2_replay.json").readAllBytes();
        assertEquals(json, new String(bytes));

        // invalid
        Replay.getInstance().setLocation(new File("DoesNotExist"));
        assertFalse(Replay.getInstance().saveFile());
    }

}