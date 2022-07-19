package de.uulm.sopra.team08.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.item.*;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for testing the parsing of JSON to Events
 */
public class EventsFromJsonTest {

    final Gson gson = new Gson();

    @Test
    void testGamestateEventFromJson() throws FileNotFoundException, ConfigValidationException {
        final Config config = new Config(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieexample.game.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterConfig.character.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioexample.scenario.json"));

        JsonObject gamestateEvent = gson.fromJson("{\"eventType\":\"GamestateEvent\",\"entities\":[{\"entityType\":\"Character\",\"name\":\"Gamora\",\"PID\":1,\"ID\":4,\"HP\":200,\"MP\":9,\"AP\":3,\"stones\":[],\"position\":[1,0]},{\"entityType\":\"Character\",\"name\":\"Ironman\",\"PID\":2,\"ID\":4,\"HP\":200,\"MP\":5,\"AP\":8,\"stones\":[0,1,2,3,4,5],\"position\":[2,1]},{\"entityType\":\"NPC\",\"ID\":0,\"MP\":0,\"stones\":[],\"position\":[1,2]},{\"entityType\":\"NPC\",\"ID\":1,\"MP\":0,\"stones\":[],\"position\":[1,2]},{\"entityType\":\"NPC\",\"ID\":2,\"MP\":2,\"stones\":[],\"position\":[1,2]},{\"entityType\":\"InfinityStone\",\"ID\":4,\"position\":[1,2]},{\"entityType\":\"Rock\",\"HP\":100,\"ID\":45,\"position\":[0,3]}],\"mapSize\":[2,3],\"turnOrder\":[{\"entityID\":\"P1\",\"ID\":3},{\"entityID\":\"P1\",\"ID\":4},{\"entityID\":\"P2\",\"ID\":2},{\"entityID\":\"P2\",\"ID\":3},{\"entityID\":\"P2\",\"ID\":5},{\"entityID\":\"P1\",\"ID\":2}],\"activeCharacter\":{\"entityID\":\"P1\",\"ID\":4},\"stoneCooldowns\":[3,3,3,3,3,3],\"winCondition\":false}", JsonObject.class);


        JsonObject gamestateEventError = gson.fromJson("{\"eventType\":\"GamestateEvent\",\"entities\":[{\"entityType\":\"NPC\",\"ID\":100,\"MP\":2,\"stones\":[],\"position\":[1,2]}],\"mapSize\":[2,3],\"turnOrder\":[{\"entityID\":\"P1\",\"ID\":3},{\"entityID\":\"P1\",\"ID\":4},{\"entityID\":\"P2\",\"ID\":2},{\"entityID\":\"P2\",\"ID\":3},{\"entityID\":\"P2\",\"ID\":5},{\"entityID\":\"P1\",\"ID\":2}],\"activeCharacter\":{\"entityID\":\"P1\",\"ID\":4},\"stoneCooldowns\":[3,3,3,3,3,3],\"winCondition\":false}", JsonObject.class);

        Character gamora = new Character("Gamora", 9, 3, 200, 0, 0, 0, EntityID.P1, 4);
        gamora.setCoordinates(new Tuple<>(1, 0));

        Character ironman = new Character("Ironman", 5, 8, 200, 0, 0, 0, EntityID.P2, 4);
        ironman.setCoordinates(new Tuple<>(2, 1));
        ironman.addToInventory(new SpaceStone(3));
        ironman.addToInventory(new MindStone(3));
        ironman.addToInventory(new RealityStone(3));
        ironman.addToInventory(new PowerStone(3));
        ironman.addToInventory(new TimeStone(3));
        ironman.addToInventory(new SoulStone(3));

        Goose goose = new Goose();
        goose.setCoordinates(new Tuple<>(1, 2));

        StanLee stanLee = new StanLee();
        stanLee.setCoordinates(new Tuple<>(1, 2));

        Thanos thanos = new Thanos(2);
        thanos.setCoordinates(new Tuple<>(1, 2));

        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(4);
        infinityStoneEntity.setCoordinates(new Tuple<>(1, 2));

        Rock rock = new Rock(45);
        rock.setCoordinates(new Tuple<>(0, 3));
        List<Entity> list = Arrays.asList(gamora, ironman, goose, stanLee, thanos, infinityStoneEntity, rock);

        Tuple<Integer, Integer> mapSize = new Tuple<>(2, 3);

        List<Tuple<EntityID, Integer>> turnOrder = new ArrayList<>();
        turnOrder.add(new Tuple<>(EntityID.P1, 3));
        turnOrder.add(new Tuple<>(EntityID.P1, 4));
        turnOrder.add(new Tuple<>(EntityID.P2, 2));
        turnOrder.add(new Tuple<>(EntityID.P2, 3));
        turnOrder.add(new Tuple<>(EntityID.P2, 5));
        turnOrder.add(new Tuple<>(EntityID.P1, 2));


        Tuple<EntityID, Integer> activeCharacter = new Tuple<>(EntityID.P1, 4);

        List<Integer> stoneCooldowns = Arrays.asList(3, 3, 3, 3, 3, 3);

        assertEquals(GamestateEvent.fromJson(gamestateEvent, config).toJsonEvent(), new GamestateEvent(list, mapSize, turnOrder, activeCharacter, stoneCooldowns, false).toJsonEvent());

        assertThrows(IllegalArgumentException.class, () -> GamestateEvent.fromJson(gamestateEventError, config));
    }

    @Test
    void testTakenDamageEventFromJson() {
        JsonObject takenDamageEvent = gson.fromJson("{\"eventType\":\"TakenDamageEvent\",\"targetEntity\":{\"entityID\":\"Rocks\",\"ID\":6},\"targetField\":[69,42],\"amount\":10}", JsonObject.class);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.ROCKS, 6);
        Tuple<Integer, Integer> targetField = new Tuple<>(69, 42);
        assertEquals(TakenDamageEvent.fromJson(takenDamageEvent), new TakenDamageEvent(targetEntity, targetField, 10));
    }

    @Test
    void testHealedEventFromJson() {
        JsonObject healedEvent = gson.fromJson("{\"eventType\":\"HealedEvent\",\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"targetField\":[5,7],\"amount\":15}", JsonObject.class);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> targetField = new Tuple<>(5, 7);
        assertEquals(HealedEvent.fromJson(healedEvent), new HealedEvent(targetEntity, targetField, 15));
    }

    @Test
    void testConsumedAPEventFromJson() {
        JsonObject consumedAPEvent = gson.fromJson("{\"eventType\":\"ConsumedAPEvent\",\"targetEntity\":{\"entityID\":\"P1\",\"ID\":5},\"targetField\":[0,1],\"amount\":1}", JsonObject.class);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 5);
        Tuple<Integer, Integer> targetField = new Tuple<>(0, 1);
        assertEquals(ConsumedAPEvent.fromJson(consumedAPEvent), new ConsumedAPEvent(targetEntity, targetField, 1));
    }

    @Test
    void testConsumedMPEventFromJson() {
        JsonObject consumedMPEvent = gson.fromJson("{\"eventType\":\"ConsumedMPEvent\",\"targetEntity\":{\"entityID\":\"P2\",\"ID\":2},\"targetField\":[0,0],\"amount\":1}", JsonObject.class);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 2);
        Tuple<Integer, Integer> targetField = new Tuple<>(0, 0);
        assertEquals(ConsumedMPEvent.fromJson(consumedMPEvent), new ConsumedMPEvent(targetEntity, targetField, 1));
    }

    @Test
    void testSpawnEntityEventFromJson() throws FileNotFoundException, ConfigValidationException {
        final Config config = new Config(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieexample.game.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterConfig.character.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioexample.scenario.json"));
        JsonObject spawnCharacter = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Character\",\"name\":\"Gamora\",\"PID\":2,\"ID\":3,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[],\"position\":[14,22]}}", JsonObject.class);
        JsonObject spawnCharacterWrongPID = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Character\",\"name\":\"Gamora\",\"PID\":5,\"ID\":3,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[],\"position\":[14,22]}}", JsonObject.class);
        JsonObject spawnEntityWrongType = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"CoolCharacter\",\"name\":\"Gamora\",\"PID\":5,\"ID\":3,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[],\"position\":[14,22]}}", JsonObject.class);
        JsonObject spawnInfStone = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"InfinityStone\",\"ID\":3,\"position\":[12,13]}}", JsonObject.class);
        JsonObject spawnRock = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Rock\",\"HP\":100,\"ID\":62,\"position\":[1,3]}}", JsonObject.class);
        JsonObject spawnNPC0 = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"NPC\",\"ID\":0,\"MP\":0,\"stones\":[],\"position\":[3,3]}}", JsonObject.class);
        JsonObject spawnNPC1 = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"NPC\",\"ID\":1,\"MP\":0,\"stones\":[],\"position\":[4,4]}}", JsonObject.class);
        JsonObject spawnNPC2 = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"NPC\",\"ID\":2,\"MP\":3,\"stones\":[],\"position\":[5,5]}}", JsonObject.class);
        JsonObject spawnNPC3WrongID = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"NPC\",\"ID\":5,\"MP\":3,\"stones\":[],\"position\":[5,5]}}", JsonObject.class);

        JsonObject spawnNPC0AsCharacter = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Character\",\"name\":\"Goose\",\"PID\":2,\"ID\":0,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[1],\"position\":[3,3]}}", JsonObject.class);
        JsonObject spawnNPC1AsCharacter = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Character\",\"name\":\"Stan Lee\",\"PID\":2,\"ID\":1,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[2],\"position\":[4,4]}}", JsonObject.class);
        JsonObject spawnNPC2AsCharacter = gson.fromJson("{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Character\",\"name\":\"Thanos\",\"PID\":2,\"ID\":2,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[3],\"position\":[5,5]}}", JsonObject.class);

        Character gamora = new Character("Gamora", 3, 4, 120, 0, 0, 0, EntityID.P2, 3);
        gamora.setCoordinates(new Tuple<>(14, 22));

        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(3);
        infinityStoneEntity.setCoordinates(new Tuple<>(12, 13));

        Rock rock = new Rock(62);
        rock.setCoordinates(new Tuple<>(1, 3));

        Goose goose = new Goose();
        goose.setCoordinates(new Tuple<>(3, 3));

        StanLee stanLee = new StanLee();
        stanLee.setCoordinates(new Tuple<>(4, 4));

        Thanos thanos = new Thanos(3);
        thanos.setCoordinates(new Tuple<>(5, 5));


        assertEquals(SpawnEntityEvent.fromJson(spawnCharacter, config), new SpawnEntityEvent(gamora));
        assertThrows(IllegalArgumentException.class, () -> SpawnEntityEvent.fromJson(spawnCharacterWrongPID, config));
        assertThrows(IllegalArgumentException.class, () -> SpawnEntityEvent.fromJson(spawnEntityWrongType, config));
        assertEquals(SpawnEntityEvent.fromJson(spawnInfStone, config), new SpawnEntityEvent(infinityStoneEntity));
        assertEquals(SpawnEntityEvent.fromJson(spawnRock, config), new SpawnEntityEvent(rock));
        assertEquals(SpawnEntityEvent.fromJson(spawnNPC0, config), new SpawnEntityEvent(goose));
        assertEquals(SpawnEntityEvent.fromJson(spawnNPC1, config), new SpawnEntityEvent(stanLee));
        assertEquals(SpawnEntityEvent.fromJson(spawnNPC2, config), new SpawnEntityEvent(thanos));

        assertThrows(IllegalArgumentException.class, () -> SpawnEntityEvent.fromJson(spawnNPC3WrongID, config));

        goose.addToInventory(new MindStone(3));
        stanLee.addToInventory(new RealityStone(3));
        thanos.addToInventory(new PowerStone(3));

        assertEquals(SpawnEntityEvent.fromJson(spawnNPC0AsCharacter, config), new SpawnEntityEvent(goose));
        assertEquals(SpawnEntityEvent.fromJson(spawnNPC1AsCharacter, config).toJsonEvent(), new SpawnEntityEvent(stanLee).toJsonEvent());
        assertEquals(SpawnEntityEvent.fromJson(spawnNPC2AsCharacter, config), new SpawnEntityEvent(thanos));
    }

    @Test
    void testDestroyedEntityEventFromJson() {
        JsonObject destroyedEntityEvent = gson.fromJson("{\"eventType\":\"DestroyedEntityEvent\",\"targetField\":[3,5],\"targetEntity\":{\"entityID\":\"P1\",\"ID\":3}}", JsonObject.class);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 5);
        assertEquals(DestroyedEntityEvent.fromJson(destroyedEntityEvent), new DestroyedEntityEvent(targetField, targetEntity));
    }

    @Test
    void testMeleeAttackEventFromJson() {
        JsonObject meleeAttackEvent = gson.fromJson("{\"eventType\":\"MeleeAttackEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":4},\"originField\":[3,7],\"targetField\":[3,6]}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        assertEquals(MeleeAttackEvent.fromJson(meleeAttackEvent), new MeleeAttackEvent(originEntity, targetEntity, originField, targetField));
    }

    @Test
    void testRangedAttackEventFromJson() {
        JsonObject rangedAttackEvent = gson.fromJson("{\"eventType\":\"RangedAttackEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":4},\"originField\":[3,7],\"targetField\":[2,3]}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 3);
        assertEquals(RangedAttackEvent.fromJson(rangedAttackEvent), new RangedAttackEvent(originEntity, targetEntity, originField, targetField));
    }

    @Test
    void testMoveEventFromJson() {
        JsonObject moveEvent = gson.fromJson("{\"eventType\":\"MoveEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"originField\":[3,7],\"targetField\":[3,6]}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        assertEquals(MoveEvent.fromJson(moveEvent), new MoveEvent(originEntity, originField, targetField));
    }

    @Test
    void testExchangeInfinityStoneEventFromJson() {
        JsonObject exchangeInfinityStoneEvent = gson.fromJson("{\"eventType\":\"ExchangeInfinityStoneEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":4},\"originField\":[3,7],\"targetField\":[3,6],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":5}}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 5);
        assertEquals(ExchangeInfinityStoneEvent.fromJson(exchangeInfinityStoneEvent), new ExchangeInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType));
    }

    @Test
    void testUseInfinityStoneEventFromJson() {
        JsonObject useInfinityStoneEvent = gson.fromJson("{\"eventType\":\"UseInfinityStoneEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":3},\"originField\":[3,7],\"targetField\":[3,7],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":6}}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 7);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 6);
        assertEquals(UseInfinityStoneEvent.fromJson(useInfinityStoneEvent), new UseInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType));
    }

    @Test
    void testRoundSetupEventFromJson() {
        JsonObject roundSetupEvent = gson.fromJson("{\"eventType\":\"RoundSetupEvent\",\"roundCount\":4,\"characterOrder\":[{\"entityID\":\"P1\",\"ID\":2},{\"entityID\":\"P2\",\"ID\":4},{\"entityID\":\"P2\",\"ID\":2},{\"entityID\":\"P1\",\"ID\":4}]}", JsonObject.class);
        Tuple<EntityID, Integer> char1 = new Tuple<>(EntityID.P1, 2);
        Tuple<EntityID, Integer> char2 = new Tuple<>(EntityID.P2, 4);
        Tuple<EntityID, Integer> char3 = new Tuple<>(EntityID.P2, 2);
        Tuple<EntityID, Integer> char4 = new Tuple<>(EntityID.P1, 4);
        List<Tuple<EntityID, Integer>> characterOrder = new ArrayList<>(Arrays.asList(char1, char2, char3, char4));
        assertEquals(RoundSetupEvent.fromJson(roundSetupEvent), new RoundSetupEvent(4, characterOrder));
    }

    @Test
    void testTurnEventFromJson() {
        JsonObject turnEvent = gson.fromJson("{\"eventType\":\"TurnEvent\",\"turnCount\":6,\"nextCharacter\":{\"entityID\":\"P2\",\"ID\":4}}", JsonObject.class);
        Tuple<EntityID, Integer> nextCharacter = new Tuple<>(EntityID.P2, 4);
        assertEquals(TurnEvent.fromJson(turnEvent), new TurnEvent(6, nextCharacter));
    }

    @Test
    void testWinEventFromJson() {
        JsonObject winEvent = gson.fromJson("{\"eventType\":\"WinEvent\",\"playerWon\":1}", JsonObject.class);
        assertEquals(WinEvent.fromJson(winEvent), new WinEvent(1));
    }

    @Test
    void testTimeoutWarningEventFromJson() {
        JsonObject timeoutWarningEvent = gson.fromJson("{\"eventType\":\"TimeoutWarningEvent\",\"message\":\"You will be disconnected soon.\",\"timeLeft\":1337}", JsonObject.class);
        String message = "You will be disconnected soon.";
        assertEquals(TimeoutWarningEvent.fromJson(timeoutWarningEvent), new TimeoutWarningEvent(message, 1337));
    }

    @Test
    void testTimeoutEventFromJson() {
        JsonObject timeoutEvent = gson.fromJson("{\"eventType\":\"TimeoutEvent\",\"message\":\"You have been disconnected.\"}", JsonObject.class);
        String message = "You have been disconnected.";
        assertEquals(TimeoutEvent.fromJson(timeoutEvent), new TimeoutEvent(message));
    }

    /**
     * The following Events are LOGIN Events and were parsed with Gson, causing a different order of JSON Properties
     */

    @Test
    void testConfirmSelectionFromJson() {
        JsonObject confirmSelection = gson.fromJson("{\"selectionComplete\":true,\"messageType\":\"CONFIRM_SELECTION\"}", JsonObject.class);
        assertEquals(ConfirmSelection.fromJson(confirmSelection), new ConfirmSelection(true));
    }

    @Test
    void testErrorFromJson() {
        JsonObject error = gson.fromJson("{\"message\":\"There was an error!\",\"type\":1,\"messageType\":\"ERROR\"}", JsonObject.class);
        assertEquals(ErrorEvent.fromJson(error), new ErrorEvent("There was an error!", 1));
    }

    @Test
    void testGameAssignmentFromJson() {
        JsonObject gameAssignment = gson.fromJson("{\"gameID\":\"6a39c3cf-26d8-409e-a309-45590f38ec4f\",\"characterSelection\":[{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":110,\"MP\":3,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":3,\"name\":\"Hulk\",\"HP\":120,\"MP\":3,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":130,\"MP\":4,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":140,\"MP\":4,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":6,\"name\":\"Captain America\",\"HP\":150,\"MP\":5,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":7,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":8,\"name\":\"Quicksilver\",\"HP\":110,\"MP\":3,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":9,\"name\":\"Hulk\",\"HP\":120,\"MP\":3,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":10,\"name\":\"Black Widow\",\"HP\":130,\"MP\":4,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":11,\"name\":\"Hawkeye\",\"HP\":140,\"MP\":4,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":12,\"name\":\"Captain America\",\"HP\":150,\"MP\":5,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}],\"messageType\":\"GAME_ASSIGNMENT\"}", JsonObject.class);
        Object[] characterSelection = new Object[12];

        Gson gson = new Gson();
        characterSelection[0] = gson.fromJson("{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[1] = gson.fromJson("{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":110,\"MP\":3,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[2] = gson.fromJson("{\"characterID\":3,\"name\":\"Hulk\",\"HP\":120,\"MP\":3,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[3] = gson.fromJson("{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":130,\"MP\":4,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[4] = gson.fromJson("{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":140,\"MP\":4,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[5] = gson.fromJson("{\"characterID\":6,\"name\":\"Captain America\",\"HP\":150,\"MP\":5,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[6] = gson.fromJson("{\"characterID\":7,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[7] = gson.fromJson("{\"characterID\":8,\"name\":\"Quicksilver\",\"HP\":110,\"MP\":3,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[8] = gson.fromJson("{\"characterID\":9,\"name\":\"Hulk\",\"HP\":120,\"MP\":3,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[9] = gson.fromJson("{\"characterID\":10,\"name\":\"Black Widow\",\"HP\":130,\"MP\":4,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[10] = gson.fromJson("{\"characterID\":11,\"name\":\"Hawkeye\",\"HP\":140,\"MP\":4,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection[11] = gson.fromJson("{\"characterID\":12,\"name\":\"Captain America\",\"HP\":150,\"MP\":5,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);

        assertEquals(GameAssignment.fromJson(gameAssignment), new GameAssignment("6a39c3cf-26d8-409e-a309-45590f38ec4f", characterSelection));

        assertThrows(IllegalArgumentException.class, () -> new GameAssignment("test", new Object[4]));
        assertThrows(IllegalArgumentException.class, () -> new GameAssignment("test", new Object[12]));

        JsonObject gameAssignmentError = gson.fromJson("{\"gameID\":\"6a39c3cf-26d8-409e-a309-45590f38ec4f\",\"characterSelection\":[],\"messageType\":\"GAME_ASSIGNMENT\"}", JsonObject.class);

        assertThrows(IllegalArgumentException.class, () -> GameAssignment.fromJson(gameAssignmentError));
    }

    @Test
    void testGameStructureFromJson() throws FileNotFoundException, ConfigValidationException {
        JsonObject gameStructure = gson.fromJson("{\"assignment\":\"PlayerOne\",\"playerOneName\":\"Gandalf\",\"playerTwoName\":\"Bilbo\",\"playerOneCharacters\":[{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":3,\"name\":\"Hulk\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":6,\"name\":\"Captain America\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}],\"playerTwoCharacters\":[{\"characterID\":19,\"name\":\"Loki\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":20,\"name\":\"Silver Surfer\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":21,\"name\":\"Mantis\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":22,\"name\":\"Ghost Rider\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":23,\"name\":\"Jesica Jones\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":24,\"name\":\"Scarlet Witch\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}],\"matchconfig\":{\"maxRounds\":30,\"maxRoundTime\":300,\"maxGameTime\":1800,\"maxAnimationTime\":50,\"spaceStoneCD\":2,\"mindStoneCD\":1,\"realityStoneCD\":3,\"powerStoneCD\":1,\"timeStoneCD\":5,\"soulStoneCD\":5,\"mindStoneDMG\":12,\"maxPauseTime\":60,\"maxResponseTime\":20},\"scenarioconfig\":{\"scenario\":[[\"GRASS\",\"GRASS\",\"ROCK\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"ROCK\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"],[\"ROCK\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"]],\"name\":\"examplescenarioconfig\",\"author\":\"Alice\"},\"messageType\":\"GAME_STRUCTURE\"}", JsonObject.class);

        final Config config = new Config(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieexample.game.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterConfig.character.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioexample.scenario.json"));

        Object[] characterSelection1 = new Object[6];
        Gson gson = new Gson();
        characterSelection1[0] = gson.fromJson("{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection1[1] = gson.fromJson("{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection1[2] = gson.fromJson("{\"characterID\":3,\"name\":\"Hulk\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection1[3] = gson.fromJson("{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection1[4] = gson.fromJson("{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection1[5] = gson.fromJson("{\"characterID\":6,\"name\":\"Captain America\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);

        Object[] characterSelection2 = new Object[6];
        characterSelection2[0] = gson.fromJson("{\"characterID\":19,\"name\":\"Loki\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection2[1] = gson.fromJson("{\"characterID\":20,\"name\":\"Silver Surfer\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection2[2] = gson.fromJson("{\"characterID\":21,\"name\":\"Mantis\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection2[3] = gson.fromJson("{\"characterID\":22,\"name\":\"Ghost Rider\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection2[4] = gson.fromJson("{\"characterID\":23,\"name\":\"Jesica Jones\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}", de.uulm.sopra.team08.config.character.Character.class);
        characterSelection2[5] = gson.fromJson("{\"characterID\":24,\"name\":\"Scarlet Witch\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}", de.uulm.sopra.team08.config.character.Character.class);

        assertEquals(GameStructure.fromJson(gameStructure), new GameStructure("PlayerOne", "Gandalf", "Bilbo", characterSelection1, characterSelection2, config.getPartieConfig(), config.getScenarioConfig()));

        assertThrows(IllegalArgumentException.class, () -> new GameStructure("test", "test", "test", new Object[6], new Object[6], config.getPartieConfig(), config.getScenarioConfig()));
        assertThrows(IllegalArgumentException.class, () -> new GameStructure("PlayerOne", "test", "test", new Object[4], new Object[6], config.getPartieConfig(), config.getScenarioConfig()));
        assertThrows(IllegalArgumentException.class, () -> new GameStructure("PlayerOne", "test", "test", new Object[6], new Object[6], config.getPartieConfig(), config.getScenarioConfig()));
        assertThrows(IllegalArgumentException.class, () -> new GameStructure("PlayerOne", "test", "test", new Object[]{new de.uulm.sopra.team08.config.character.Character(), new de.uulm.sopra.team08.config.character.Character(), new de.uulm.sopra.team08.config.character.Character(), new de.uulm.sopra.team08.config.character.Character(), new de.uulm.sopra.team08.config.character.Character(), new de.uulm.sopra.team08.config.character.Character()}, new Object[6], config.getPartieConfig(), config.getScenarioConfig()));
    }

    @Test
    void testGeneralAssignmentFromJson() {
        JsonObject generalAssignment = gson.fromJson("{\"gameID\":\"6a39c3cf-26d8-409e-a309-45590f38ec4f\",\"messageType\":\"GENERAL_ASSIGNMENT\"}", JsonObject.class);

        assertEquals(GeneralAssignment.fromJson(generalAssignment), new GeneralAssignment("6a39c3cf-26d8-409e-a309-45590f38ec4f"));
    }

    @Test
    void testGoodbyeClientFromJson() {
        JsonObject goodbyeClient = gson.fromJson("{\"message\":\"Goodbye!\",\"messageType\":\"GOODBYE_CLIENT\"}", JsonObject.class);

        assertEquals(GoodbyeClient.fromJson(goodbyeClient), new GoodbyeClient("Goodbye!"));
    }

    @Test
    void testHelloClientFromJson() {
        JsonObject helloClient = gson.fromJson("{\"runningGame\":false,\"messageType\":\"HELLO_CLIENT\"}", JsonObject.class);

        assertEquals(HelloClient.fromJson(helloClient), new HelloClient(false));
    }

}
