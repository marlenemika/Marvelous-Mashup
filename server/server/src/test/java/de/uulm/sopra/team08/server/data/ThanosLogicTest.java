package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.InfinityStoneEntity;
import de.uulm.sopra.team08.data.entity.Thanos;
import de.uulm.sopra.team08.data.item.MindStone;
import de.uulm.sopra.team08.data.terrain.Board;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.server.net.NetTestSettings;
import de.uulm.sopra.team08.server.net.NetworkManager;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class ThanosLogicTest {

    private static GameLogic gameLogic;
    private static ArrayList<Character> turnOrder;
    private static Board board;
    private static Player p1;
    private static Player p2;


    private static ThanosLogic thanosLogic;
    private static Thanos thanos;

    @BeforeAll
    static void init() throws FileNotFoundException, ConfigValidationException {
        final File characterConfig = new File("src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.character.json");
        final File partieConfig = new File("src/test/resources/de/uulm/sopra/team08/server/config/partieexample.game.json");
        final File scenarioConfig = new File("src/test/resources/de/uulm/sopra/team08/server/config/scenarioexample.scenario.json");
        final Config config = new Config(partieConfig, characterConfig, scenarioConfig);


        p1 = new Player("Player 1", "1", Role.PLAYER);
        p2 = new Player("Player 2", "2", Role.PLAYER);
        gameLogic = new GameLogic(config);

        // make sure the NetworkManager is configured correctly
        if (!NetworkManager.isInitialized())
            NetworkManager.init(NetTestSettings.TEST_PORT, gameLogic, NetTestSettings.TEST_TIMEOUT_MILLIS);
        NetworkManager.getInstance().setLogic(gameLogic);

        try {
            Field field = GameLogic.class.getDeclaredField("gameBoard");
            field.setAccessible(true);

            field.set(gameLogic, new Board(10, 10));

            board = (Board) field.get(gameLogic);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        try {
            Field field = GameLogic.class.getDeclaredField("turnOrderCharacters");
            field.setAccessible(true);
            turnOrder = (ArrayList<Character>) field.get(gameLogic);
        } catch (Exception ignored) {
        }


        for (int i = 0; i < 6; i++) {
            gameLogic.getPlayerCharacters().add(new Character("" + i, 1, 1, 1, 1, 1, 3, EntityID.P1, i));
        }
        for (int i = 6; i < 12; i++) {
            gameLogic.getPlayerCharacters().add(new Character("" + i, 1, 1, 1, 1, 1, 3, EntityID.P2, i));
        }


        gameLogic.player1 = p1;
        gameLogic.player2 = p2;
    }

    @BeforeEach
    void clearBoard() {
        board.forEachPosition(pos -> board.freeEntityAt(pos));
        turnOrder.clear();
        gameLogic.getPlayerCharacters().forEach(Character::refillStats);
        gameLogic.getPlayerCharacters().forEach(Character::healMax);
        gameLogic.getPlayerCharacters().forEach(character -> character.getInventoryList().forEach(character::removeFromInventory));

        thanos = new Thanos(1);
        thanosLogic = new ThanosLogic(thanos, gameLogic);
        thanosLogic.resetMoveField();
    }


    @Test
    void fieldHasStoneTest() {
        MindStone mindStone = new MindStone(1);
        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(1);
        Character character = new Character("Thor", 2, 2, 100, 1, 1, 1, EntityID.P2, 100);
        board.setEntityAt(character, new Tuple<>(0, 0));
        board.setEntityAt(infinityStoneEntity, new Tuple<>(3, 2));

        // Character has no InfinityStone in his Inventory yet
        assertFalse(thanosLogic.fieldHasStone(0, 0));

        character.addToInventory(mindStone);

        assertTrue(thanosLogic.fieldHasStone(0, 0)); // Field with Character and Stone
        assertTrue(thanosLogic.fieldHasStone(3, 2)); // Field with mindStone
        assertFalse(thanosLogic.fieldHasStone(4, 5)); // Empty Field
        assertFalse(thanosLogic.fieldHasStone(-30, 1000)); // IndexOutOfBounds
    }

    @Test
    void searchIterationTest() {
        // Setup
        MindStone mindStone = new MindStone(1);
        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(1);
        Character character = new Character("Thor", 2, 2, 100, 1, 1, 1, EntityID.P2, 100);
        LinkedList<Tuple<Integer, Integer>> queue = new LinkedList<>();
        board.setEntityAt(character, new Tuple<>(0, 0));
        character.addToInventory(mindStone);
        board.setEntityAt(infinityStoneEntity, new Tuple<>(3, 2));
        queue.add(new Tuple<>(1, 0));
        thanosLogic.getMoveField()[1][0] = 0;

        // Check Field with InfinityStone
        assertTrue(thanosLogic.searchIteration(queue, -1, 0));
        assertTrue(queue.peek().first == 1 && queue.peek().second == 0);
        queue.poll();
        assertTrue(queue.peek().first == 0 && queue.peek().second == 0);
        assertEquals(thanosLogic.getMoveField()[0][0], 1);

        // Check Field out of bounds
        assertFalse(thanosLogic.searchIteration(queue, -1, 0));
        assertTrue(queue.peek().first == 0 && queue.peek().second == 0);
        assertEquals(queue.size(), 1);

        // Check Field without InfinityStone
        assertFalse(thanosLogic.searchIteration(queue, 0, 1));
        assertTrue(queue.peek().first == 0 && queue.peek().second == 0);
        assertEquals(queue.size(), 2);
        assertEquals(thanosLogic.getMoveField()[0][1], 2);

        queue.poll();

        // Check Field with InfinityStone again
        assertFalse(thanosLogic.searchIteration(queue, 0, -1));
        assertTrue(queue.peek().first == 0 && queue.peek().second == 1);
        assertEquals(queue.size(), 1);
        assertEquals(thanosLogic.getMoveField()[0][0], 1);
    }

    @Test
    void searchStoneTest() {
        MindStone mindStone = new MindStone(1);
        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(1);
        thanos.setX(0);
        thanos.setX(0);
        board.setEntityAt(infinityStoneEntity, new Tuple<>(3, 2));

        var tuple = thanosLogic.searchStone();
        assertNotNull(tuple); // method should find InfinityStone
        int x = tuple.first;
        int y = tuple.second;
        assertEquals(x, 3);
        assertEquals(y, 2);

        thanosLogic.resetMoveField();
        board.freeEntityAt(new Tuple<>(3, 2)); // remove InfinityStone
        tuple = thanosLogic.searchStone();
        assertNull(tuple); // no InfinityStone -> null
    }

    @Test
    void nextMoveIteration() {
        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(1);
        thanos.setX(0);
        thanos.setX(0);
        board.setEntityAt(infinityStoneEntity, new Tuple<>(4, 4));
        var tuple = thanosLogic.searchStone(); // stone should be at (4,4)

        // next closer field in thanos direction should be (3,3)
        tuple = thanosLogic.nextMoveIteration(tuple.first, tuple.second);
        assertTrue(tuple.first == 3 && tuple.second == 3);
    }

    @Test
    void findPathTest() {
        MindStone mindStone = new MindStone(1);
        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(1);
        board.setEntityAt(infinityStoneEntity, new Tuple<>(4, 4));

        // Thanos(MP = 1) and InfinityStone in opposing corners
        thanos.setX(0);
        thanos.setX(0);
        var result = thanosLogic.findPath();
        assertEquals(result.size(), 2);
        assertTrue(result.get(0).first == 0 && result.get(0).second == 0);
        assertTrue(result.get(1).first == 1 && result.get(1).second == 1); // Thanos should move to (1,1) with 1 MP

        // Thanos(MP = 4) and InfinityStone in opposing corners
        thanos.refillStats();
        thanos.refillStats();
        thanos.refillStats(); // Thanos now has 4 MP
        result = thanosLogic.findPath();
        assertEquals(result.size(), 5);
        assertTrue(result.get(0).first == 0 && result.get(0).second == 0);
        assertTrue(result.get(1).first == 1 && result.get(1).second == 1);
        assertTrue(result.get(2).first == 2 && result.get(2).second == 2);
        assertTrue(result.get(3).first == 3 && result.get(3).second == 3);
        assertTrue(result.get(4).first == 4 && result.get(4).second == 4);


        // Thanos next to InfinityStone
        thanos.setX(3);
        thanos.setY(4);
        result = thanosLogic.findPath();
        assertEquals(result.size(), 2);
        assertTrue(result.get(0).first == 3 && result.get(0).second == 4);
        assertTrue(result.get(1).first == 4 && result.get(1).second == 4);

        // Thanos closest to Character with InfinityStone
        Character character = new Character("Thor", 2, 2, 100, 1, 1, 1, EntityID.P2, 100);
        character.addToInventory(mindStone);
        thanos.setX(3);
        thanos.setY(1);
        board.setEntityAt(character, new Tuple<>(1, 0));
        result = thanosLogic.findPath();
        assertEquals(3, result.size());
        assertTrue(result.get(0).first == 3 && result.get(0).second == 1);
        assertTrue(result.get(1).first == 2 && result.get(1).second == 0);
        assertTrue(result.get(2).first == 1 && result.get(2).second == 0);

        // Rock between Thanos and InfinityStone
        Rock rock = new Rock(1);
        thanos.setX(4);
        thanos.setY(2);
        board.setEntityAt(rock, new Tuple<>(4, 3));
        result = thanosLogic.findPath();
        assertEquals(3, result.size());
        assertTrue(result.get(0).first == 4 && result.get(0).second == 2);
        assertTrue(result.get(1).first == 4 && result.get(1).second == 3);
        assertTrue(result.get(2).first == 4 && result.get(2).second == 4);
    }

    @Test
    void moveTest() {

        assertThrows(IllegalArgumentException.class, () -> thanosLogic.doTurn());

        // place everything on board
        final InfinityStoneEntity infinityStoneEntity1 = new InfinityStoneEntity(0);
        final InfinityStoneEntity infinityStoneEntity2 = new InfinityStoneEntity(2);
        final InfinityStoneEntity infinityStoneEntity3 = new InfinityStoneEntity(3);
        final InfinityStoneEntity infinityStoneEntity4 = new InfinityStoneEntity(4);
        final InfinityStoneEntity infinityStoneEntity5 = new InfinityStoneEntity(5);
        final Character cWithStone = new Character("c1", 1, 1, 1, 1, 1, 1, EntityID.P1, 1);
        cWithStone.addToInventory(new MindStone(1));
        final Character cWithoutStone = new Character("c2", 1, 1, 1, 1, 1, 1, EntityID.P1, 2);
        final Rock rock = new Rock(1);
        board.setEntityAt(infinityStoneEntity1, new Tuple<>(2, 2));
        board.setEntityAt(infinityStoneEntity2, new Tuple<>(2, 3));
        board.setEntityAt(cWithoutStone, new Tuple<>(2, 4));
        board.setEntityAt(cWithStone, new Tuple<>(2, 5));
        board.setEntityAt(rock, new Tuple<>(2, 6));
        board.setEntityAt(infinityStoneEntity3, new Tuple<>(2, 7));
        board.setEntityAt(infinityStoneEntity4, new Tuple<>(2, 8));
        board.setEntityAt(infinityStoneEntity5, new Tuple<>(2, 9));

        // first turn (1mp)
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(infinityStoneEntity1, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(infinityStoneEntity2, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(rock, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(infinityStoneEntity3, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 2 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(infinityStoneEntity2, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(rock, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(infinityStoneEntity3, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 3 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(rock, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(infinityStoneEntity3, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 4 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(rock, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(infinityStoneEntity3, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 5 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(rock, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(infinityStoneEntity3, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 6 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(infinityStoneEntity3, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 7 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(infinityStoneEntity4, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 8 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(infinityStoneEntity5, board.getEntityAt(new Tuple<>(2, 9)));

        // 9 turn (1mp)
        thanos.setMP(1);
        assertTrue(thanosLogic.doTurn());
        assertEquals(null, board.getEntityAt(new Tuple<>(0, 0)));
        assertEquals(null, board.getEntityAt(new Tuple<>(1, 1)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 2)));
        assertEquals(cWithoutStone, board.getEntityAt(new Tuple<>(2, 3)));
        assertEquals(cWithStone, board.getEntityAt(new Tuple<>(2, 4)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 5)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 6)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 7)));
        assertEquals(null, board.getEntityAt(new Tuple<>(2, 8)));
        assertEquals(thanos, board.getEntityAt(new Tuple<>(2, 9)));

        System.out.println(Arrays.toString(thanos.getInventory()));

        // are all dead?
        boolean finished = false;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!thanosLogic.doTurn()) {
                finished = true;
                break;
            }
        }
        assertTrue(finished);

    }

}
