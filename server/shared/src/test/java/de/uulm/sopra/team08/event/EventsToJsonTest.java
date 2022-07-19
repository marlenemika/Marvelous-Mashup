package de.uulm.sopra.team08.event;

import com.google.gson.Gson;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.item.SpaceStone;
import de.uulm.sopra.team08.data.item.TimeStone;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for testing the parsing of Events to correct JSON
 */
class EventsToJsonTest {

    @Test
    void testEventsWithoutParameters() {
        String ack = "{\"eventType\":\"Ack\"}";
        assertEquals(ack, (new Ack()).toJsonEvent());

        String nack = "{\"eventType\":\"Nack\"}";
        assertEquals(nack, (new Nack()).toJsonEvent());

        String pauseStartEvent = "{\"eventType\":\"PauseStartEvent\"}";
        assertEquals(pauseStartEvent, (new PauseStartEvent()).toJsonEvent());

        String pauseStopEvent = "{\"eventType\":\"PauseStopEvent\"}";
        assertEquals(pauseStopEvent, (new PauseStopEvent()).toJsonEvent());

        String turnTimeoutEvent = "{\"eventType\":\"TurnTimeoutEvent\"}";
        assertEquals(turnTimeoutEvent, (new TurnTimeoutEvent()).toJsonEvent());

        String disconnectEvent = "{\"eventType\":\"DisconnectEvent\"}";
        assertEquals(disconnectEvent, (new DisconnectEvent()).toJsonEvent());
    }

    @Test
    void testGamestateEventToJson() {
        String gamestateEvent = "{\"eventType\":\"GamestateEvent\",\"entities\":[{\"entityType\":\"Character\",\"name\":\"Gamora\",\"PID\":1,\"ID\":4,\"HP\":200,\"MP\":9,\"AP\":3,\"stones\":[],\"position\":[1,0]},{\"entityType\":\"Character\",\"name\":\"Ironman\",\"PID\":2,\"ID\":4,\"HP\":200,\"MP\":5,\"AP\":8,\"stones\":[0,4],\"position\":[2,1]},{\"entityType\":\"InfinityStone\",\"ID\":4,\"position\":[1,2]},{\"entityType\":\"Rock\",\"HP\":100,\"ID\":45,\"position\":[0,3]}],\"mapSize\":[2,3],\"turnOrder\":[{\"entityID\":\"P1\",\"ID\":3},{\"entityID\":\"P1\",\"ID\":4},{\"entityID\":\"P2\",\"ID\":2},{\"entityID\":\"P2\",\"ID\":3},{\"entityID\":\"P2\",\"ID\":5},{\"entityID\":\"P1\",\"ID\":2}],\"activeCharacter\":{\"entityID\":\"P1\",\"ID\":4},\"stoneCooldowns\":[0,0,2,0,3,1],\"winCondition\":false}";
        Character gamora = new Character("Gamora", 9, 3, 200, 0, 0, 0, EntityID.P1, 4);
        gamora.setCoordinates(new Tuple<>(1, 0));

        Character ironman = new Character("Ironman", 5, 8, 200, 0, 0, 0, EntityID.P2, 4);
        ironman.setCoordinates(new Tuple<>(2, 1));
        ironman.addToInventory(new SpaceStone(2));
        ironman.addToInventory(new TimeStone(3));

        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(4);
        infinityStoneEntity.setCoordinates(new Tuple<>(1, 2));

        Rock rock = new Rock(45);
        rock.setCoordinates(new Tuple<>(0, 3));
        List<Entity> list = Arrays.asList(gamora, ironman, infinityStoneEntity, rock);

        Tuple<Integer, Integer> mapSize = new Tuple<>(2, 3);

        List<Tuple<EntityID, Integer>> turnOrder = new ArrayList<>();
        turnOrder.add(new Tuple<>(EntityID.P1, 3));
        turnOrder.add(new Tuple<>(EntityID.P1, 4));
        turnOrder.add(new Tuple<>(EntityID.P2, 2));
        turnOrder.add(new Tuple<>(EntityID.P2, 3));
        turnOrder.add(new Tuple<>(EntityID.P2, 5));
        turnOrder.add(new Tuple<>(EntityID.P1, 2));


        Tuple<EntityID, Integer> activeCharacter = new Tuple<>(EntityID.P1, 4);

        List<Integer> stoneCooldowns = Arrays.asList(0, 0, 2, 0, 3, 1);


        assertEquals(gamestateEvent, (new GamestateEvent(list, mapSize, turnOrder, activeCharacter, stoneCooldowns, false)).toJsonEvent());

    }

    @Test
    void testTakenDamageEventToJson() {
        String takenDamageEvent = "{\"eventType\":\"TakenDamageEvent\",\"targetEntity\":{\"entityID\":\"Rocks\",\"ID\":6},\"targetField\":[69,42],\"amount\":10}";
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.ROCKS, 6);
        Tuple<Integer, Integer> targetField = new Tuple<>(69, 42);
        assertEquals(takenDamageEvent, (new TakenDamageEvent(targetEntity, targetField, 10)).toJsonEvent());
    }

    @Test
    void testHealedEventToJson() {
        String healedEvent = "{\"eventType\":\"HealedEvent\",\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"targetField\":[5,7],\"amount\":15}";
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> targetField = new Tuple<>(5, 7);
        assertEquals(healedEvent, (new HealedEvent(targetEntity, targetField, 15)).toJsonEvent());
    }

    @Test
    void testConsumedAPEventToJson() {
        String consumedAPEvent = "{\"eventType\":\"ConsumedAPEvent\",\"targetEntity\":{\"entityID\":\"P1\",\"ID\":5},\"targetField\":[0,1],\"amount\":1}";
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 5);
        Tuple<Integer, Integer> targetField = new Tuple<>(0, 1);
        assertEquals(consumedAPEvent, (new ConsumedAPEvent(targetEntity, targetField, 1)).toJsonEvent());
    }

    @Test
    void testConsumedMPEventToJson() {
        String consumedMPEvent = "{\"eventType\":\"ConsumedMPEvent\",\"targetEntity\":{\"entityID\":\"P2\",\"ID\":2},\"targetField\":[0,0],\"amount\":1}";
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 2);
        Tuple<Integer, Integer> targetField = new Tuple<>(0, 0);
        assertEquals(consumedMPEvent, (new ConsumedMPEvent(targetEntity, targetField, 1)).toJsonEvent());
    }

    @Test
    void testSpawnEntityEventToJson() {
        String spawnCharacter = "{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Character\",\"name\":\"Gamora\",\"PID\":2,\"ID\":3,\"HP\":120,\"MP\":3,\"AP\":4,\"stones\":[],\"position\":[14,22]}}";
        String spawnInfStone = "{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"InfinityStone\",\"ID\":3,\"position\":[12,13]}}";
        String spawnRock = "{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"Rock\",\"HP\":100,\"ID\":62,\"position\":[1,3]}}";
        String spawnNPC = "{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"NPC\",\"ID\":0,\"MP\":0,\"stones\":[],\"position\":[3,3]}}";
        String spawnNPC2 = "{\"eventType\":\"SpawnEntityEvent\",\"entity\":{\"entityType\":\"NPC\",\"ID\":2,\"MP\":3,\"stones\":[],\"position\":[3,3]}}";

        Character gamora = new Character("Gamora", 3, 4, 120, 0, 0, 0, EntityID.P2, 3);
        gamora.setCoordinates(new Tuple<>(14, 22));

        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(3);
        infinityStoneEntity.setCoordinates(new Tuple<>(12, 13));

        Rock rock = new Rock(62);
        rock.setCoordinates(new Tuple<>(1, 3));

        Goose goose = new Goose();
        goose.setCoordinates(new Tuple<>(3, 3));

        Thanos thanos = new Thanos(3);
        thanos.setCoordinates(new Tuple<>(3, 3));

        assertEquals(spawnCharacter, (new SpawnEntityEvent(gamora)).toJsonEvent());
        assertEquals(spawnInfStone, (new SpawnEntityEvent(infinityStoneEntity)).toJsonEvent());
        assertEquals(spawnRock, (new SpawnEntityEvent(rock)).toJsonEvent());
        assertEquals(spawnNPC, (new SpawnEntityEvent(goose)).toJsonEvent());
        assertEquals(spawnNPC2, (new SpawnEntityEvent(thanos)).toJsonEvent());

    }

    @Test
    void testDestroyedEntityEventToJson() {
        String destroyedEntityEvent = "{\"eventType\":\"DestroyedEntityEvent\",\"targetField\":[3,5],\"targetEntity\":{\"entityID\":\"P1\",\"ID\":3}}";
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 5);
        assertEquals(destroyedEntityEvent, (new DestroyedEntityEvent(targetField, targetEntity)).toJsonEvent());
    }

    @Test
    void testMeleeAttackEventToJson() {
        String meleeAttackEvent = "{\"eventType\":\"MeleeAttackEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":4},\"originField\":[3,7],\"targetField\":[3,6]}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        assertEquals(meleeAttackEvent, (new MeleeAttackEvent(originEntity, targetEntity, originField, targetField)).toJsonEvent());
    }

    @Test
    void testRangedAttackEventToJson() {
        String rangedAttackEvent = "{\"eventType\":\"RangedAttackEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":4},\"originField\":[3,7],\"targetField\":[2,3]}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 3);
        assertEquals(rangedAttackEvent, (new RangedAttackEvent(originEntity, targetEntity, originField, targetField)).toJsonEvent());
    }

    @Test
    void testMoveEventToJson() {
        String moveEvent = "{\"eventType\":\"MoveEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"originField\":[3,7],\"targetField\":[3,6]}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        assertEquals(moveEvent, (new MoveEvent(originEntity, originField, targetField)).toJsonEvent());
    }

    @Test
    void testExchangeInfinityStoneEventToJson() {
        String exchangeInfinityStoneEvent = "{\"eventType\":\"ExchangeInfinityStoneEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":4},\"originField\":[3,7],\"targetField\":[3,6],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":5}}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 5);
        assertEquals(exchangeInfinityStoneEvent, (new ExchangeInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType)).toJsonEvent());
    }

    @Test
    void testUseInfinityStoneEventToJson() {
        String useInfinityStoneEvent = "{\"eventType\":\"UseInfinityStoneEvent\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":3},\"originField\":[3,7],\"targetField\":[3,7],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":6}}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 7);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 6);
        assertEquals(useInfinityStoneEvent, (new UseInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType)).toJsonEvent());
    }

    @Test
    void testRoundSetupEventToJson() {
        String roundSetupEvent = "{\"eventType\":\"RoundSetupEvent\",\"roundCount\":4,\"characterOrder\":[{\"entityID\":\"P1\",\"ID\":2},{\"entityID\":\"P2\",\"ID\":4},{\"entityID\":\"P2\",\"ID\":2},{\"entityID\":\"P1\",\"ID\":4}]}";
        Tuple<EntityID, Integer> char1 = new Tuple<>(EntityID.P1, 2);
        Tuple<EntityID, Integer> char2 = new Tuple<>(EntityID.P2, 4);
        Tuple<EntityID, Integer> char3 = new Tuple<>(EntityID.P2, 2);
        Tuple<EntityID, Integer> char4 = new Tuple<>(EntityID.P1, 4);
        List<Tuple<EntityID, Integer>> characterOrder = new ArrayList<>(Arrays.asList(char1, char2, char3, char4));
        assertEquals(roundSetupEvent, (new RoundSetupEvent(4, characterOrder)).toJsonEvent());
    }

    @Test
    void testTurnEventToJson() {
        String turnEvent = "{\"eventType\":\"TurnEvent\",\"turnCount\":6,\"nextCharacter\":{\"entityID\":\"P2\",\"ID\":4}}";
        Tuple<EntityID, Integer> nextCharacter = new Tuple<>(EntityID.P2, 4);
        assertEquals(turnEvent, (new TurnEvent(6, nextCharacter)).toJsonEvent());
    }

    @Test
    void testWinEventToJson() {
        String winEvent = "{\"playerWon\":1,\"eventType\":\"WinEvent\"}";
        assertEquals(winEvent, (new WinEvent(1)).toJsonEvent());
    }

    @Test
    void testTimeoutWarningEventToJson() {
        String timeoutWarningEvent = "{\"message\":\"You will be disconnected soon.\",\"timeLeft\":1337,\"eventType\":\"TimeoutWarningEvent\"}";
        String message = "You will be disconnected soon.";
        assertEquals(timeoutWarningEvent, (new TimeoutWarningEvent(message, 1337)).toJsonEvent());
    }

    @Test
    void testTimeoutEventToJson() {
        String timeoutEvent = "{\"message\":\"You have been disconnected.\",\"eventType\":\"TimeoutEvent\"}";
        String message = "You have been disconnected.";
        assertEquals(timeoutEvent, (new TimeoutEvent(message)).toJsonEvent());
    }

    /**
     * The following Events are LOGIN Events and were parsed with Gson, causing a different order of JSON Properties
     */

    @Test
    void testConfirmSelectionToJson() {
        String confirmSelection = "{\"selectionComplete\":true,\"messageType\":\"CONFIRM_SELECTION\"}";
        assertEquals(confirmSelection, (new ConfirmSelection(true)).toJsonEvent());
    }

    @Test
    void testErrorToJson() {
        String error = "{\"message\":\"There was an error!\",\"type\":1,\"messageType\":\"ERROR\"}";
        assertEquals(error, (new ErrorEvent("There was an error!", 1)).toJsonEvent());
    }

    @Test
    void testGameAssignmentToJson() {
        String gameAssignment = "{\"gameID\":\"6a39c3cf-26d8-409e-a309-45590f38ec4f\",\"characterSelection\":[{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":110,\"MP\":3,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":3,\"name\":\"Hulk\",\"HP\":120,\"MP\":3,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":130,\"MP\":4,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":140,\"MP\":4,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":6,\"name\":\"Captain America\",\"HP\":150,\"MP\":5,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":7,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":8,\"name\":\"Quicksilver\",\"HP\":110,\"MP\":3,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":9,\"name\":\"Hulk\",\"HP\":120,\"MP\":3,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":10,\"name\":\"Black Widow\",\"HP\":130,\"MP\":4,\"AP\":3,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":11,\"name\":\"Hawkeye\",\"HP\":140,\"MP\":4,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":12,\"name\":\"Captain America\",\"HP\":150,\"MP\":5,\"AP\":4,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}],\"messageType\":\"GAME_ASSIGNMENT\"}";
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

        assertEquals(gameAssignment, (new GameAssignment("6a39c3cf-26d8-409e-a309-45590f38ec4f", characterSelection)).toJsonEvent());
    }

    @Test
    void testGameStructureToJson() throws FileNotFoundException, ConfigValidationException {
        String gameStructure = "{\"assignment\":\"PlayerOne\",\"playerOneName\":\"Gandalf\",\"playerTwoName\":\"Bilbo\",\"playerOneCharacters\":[{\"characterID\":1,\"name\":\"Rocket Raccoon\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":2,\"name\":\"Quicksilver\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":3,\"name\":\"Hulk\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":4,\"name\":\"Black Widow\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":5,\"name\":\"Hawkeye\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":6,\"name\":\"Captain America\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5}],\"playerTwoCharacters\":[{\"characterID\":19,\"name\":\"Loki\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":20,\"name\":\"Silver Surfer\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":21,\"name\":\"Mantis\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":22,\"name\":\"Ghost Rider\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3},{\"characterID\":23,\"name\":\"Jesica Jones\",\"HP\":100,\"MP\":2,\"AP\":2,\"meleeDamage\":10,\"rangeCombatDamage\":30,\"rangeCombatReach\":5},{\"characterID\":24,\"name\":\"Scarlet Witch\",\"HP\":100,\"MP\":6,\"AP\":1,\"meleeDamage\":10,\"rangeCombatDamage\":10,\"rangeCombatReach\":3}],\"matchconfig\":{\"maxRounds\":30,\"maxRoundTime\":300,\"maxGameTime\":1800,\"maxAnimationTime\":50,\"spaceStoneCD\":2,\"mindStoneCD\":1,\"realityStoneCD\":3,\"powerStoneCD\":1,\"timeStoneCD\":5,\"soulStoneCD\":5,\"mindStoneDMG\":12,\"maxPauseTime\":60,\"maxResponseTime\":20},\"scenarioconfig\":{\"scenario\":[[\"GRASS\",\"GRASS\",\"ROCK\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"ROCK\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"],[\"ROCK\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"],[\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\",\"GRASS\"]],\"name\":\"examplescenarioconfig\",\"author\":\"Alice\"},\"messageType\":\"GAME_STRUCTURE\"}";

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

        assertEquals(gameStructure, (new GameStructure("PlayerOne", "Gandalf", "Bilbo", characterSelection1, characterSelection2, config.getPartieConfig(), config.getScenarioConfig())).toJsonEvent());

    }

    @Test
    void testGeneralAssignmentToJson() {
        String generalAssignment = "{\"gameID\":\"6a39c3cf-26d8-409e-a309-45590f38ec4f\",\"messageType\":\"GENERAL_ASSIGNMENT\"}";

        assertEquals(generalAssignment, (new GeneralAssignment("6a39c3cf-26d8-409e-a309-45590f38ec4f")).toJsonEvent());
    }

    @Test
    void testGoodbyeClientToJson() {
        String goodbyeClient = "{\"message\":\"Goodbye!\",\"messageType\":\"GOODBYE_CLIENT\"}";

        assertEquals(goodbyeClient, (new GoodbyeClient("Goodbye!")).toJsonEvent());
    }

    @Test
    void testHelloClientToJson() {
        String helloClient = "{\"runningGame\":false,\"messageType\":\"HELLO_CLIENT\"}";

        assertEquals(helloClient, (new HelloClient(false)).toJsonEvent());
    }

}
