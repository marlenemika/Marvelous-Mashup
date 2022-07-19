package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.item.*;
import de.uulm.sopra.team08.data.terrain.Board;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.req.*;
import de.uulm.sopra.team08.server.net.NetTestSettings;
import de.uulm.sopra.team08.server.net.NetworkManager;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;

class GameLogicTest {

    private static GameLogic gameLogic;
    private static ArrayList<Character> turnOrder;
    private static Board board;
    private static Player p1;
    private static Player p2;

    private static Thanos thanos;
    private static StanLee stanLee;
    private static Goose goose;


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

            board = (Board) field.get(gameLogic);
        } catch (Exception ignored) {
        }

        try {
            Field field = GameLogic.class.getDeclaredField("turnOrderCharacters");
            field.setAccessible(true);
            turnOrder = (ArrayList<Character>) field.get(gameLogic);
        } catch (Exception ignored) {
        }

        try {
            Field field = GameLogic.class.getDeclaredField("goose");
            field.setAccessible(true);
            goose = (Goose) field.get(gameLogic);
        } catch (Exception ignored) {
        }

        try {
            Field field = GameLogic.class.getDeclaredField("stanLee");
            field.setAccessible(true);
            stanLee = (StanLee) field.get(gameLogic);
        } catch (Exception ignored) {
        }


        for (int i = 0; i < 5; i++) {
            gameLogic.getPlayerCharacters().add(new Character("" + i, 1, 1, 1, 1, 1, 3, EntityID.P1, i));
        }
        gameLogic.getPlayerCharacters().add(new Character("6", 1, 1, 10, 1, 1, 3, EntityID.P1, 6));

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
        try {
            Field field = GameLogic.class.getDeclaredField("currentTurn");
            field.setAccessible(true);
            field.set(gameLogic, 0);
        } catch (Exception ignored) {
        }
    }


    @Test
    void nextToEachOtherTest() {
        Assertions.assertTrue(GameLogic.nextToEachOther(new Tuple<>(0, 0), new Tuple<>(1, 1)));
        Assertions.assertTrue(GameLogic.nextToEachOther(new Tuple<>(0, 0), new Tuple<>(1, 0)));
        Assertions.assertTrue(GameLogic.nextToEachOther(new Tuple<>(0, 0), new Tuple<>(0, 1)));
        Assertions.assertFalse(GameLogic.nextToEachOther(new Tuple<>(0, 0), new Tuple<>(2, 0)));
        Assertions.assertFalse(GameLogic.nextToEachOther(new Tuple<>(0, 0), new Tuple<>(2, 2)));
        Assertions.assertFalse(GameLogic.nextToEachOther(new Tuple<>(0, 0), new Tuple<>(0, 2)));
    }

    @Test
    void CheckMeleeAttackRequestTest() {
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(2, 3);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(1, 3);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(3, 2);
        Tuple<Integer, Integer> nullPos = new Tuple<>(1, 1);
        Character p1C0 = gameLogic.getPlayerCharacters().get(0);
        Character p1C1 = gameLogic.getPlayerCharacters().get(1);
        Character p2C6 = gameLogic.getPlayerCharacters().get(6);
        Character p2C7 = gameLogic.getPlayerCharacters().get(7);
        p1C0.setCoordinates(c0Pos);
        p1C1.setCoordinates(c1Pos);
        p2C6.setCoordinates(c6Pos);
        p2C7.setCoordinates(c7Pos);
        board.setEntityAt(p1C0, c0Pos);
        board.setEntityAt(p1C1, c1Pos);
        board.setEntityAt(p2C6, c6Pos);
        board.setEntityAt(p2C7, c7Pos);
        Rock r1 = new Rock(1);
        Rock r2 = new Rock(2);
        Tuple<Integer, Integer> r1Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> r2Pos = new Tuple<>(4, 4);
        r1.setCoordinates(r1Pos);
        r2.setCoordinates(r2Pos);
        board.setEntityAt(r1, r1Pos);
        board.setEntityAt(r2, r2Pos);


        MeleeAttackRequest sameField = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                0
        );

        MeleeAttackRequest fieldOutOfRange = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 2),
                c0Pos,
                new Tuple<>(4, 4),
                0
        );

        MeleeAttackRequest wrongPosition = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 2),
                new Tuple<>(3, 3),
                new Tuple<>(4, 4),
                0
        );

        MeleeAttackRequest friendlyFire = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                1
        );

        MeleeAttackRequest nullField = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                nullPos,
                1
        );

        MeleeAttackRequest enemyFieldDownLeft = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                c6Pos,
                1
        );

        MeleeAttackRequest p2NotHavingTurn = new MeleeAttackRequest(
                new Tuple<>(EntityID.P2, 6),
                new Tuple<>(EntityID.P1, 1),
                c6Pos,
                c1Pos,
                50
        );

        MeleeAttackRequest enemyFieldRight = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 7),
                c0Pos,
                c7Pos,
                100
        );

        MeleeAttackRequest rockFieldUP = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 1),
                c0Pos,
                new Tuple<>(2, 1),
                20
        );

        MeleeAttackRequest outOfAP = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 1),
                c0Pos,
                new Tuple<>(2, 1),
                20
        );

        MeleeAttackRequest knockedOutCharacter = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 7),
                c0Pos,
                c7Pos,
                20
        );

        turnOrder.add(p1C0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(fieldOutOfRange, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(wrongPosition, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(friendlyFire, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(nullField, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(enemyFieldDownLeft, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(p2NotHavingTurn, p2));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(enemyFieldRight, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(enemyFieldRight, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(rockFieldUP, p1));
        p1C0.updateUsedAP(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfAP, p1));
        p1C0.refillStats();
        p1C0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOutCharacter, p1));
    }

    @Test
    void checkRangedAttackTest() {
        Tuple<Integer, Integer> c0Pos = new Tuple<>(0, 0);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(0, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 0);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(0, 3);
        Tuple<Integer, Integer> c8Pos = new Tuple<>(4, 4);
        Tuple<Integer, Integer> nullPos = new Tuple<>(2, 2);
        Character p1C0 = gameLogic.getPlayerCharacters().get(0);
        Character p1C1 = gameLogic.getPlayerCharacters().get(1);
        Character p2C6 = gameLogic.getPlayerCharacters().get(6);
        Character p2C7 = gameLogic.getPlayerCharacters().get(7);
        Character p2C8 = gameLogic.getPlayerCharacters().get(8);
        p1C0.setCoordinates(c0Pos);
        p1C1.setCoordinates(c1Pos);
        p2C6.setCoordinates(c6Pos);
        p2C7.setCoordinates(c7Pos);
        p2C8.setCoordinates(c8Pos);
        board.setEntityAt(p1C0, c0Pos);
        board.setEntityAt(p1C1, c1Pos);
        board.setEntityAt(p2C6, c6Pos);
        board.setEntityAt(p2C7, c7Pos);
        board.setEntityAt(p2C8, c8Pos);
        Rock r1 = new Rock(1);
        Rock r2 = new Rock(2);
        Tuple<Integer, Integer> r1Pos = new Tuple<>(1, 2);
        Tuple<Integer, Integer> r2Pos = new Tuple<>(4, 0);
        r1.setCoordinates(r1Pos);
        r2.setCoordinates(r2Pos);
        board.setEntityAt(r1, r1Pos);
        board.setEntityAt(r2, r2Pos);


        RangedAttackRequest inRange = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                c6Pos,
                1
        );

        RangedAttackRequest friendlyFire = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                1
        );

        RangedAttackRequest blockedSight = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 7),
                c0Pos,
                c7Pos,
                1
        );

        RangedAttackRequest outOfRange = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 8),
                c0Pos,
                c8Pos,
                1
        );

        RangedAttackRequest rockInRange = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 1),
                c0Pos,
                r1Pos,
                1
        );

        RangedAttackRequest nullField = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 2),
                c0Pos,
                nullPos,
                1
        );

        RangedAttackRequest meleeRange = new RangedAttackRequest(
                new Tuple<>(EntityID.P2, 6),
                new Tuple<>(EntityID.ROCKS, 2),
                c6Pos,
                r2Pos,
                1
        );

        RangedAttackRequest playerNotAuthorized = new RangedAttackRequest(
                new Tuple<>(EntityID.P2, 6),
                new Tuple<>(EntityID.ROCKS, 2),
                c6Pos,
                r2Pos,
                1
        );

        RangedAttackRequest knockedOut = new RangedAttackRequest(
                new Tuple<>(EntityID.P2, 6),
                new Tuple<>(EntityID.P1, 0),
                c6Pos,
                c0Pos,
                1
        );

        turnOrder.add(p1C0);
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(inRange, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(friendlyFire, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(blockedSight, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfRange, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(rockInRange, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(nullField, p1));
        turnOrder.set(0, p2C6);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(meleeRange, p2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(playerNotAuthorized, p1));
        p1C0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p2));
    }

    @Test
    void checkMoveTest() {
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 1);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> s0Pos = new Tuple<>(2, 3);
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Rock r0 = new Rock(0);
        InfinityStoneEntity s0 = new InfinityStoneEntity(0);
        p1c0.setCoordinates(c0Pos);
        p2c6.setCoordinates(c6Pos);
        r0.setCoordinates(r0Pos);
        s0.setCoordinates(s0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(r0, r0Pos);
        board.setEntityAt(s0, s0Pos);

        MoveRequest moveInRock = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos
        );

        MoveRequest moveInPlayer = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos
        );

        MoveRequest moveOnEmptyField = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(3, 2)
        );

        MoveRequest moveOnInfinityStone = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                s0Pos
        );

        MoveRequest outOfRange = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(4, 4)
        );

        MoveRequest playerNotAuthorized = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(1, 1)
        );

        MoveRequest knockedOut = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(4, 4)
        );

        MoveRequest playerNotHavingTurn = new MoveRequest(
                new Tuple<>(EntityID.P2, 6),
                c6Pos,
                c0Pos
        );

        MoveRequest sameField = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos
        );

        MoveRequest differentEntity = new MoveRequest(
                new Tuple<>(EntityID.P1, 2),
                c0Pos,
                c6Pos
        );

        MoveRequest outOfMP = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos
        );


        turnOrder.add(p1c0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(moveInRock, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(moveInPlayer, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(moveOnEmptyField, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(moveOnInfinityStone, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfRange, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(playerNotAuthorized, p2));
        p1c0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(playerNotHavingTurn, p2));
        p1c0.healMax();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(differentEntity, p1));
        p1c0.updateUsedMP(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfMP, p1));
    }

    @Test
    void checkExchangeTest() {
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(1, 1);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 2);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(1, 4);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 3);
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Rock r0 = new Rock(0);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(r0, r0Pos);
        p1c0.addToInventory(new MindStone(1));
        p1c1.addToInventory(new PowerStone(1));

        ExchangeInfinityStoneRequest giveToEnemy = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest giveToRock = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest giveToAlly = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest outOfRange = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 7),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest giveToNull = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 1),
                c0Pos,
                new Tuple<>(1, 2),
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest sameField = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest wrongID = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 3),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest playerNotAuthorized = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest outOfAP = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest knockedOut = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest hasNoStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                new Tuple<>(EntityID.P1, 0),
                c6Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        turnOrder.add(p1c0);
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(giveToEnemy, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(giveToRock, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(giveToAlly, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfRange, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(giveToNull, p1));
        p1c0.updateUsedAP(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfAP, p1));
        p1c0.refillStats();
        p1c0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(wrongID, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(playerNotAuthorized, p2));
        turnOrder.set(0, p2c6);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(hasNoStone, p2));
    }

    @Test
    void checkUseMindStoneTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Character p2c8 = gameLogic.getPlayerCharacters().get(8);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(3, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(1, 1);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(0, 0);
        Tuple<Integer, Integer> c8Pos = new Tuple<>(0, 3);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(2, 3);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        p2c8.setCoordinates(c8Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(p2c8, c8Pos);
        board.setEntityAt(r0, r0Pos);
        p1c0.addToInventory(new MindStone(2));

        UseInfinityStoneRequest characterNotHavingTurn = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest c6Targeted = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest c7Targeted = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest c8Targeted = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c8Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest rockTargeted = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest friendlyFire = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest outOfAP = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest knockedOut = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest sameField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest playerNotAuthorized = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest stoneOnCD = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        turnOrder.add(p1c0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(characterNotHavingTurn, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(c6Targeted, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(c7Targeted, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(c8Targeted, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(rockTargeted, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(friendlyFire, p1));
        p1c0.updateUsedAP(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfAP, p1));
        p1c0.refillStats();
        p1c0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p1));
        p1c0.healMax();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(playerNotAuthorized, p2));
        p1c0.getInventory()[0].resetCD();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneOnCD, p1));
    }

    @Test
    void checkUsePowerStoneTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 3);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(2, 4);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(1, 2);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(r0, r0Pos);
        p1c0.addToInventory(new PowerStone(1));


        UseInfinityStoneRequest sameField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );


        UseInfinityStoneRequest friendlyFire = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest playerNotAuthorized = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest useOnRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest useOnEnemy = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest outOfRange = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest nullField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(3, 2),
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest knockedOut = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest differentID = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest stoneOnCD = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        UseInfinityStoneRequest characterNotHavingTurn = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c6Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );


        UseInfinityStoneRequest stoneNotInInventory = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c6Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );


        turnOrder.add(p1c0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(friendlyFire, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(playerNotAuthorized, p2));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(useOnRock, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(useOnEnemy, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(outOfRange, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(nullField, p1));
        p1c0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p1));
        p1c0.healMax();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(differentID, p1));
        p1c0.getInventory()[0].resetCD();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneOnCD, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(characterNotHavingTurn, p2));
        turnOrder.set(0, p2c6);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneNotInInventory, p2));
    }

    @Test
    void checkUseSpaceStoneTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        InfinityStoneEntity s0 = new InfinityStoneEntity(0);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(0, 3);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(0, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 1);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 3);
        Tuple<Integer, Integer> s0Pos = new Tuple<>(4, 0);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p2c6.setCoordinates(c6Pos);
        s0.setCoordinates(s0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(r0, r0Pos);
        board.setEntityAt(s0, s0Pos);
        p1c0.addToInventory(new SpaceStone(2));

        UseInfinityStoneRequest sameField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest teleportOnFriendlyCharacter = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest teleportOnEnemyCharacter = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest teleportOnRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest teleportOnFreeField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(4, 4),
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest teleportOutOfBounds = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(10, 10),
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest teleportOnInfinityStone = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                s0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest knockedOut = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(4, 4),
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest stoneOnCD = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(4, 4),
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest characterNotHavingTurn = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                new Tuple<>(0, 0),
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest stoneNotInInventory = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                new Tuple<>(0, 0),
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );


        turnOrder.add(p1c0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(teleportOnFriendlyCharacter, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(teleportOnEnemyCharacter, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(teleportOnRock, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(teleportOnFreeField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(teleportOutOfBounds, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(teleportOnInfinityStone, p1));
        p1c0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p1));
        p1c0.healMax();
        p1c0.getInventory()[0].resetCD();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneOnCD, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(characterNotHavingTurn, p2));
        turnOrder.add(p1c1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneNotInInventory, p2));
    }

    @Test
    void checkUseRealityStoneTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p1c2 = gameLogic.getPlayerCharacters().get(2);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        InfinityStoneEntity stone = new InfinityStoneEntity(3);
        Rock r0 = new Rock(0);
        Rock r1 = new Rock(1);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> c2Pos = new Tuple<>(4, 0);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 1);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 2);
        Tuple<Integer, Integer> r1Pos = new Tuple<>(3, 4);
        Tuple<Integer, Integer> s0Pos = new Tuple<>(1, 3);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p1c2.setCoordinates(c2Pos);
        p2c6.setCoordinates(c6Pos);
        r0.setCoordinates(r0Pos);
        r1.setCoordinates(r1Pos);
        stone.setCoordinates(s0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(r0, r0Pos);
        board.setEntityAt(r1, r1Pos);
        board.setEntityAt(stone, s0Pos);
        p1c0.addToInventory(new RealityStone(1));
        p1c2.addToInventory(new RealityStone(1));

        UseInfinityStoneRequest sameField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest useOnEnemy = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest useOnAlly = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest useOnRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest useOnEmptyField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(1, 2),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest useOnStone = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                s0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest knockedOut = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(1, 1),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest stoneOnCD = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(1, 1),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest characterNotHavingTurn = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                new Tuple<>(1, 1),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest stoneNotInInventory = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                new Tuple<>(1, 1),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest useOutOfBounds = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                new Tuple<>(4, -1),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );


        turnOrder.add(p1c0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnEnemy, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnAlly, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(useOnRock, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(useOnEmptyField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnStone, p1));
        p1c0.damageCharacter(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(knockedOut, p1));
        p1c0.healMax();
        p1c0.getInventory()[0].resetCD();
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneOnCD, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneOnCD, p2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(characterNotHavingTurn, p2));
        turnOrder.set(0, p2c6);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(stoneNotInInventory, p2));
        turnOrder.set(0, p1c2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOutOfBounds, p2));
    }

    @Test
    void useSoulStoneTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p1c2 = gameLogic.getPlayerCharacters().get(2);
        Character p1c3 = gameLogic.getPlayerCharacters().get(3);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 0);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(3, 1);
        Tuple<Integer, Integer> c2Pos = new Tuple<>(1, 0);
        Tuple<Integer, Integer> c3Pos = new Tuple<>(0, 0);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(1, 1);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 0);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p1c2.setCoordinates(c2Pos);
        p1c3.setCoordinates(c3Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p1c2, c2Pos);
        board.setEntityAt(p1c3, c3Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        p1c0.addToInventory(new SoulStone(1));
        p1c1.damageCharacter(1);
        p1c3.damageCharacter(1);
        p2c6.damageCharacter(1);

        UseInfinityStoneRequest sameField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOnAliveAlly = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c2Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOnDeadAlly = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOutOfRange = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c3Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOnRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOutOfBounds = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(1, -1),
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOnAliveEnemy = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest useOnDeadEnemy = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        turnOrder.add(p1c0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnAliveAlly, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(useOnDeadAlly, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOutOfBounds, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnAliveEnemy, p1));
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(useOnDeadEnemy, p1));
    }

    @Test
    void useTimeStoneTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 2);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(r0, r0Pos);
        p1c0.addToInventory(new TimeStone(1));

        UseInfinityStoneRequest sameField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 4)
        );

        UseInfinityStoneRequest useOnRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 4)
        );

        UseInfinityStoneRequest useOnAlly = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 4)
        );

        UseInfinityStoneRequest useOnFreeField = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                new Tuple<>(1, 2),
                new Tuple<>(EntityID.INFINITYSTONES, 4)
        );

        turnOrder.add(p1c0);
        Assertions.assertDoesNotThrow(() -> gameLogic.checkRequest(sameField, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnRock, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnAlly, p1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkRequest(useOnFreeField, p1));

    }

    @Test
    void handlePauseRequestsTest() {
        gameLogic.gameTimer.startGameTimer();
        Assertions.assertDoesNotThrow(() -> gameLogic.checkPaused());
        gameLogic.handleRequest(new PauseStartRequest());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gameLogic.checkPaused());
        gameLogic.handleRequest(new PauseStopRequest());
        Assertions.assertDoesNotThrow(() -> gameLogic.checkPaused());
    }

    @Test
    void handleRoundSetupTest() {
        gameLogic.gameTimer.startGameTimer();

        // Rounds 1-6
        for (int i = 1; i <= 6; i++) {
            gameLogic.handleRoundSetup();
            Assertions.assertEquals(13, turnOrder.size());
            Assertions.assertTrue(turnOrder.contains(goose));
            Assertions.assertFalse(turnOrder.contains(stanLee));
        }

        // Round 7
        gameLogic.handleRoundSetup();
        Assertions.assertEquals(13, turnOrder.size());
        Assertions.assertTrue(turnOrder.contains(stanLee));
        Assertions.assertFalse(turnOrder.contains(goose));

        // Rounds 8-29
        for (int i = 8; i <= 29; i++) {
            gameLogic.handleRoundSetup();
            Assertions.assertEquals(12, turnOrder.size());
            Assertions.assertFalse(turnOrder.contains(goose));
            Assertions.assertFalse(turnOrder.contains(stanLee));
        }

        // Round 30 spawns Thanos
        gameLogic.handleRoundSetup();

        try {
            Field field = GameLogic.class.getDeclaredField("thanos");
            field.setAccessible(true);
            thanos = (Thanos) field.get(gameLogic);
        } catch (Exception ignored) {
        }

        Assertions.assertEquals(13, turnOrder.size());
        Assertions.assertTrue(turnOrder.contains(thanos));
        Assertions.assertFalse(turnOrder.contains(goose));
        Assertions.assertFalse(turnOrder.contains(stanLee));
    }

    @Test
    void handleStanLeeTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> tPos = new Tuple<>(1, 4);
        Tuple<Integer, Integer> sLPos = new Tuple<>(1, 2);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(3, 1);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(0, 0);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(2, 1);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p2c6.setCoordinates(c6Pos);
        r0.setCoordinates(r0Pos);
        if (thanos == null) thanos = new Thanos(1);
        thanos.setCoordinates(tPos);
        board.setEntityAt(r0, r0Pos);
        board.setEntityAt(thanos, tPos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        p1c0.damageCharacter(1);
        p1c1.damageCharacter(1);
        p2c6.damageCharacter(1);
        r0.damage(20);

        gameLogic.handleStanLee(sLPos);
        Assertions.assertEquals(1, p1c0.getCurrentHP());
        Assertions.assertEquals(0, p1c1.getCurrentHP());
        Assertions.assertEquals(1, p2c6.getCurrentHP());
        Assertions.assertEquals(80, r0.getHp());
    }

    @Test
    void handleMeleeAttackRequestTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(1, 3);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 2);
        Tuple<Integer, Integer> tPos = new Tuple<>(1, 1);
        if (thanos == null) thanos = new Thanos(1);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(r0, r0Pos);
        board.setEntityAt(thanos, tPos);


        MeleeAttackRequest attackRock = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 0),
                c0Pos,
                r0Pos,
                1
        );

        MeleeAttackRequest destroyRock = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 0),
                c0Pos,
                r0Pos,
                1
        );

        MeleeAttackRequest attackC6 = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                c6Pos,
                1
        );

        MeleeAttackRequest attackC7 = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 7),
                c0Pos,
                c7Pos,
                1
        );

        MeleeAttackRequest attackThanos = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.NPC, 2),
                c0Pos,
                tPos,
                1
        );


        turnOrder.add(p1c0);
        gameLogic.handleRequest(attackRock);
        Assertions.assertEquals(99, r0.getHp());
        Assertions.assertEquals(0, p1c0.getCurrentAP());
        r0.damage(98);
        p1c0.refillStats();
        gameLogic.handleRequest(destroyRock);
        Assertions.assertTrue(r0.isDestroyed());
        Assertions.assertNull(board.getEntityAt(r0Pos));
        p1c0.refillStats();
        gameLogic.handleRequest(attackC6);
        Assertions.assertTrue(p2c6.isKnockedOut());
        p1c0.refillStats();
        gameLogic.handleRequest(attackC7);
        Assertions.assertTrue(p2c7.isKnockedOut());
        p1c0.refillStats();
        gameLogic.handleRequest(attackThanos);
        Assertions.assertEquals(thanos.getMaxHP(), thanos.getCurrentAP());
    }


    @Test
    void handleRangedAttackRequestTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Rock r0 = new Rock(0);
        if (thanos == null) thanos = new Thanos(1);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(3, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(1, 4);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(2, 0);
        Tuple<Integer, Integer> tPos = new Tuple<>(1, 1);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 4);
        thanos.setCoordinates(tPos);
        p1c0.setCoordinates(c0Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(r0, r0Pos);
        board.setEntityAt(thanos, tPos);

        RangedAttackRequest attackRock = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 0),
                c0Pos,
                r0Pos,
                1
        );

        RangedAttackRequest destroyRock = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.ROCKS, 0),
                c0Pos,
                r0Pos,
                1
        );

        RangedAttackRequest attackThanos = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.NPC, 0),
                c0Pos,
                tPos,
                1
        );

        RangedAttackRequest attackC6 = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                c6Pos,
                1
        );

        RangedAttackRequest attackC7 = new RangedAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 7),
                c0Pos,
                c7Pos,
                1
        );


        turnOrder.add(p1c0);
        gameLogic.handleRequest(attackRock);
        Assertions.assertEquals(99, r0.getHp());
        Assertions.assertEquals(0, p1c0.getCurrentAP());
        p1c0.refillStats();
        r0.damage(98);
        gameLogic.handleRequest(destroyRock);
        Assertions.assertEquals(0, r0.getHp());
        Assertions.assertNull(board.getEntityAt(r0Pos));
        p1c0.refillStats();
        gameLogic.handleRequest(attackThanos);
        Assertions.assertEquals(thanos.getMaxHP(), thanos.getCurrentHP());
        p1c0.refillStats();
        gameLogic.handleRequest(attackC6);
        Assertions.assertEquals(0, p2c6.getCurrentHP());
        p1c0.refillStats();
        gameLogic.handleRequest(attackC7);
        Assertions.assertEquals(0, p2c7.getCurrentHP());
    }

    @Test
    void handleMoveRequestTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        InfinityStoneEntity stone = new InfinityStoneEntity(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(3, 1);
        Tuple<Integer, Integer> stonePos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> freeField = new Tuple<>(3, 2);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        stone.setCoordinates(stonePos);
        board.setEntityAt(stone, stonePos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);


        MoveRequest moveOnFreeField = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c0Pos,
                freeField
        );

        MoveRequest moveOnAllyField = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                freeField,
                c1Pos
        );

        MoveRequest moveOnInfinityStone = new MoveRequest(
                new Tuple<>(EntityID.P1, 0),
                c1Pos,
                stonePos
        );


        gameLogic.handleRequest(moveOnFreeField);
        Assertions.assertNull(board.getEntityAt(c0Pos));
        Assertions.assertEquals(p1c0, board.getEntityAt(freeField));
        Assertions.assertEquals(0, p1c0.getCurrentMP());
        p1c0.refillStats();
        gameLogic.handleRequest(moveOnAllyField);
        Assertions.assertEquals(p1c1, board.getEntityAt(new Tuple<>(3, 2)));
        Assertions.assertEquals(p1c0, board.getEntityAt(c1Pos));
        p1c0.refillStats();
        gameLogic.handleRequest(moveOnInfinityStone);
        Assertions.assertNull(board.getEntityAt(c1Pos));
        Assertions.assertEquals(p1c0, board.getEntityAt(stonePos));
        Assertions.assertTrue(p1c0.contains(new SpaceStone(2)));
    }

    @Test
    void handleExchangeInfinityStoneRequestTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(2, 1);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        MindStone mindStone = new MindStone(1);
        PowerStone powerStone = new PowerStone(1);
        RealityStone realityStone = new RealityStone(3);
        SoulStone soulStone = new SoulStone(5);
        SpaceStone spaceStone = new SpaceStone(2);
        TimeStone timeStone = new TimeStone(5);
        p1c0.addToInventory(mindStone);
        p1c0.addToInventory(powerStone);
        p1c0.addToInventory(realityStone);
        p1c0.addToInventory(soulStone);
        p1c0.addToInventory(spaceStone);
        p1c0.addToInventory(timeStone);

        ExchangeInfinityStoneRequest giveMindStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        ExchangeInfinityStoneRequest givePowerStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        ExchangeInfinityStoneRequest giveRealityStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        ExchangeInfinityStoneRequest giveSoulStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        ExchangeInfinityStoneRequest giveSpaceStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        ExchangeInfinityStoneRequest giveTimeStone = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P1, 1),
                c0Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 4)
        );

        gameLogic.handleRequest(giveMindStone);
        Assertions.assertTrue(p1c1.contains(mindStone));
        Assertions.assertEquals(0, p1c0.getCurrentAP());
        p1c0.refillStats();
        gameLogic.handleRequest(givePowerStone);
        Assertions.assertTrue(p1c1.contains(powerStone));
        p1c0.refillStats();
        gameLogic.handleRequest(giveRealityStone);
        Assertions.assertTrue(p1c1.contains(realityStone));
        p1c0.refillStats();
        gameLogic.handleRequest(giveSoulStone);
        Assertions.assertTrue(p1c1.contains(soulStone));
        p1c0.refillStats();
        gameLogic.handleRequest(giveTimeStone);
        Assertions.assertTrue(p1c1.contains(timeStone));
        p1c0.refillStats();
        gameLogic.handleRequest(giveSpaceStone);
        Assertions.assertTrue(p1c1.contains(spaceStone));
        p1c0.refillStats();
    }

    @Test
    void handleUseInfinityStoneTest() {
        Character p1c5 = gameLogic.getPlayerCharacters().get(5);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        Rock r0 = new Rock(0);
        Tuple<Integer, Integer> c5Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(1, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 0);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(4, 4);
        Tuple<Integer, Integer> r0Pos = new Tuple<>(3, 3);
        Tuple<Integer, Integer> freePos = new Tuple<>(4, 0);
        MindStone mindStone = new MindStone(1);
        PowerStone powerStone = new PowerStone(1);
        RealityStone realityStone = new RealityStone(3);
        SoulStone soulStone = new SoulStone(5);
        SpaceStone spaceStone = new SpaceStone(2);
        TimeStone timeStone = new TimeStone(5);
        p1c5.addToInventory(mindStone);
        p1c5.addToInventory(powerStone);
        p1c5.addToInventory(realityStone);
        p1c5.addToInventory(soulStone);
        p1c5.addToInventory(spaceStone);
        p1c5.addToInventory(timeStone);
        p1c5.setCoordinates(c5Pos);
        p1c1.setCoordinates(c1Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        r0.setCoordinates(r0Pos);
        board.setEntityAt(p1c5, c5Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(r0, r0Pos);

        UseInfinityStoneRequest destroyRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 5),
                c5Pos,
                r0Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest reviveAlly = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 5),
                c5Pos,
                c1Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 5)
        );

        UseInfinityStoneRequest teleport = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 5),
                c5Pos,
                freePos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        UseInfinityStoneRequest laser = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 5),
                freePos,
                c7Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 1)
        );

        UseInfinityStoneRequest placeRock = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 5),
                freePos,
                new Tuple<>(4, 1),
                new Tuple<>(EntityID.INFINITYSTONES, 2)
        );

        UseInfinityStoneRequest usePowerStone = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 5),
                freePos,
                c6Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 3)
        );

        gameLogic.handleRequest(destroyRock);
        Assertions.assertNull(board.getEntityAt(r0Pos));
        Assertions.assertFalse(p1c5.getInventory()[2].isOffCD());
        Assertions.assertEquals(0, p1c5.getCurrentAP());
        gameLogic.handleRequest(reviveAlly);
        Assertions.assertFalse(p1c5.getInventory()[3].isOffCD());
        Assertions.assertFalse(p1c1.isKnockedOut());
        gameLogic.handleRequest(teleport);
        Assertions.assertFalse(p1c5.getInventory()[4].isOffCD());
        Assertions.assertNull(board.getEntityAt(c5Pos));
        Assertions.assertEquals(p1c5, board.getEntityAt(freePos));
        gameLogic.handleRequest(laser);
        Assertions.assertFalse(p1c5.getInventory()[0].isOffCD());
        Assertions.assertTrue(p2c7.isKnockedOut());
        p1c5.getInventory()[0].reduceCD();
        p1c5.getInventory()[0].reduceCD();
        p1c5.getInventory()[0].reduceCD();
        gameLogic.handleRequest(placeRock);
        Assertions.assertFalse(p1c5.getInventory()[2].isOffCD());
        Assertions.assertTrue(board.getEntityAt(new Tuple<>(4, 1)) instanceof Rock);
        gameLogic.handleRequest(usePowerStone);
        Assertions.assertFalse(p1c5.getInventory()[1].isOffCD());
        Assertions.assertTrue(p2c6.isKnockedOut());
        Assertions.assertEquals(9, p1c5.getCurrentHP());
    }

    @Test
    void handleAttackedTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        MindStone mindStone = new MindStone(1);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(2, 2);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(3, 2);
        p1c0.setCoordinates(c0Pos);
        p2c6.setCoordinates(c6Pos);
        p2c6.addToInventory(mindStone);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p2c6, c6Pos);

        gameLogic.handleAttacked(p1c0, p2c6, 1);

        boolean droppedStone = false;
        for (int x = 0; x < board.getDimensions().first; x++) {
            for (int y = 0; y < board.getDimensions().second; y++) {
                Entity e = board.getEntityAt(new Tuple<>(x, y));
                if (e instanceof InfinityStoneEntity) {
                    if (e.getId() == 1) {
                        droppedStone = true;
                    }
                }
            }
        }

        Assertions.assertTrue(droppedStone);

    }

    @Test
    void handleTest() {
        Character p1c0 = gameLogic.getPlayerCharacters().get(0);
        Character p1c1 = gameLogic.getPlayerCharacters().get(1);
        Character p1c2 = gameLogic.getPlayerCharacters().get(2);
        Character p1c3 = gameLogic.getPlayerCharacters().get(3);
        Character p2c6 = gameLogic.getPlayerCharacters().get(6);
        Character p2c7 = gameLogic.getPlayerCharacters().get(7);
        InfinityStoneEntity stone = new InfinityStoneEntity(0);
        Tuple<Integer, Integer> c0Pos = new Tuple<>(1, 2);
        Tuple<Integer, Integer> c1Pos = new Tuple<>(3, 3);
        Tuple<Integer, Integer> c2Pos = new Tuple<>(1, 4);
        Tuple<Integer, Integer> c3Pos = new Tuple<>(4, 1);
        Tuple<Integer, Integer> c6Pos = new Tuple<>(1, 1);
        Tuple<Integer, Integer> c7Pos = new Tuple<>(2, 1);
        Tuple<Integer, Integer> stonePos = new Tuple<>(0, 4);
        Tuple<Integer, Integer> freeField = new Tuple<>(4, 0);
        p1c0.setCoordinates(c0Pos);
        p1c1.setCoordinates(c1Pos);
        p1c2.setCoordinates(c2Pos);
        p1c3.setCoordinates(c3Pos);
        p2c6.setCoordinates(c6Pos);
        p2c7.setCoordinates(c7Pos);
        stone.setCoordinates(stonePos);
        board.setEntityAt(p1c0, c0Pos);
        board.setEntityAt(p1c1, c1Pos);
        board.setEntityAt(p1c2, c2Pos);
        board.setEntityAt(p1c3, c3Pos);
        board.setEntityAt(p2c6, c6Pos);
        board.setEntityAt(p2c7, c7Pos);
        board.setEntityAt(stone, stonePos);

        try {
            Field field = GameLogic.class.getDeclaredField("logicState");
            field.setAccessible(true);
            field.set(gameLogic, GameLogic.LogicState.RUNNING);
        } catch (Exception ignore) {
        }

        try {
            Field field = GameLogic.class.getDeclaredField("gameTimer");
            field.setAccessible(true);
            ((GameTimer) field.get(gameLogic)).startGameTimer();
        } catch (Exception ignore) {
        }

        MeleeAttackRequest meleeAttackRequest = new MeleeAttackRequest(
                new Tuple<>(EntityID.P1, 0),
                new Tuple<>(EntityID.P2, 6),
                c0Pos,
                c6Pos,
                1
        );

        RangedAttackRequest rangedAttackRequest = new RangedAttackRequest(
                new Tuple<>(EntityID.P2, 7),
                new Tuple<>(EntityID.P1, 1),
                c7Pos,
                c1Pos,
                1
        );

        MoveRequest moveRequest = new MoveRequest(
                new Tuple<>(EntityID.P1, 2),
                c2Pos,
                stonePos
        );

        UseInfinityStoneRequest useInfinityStoneRequest = new UseInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 2),
                stonePos,
                freeField,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );

        ExchangeInfinityStoneRequest exchangeInfinityStoneRequest = new ExchangeInfinityStoneRequest(
                new Tuple<>(EntityID.P1, 2),
                new Tuple<>(EntityID.P1, 3),
                freeField,
                c3Pos,
                new Tuple<>(EntityID.INFINITYSTONES, 0)
        );


        turnOrder.add(p1c0);
        turnOrder.add(p2c7);
        turnOrder.add(p1c1);
        turnOrder.add(p1c2);

        Assertions.assertTrue(gameLogic.handle(p1, meleeAttackRequest));
        Assertions.assertTrue(gameLogic.handle(p1, new EndRoundRequest()));
        Assertions.assertTrue(gameLogic.handle(p2, rangedAttackRequest));
        Assertions.assertTrue(gameLogic.handle(p2, new EndRoundRequest()));
        Assertions.assertTrue(gameLogic.handle(p1, moveRequest));
        Assertions.assertTrue(gameLogic.handle(p1, new PauseStartRequest()));
        Assertions.assertTrue(gameLogic.handle(p1, new PauseStopRequest()));
        Assertions.assertTrue(gameLogic.handle(p1, useInfinityStoneRequest));
        Assertions.assertFalse(gameLogic.handle(p1, exchangeInfinityStoneRequest));
    }

    @Test
    void registerPlayerTests() {
        try {
            Field field = GameLogic.class.getDeclaredField("player1");
            field.setAccessible(true);
            field.set(gameLogic, null);
        } catch (Exception ignore) {
        }

        Assertions.assertTrue(gameLogic.registerPlayer(p1));
        Assertions.assertTrue(gameLogic.unregisterPlayer(p1));


        try {
            Field field = GameLogic.class.getDeclaredField("player1");
            field.setAccessible(true);
            field.set(gameLogic, p1);
        } catch (Exception ignore) {
        }
    }

}