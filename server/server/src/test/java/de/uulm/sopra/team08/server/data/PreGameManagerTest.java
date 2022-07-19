package de.uulm.sopra.team08.server.data;

import com.google.gson.Gson;
import de.uulm.sopra.team08.config.character.Character;
import de.uulm.sopra.team08.config.character.CharacterConfig;
import de.uulm.sopra.team08.util.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;

public class PreGameManagerTest {

    private static PreGameManager preGameManager;
    private static Player player1;
    private static Player player2;
    private static CharacterConfig characterConfig;

    @BeforeAll
    static void init() {
        player1 = new Player("1", "1", Role.PLAYER);
        player2 = new Player("2", "2", Role.PLAYER);
        File configFile = new File("src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.character.json");
        try {
            characterConfig = new Gson().fromJson(new BufferedReader(new FileReader(configFile)), CharacterConfig.class);
        } catch (FileNotFoundException ignore) {
            ignore.printStackTrace();
        }
    }

    @BeforeEach
    void initEach() {
        preGameManager = new PreGameManager(player1, player2);
    }

    @Test
    void splitTest() {
        var tuple = preGameManager.split(characterConfig);
        Assertions.assertEquals(12, new HashSet<>(tuple.first).size()); // player1 gets 12 characters
        Assertions.assertEquals(12, new HashSet<>(tuple.second).size()); // player2 gets 12 characters
        HashSet<Character> allAssignedCharacters = new HashSet<>();
        allAssignedCharacters.addAll(tuple.first);
        allAssignedCharacters.addAll(tuple.second);
        Assertions.assertEquals(24, allAssignedCharacters.size()); // all assigned characters should total to 24
        Assertions.assertNull(preGameManager.split(characterConfig));
    }

    @Test
    void playerPickedTest() {
        preGameManager.split(characterConfig);
        boolean[] p1 = {true, true, false, false, true, false, false, true, false, true, true, false};
        boolean[] p2_1 = {false, false, false, false, true, false, false, true, false, true, true, false};
        boolean[] p2_2 = {false, false, false, true, false, false, true, false, true, true, false};
        Assertions.assertTrue(preGameManager.playerPicked(player1, p1));
        Assertions.assertFalse(preGameManager.playerPicked(player2, p2_1));
        Assertions.assertFalse(preGameManager.playerPicked(player2, p2_2));
        Assertions.assertFalse(preGameManager.playerPicked(new Player("3", "3", Role.PLAYER), p1));
    }

    @Test
    void getGameAssignmentStringTest() {
        preGameManager.split(characterConfig);
        Assertions.assertEquals(12, preGameManager.getGameAssignment(player1, "1").getCharacterSelection().length);
    }

}
