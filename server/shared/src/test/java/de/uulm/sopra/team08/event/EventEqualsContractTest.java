package de.uulm.sopra.team08.event;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.config.character.Character;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * tests the hashCode-equals contract
 */
public class EventEqualsContractTest {

    @Test
    void generalEventTest() {
        Assertions.assertFalse(new MMIngameEvent(MMEvent.EventType.HELLO_CLIENT).getEventType().isIngame());
        Assertions.assertTrue(new MMLoginEvent(MMEvent.EventType.GAMESTATE).getEventType().isIngame());


        MMIngameEvent ingameEvent = new MMIngameEvent(MMEvent.EventType.TURN);
        MMIngameEvent ingameEvent2 = new MMIngameEvent(MMEvent.EventType.TURN);
        MMIngameEvent ingameEvent3 = new MMIngameEvent(MMEvent.EventType.TAKEN_DAMAGE);
        MMLoginEvent loginEvent = new MMLoginEvent(MMEvent.EventType.ERROR);
        MMLoginEvent loginEvent2 = new MMLoginEvent(MMEvent.EventType.ERROR);
        MMLoginEvent loginEvent3 = new MMLoginEvent(MMEvent.EventType.HELLO_CLIENT);

        Assertions.assertEquals("{\"eventType\":\"TurnEvent\"}", ingameEvent.toJsonEvent());
        Assertions.assertEquals("MMIngameEvent", ingameEvent.toString());
        Assertions.assertEquals(MMEvent.EventType.TURN, ingameEvent.getEventType());

        Assertions.assertEquals(ingameEvent, ingameEvent);
        Assertions.assertNotEquals(null, ingameEvent);
        Assertions.assertNotEquals(ingameEvent, loginEvent);
        Assertions.assertEquals(ingameEvent, ingameEvent2);
        Assertions.assertNotEquals(ingameEvent, ingameEvent3);

        Assertions.assertNotEquals(ingameEvent.hashCode(), ingameEvent3.hashCode());

        Assertions.assertEquals("{\"messageType\":\"ERROR\"}", loginEvent.toJsonEvent());
        Assertions.assertEquals("MMLoginEvent", loginEvent.toString());
        Assertions.assertEquals(MMEvent.EventType.ERROR, loginEvent.getEventType());

        Assertions.assertEquals(loginEvent, loginEvent);
        Assertions.assertNotEquals(null, loginEvent);
        Assertions.assertNotEquals(ingameEvent, loginEvent);
        Assertions.assertEquals(loginEvent, loginEvent2);
        Assertions.assertNotEquals(loginEvent, loginEvent3);

        Assertions.assertNotEquals(loginEvent.hashCode(), loginEvent3.hashCode());
    }

    @Test
    void gameStructure() throws FileNotFoundException, ConfigValidationException {
        final Config config = new Config(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieexample.game.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterConfig.character.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioexample.scenario.json"));
        Character[] array = {new Character(), new Character(), new Character(), new Character(), new Character(), new Character()};
        final GameStructure g1 = new GameStructure("PlayerOne", "Gandalf", "Bilbo", array, array, config.getPartieConfig(), config.getScenarioConfig());
        final GameStructure g2 = new GameStructure("PlayerOne", "Gandalf", "Bilbo", array, array, config.getPartieConfig(), config.getScenarioConfig());
        final GameStructure g3 = new GameStructure("Spectator", "Gandalf", "Bilbo", array, array, config.getPartieConfig(), config.getScenarioConfig());
        Assertions.assertEquals(g1, g2);
        Assertions.assertEquals(g1, g1);
        Assertions.assertNotEquals(g1, g3);
        // test some more branches
        Assertions.assertNotEquals(g1, new GameStructure("PlayerOne", "test", "Bilbo", array, array, config.getPartieConfig(), config.getScenarioConfig()));
        Assertions.assertNotEquals(g1, new GameStructure("PlayerOne", "Gandalf", "test", array, array, config.getPartieConfig(), config.getScenarioConfig()));
        Assertions.assertNotEquals(g1, "");
        Assertions.assertNotEquals(g1, null);
        Assertions.assertEquals(g1.hashCode(), g2.hashCode());

    }

    @Test
    void useInifinityStoneEvent() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 7);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 6);
        final UseInfinityStoneEvent e1 = new UseInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType);
        final UseInfinityStoneEvent e2 = new UseInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType);
        final UseInfinityStoneEvent e3 = new UseInfinityStoneEvent(new Tuple<>(EntityID.P1, 1), targetEntity, originField, targetField, stoneType);
        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void exchangeInfinityStoneEvent() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 7);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 6);
        final ExchangeInfinityStoneEvent e1 = new ExchangeInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType);
        final ExchangeInfinityStoneEvent e2 = new ExchangeInfinityStoneEvent(originEntity, targetEntity, originField, targetField, stoneType);
        final ExchangeInfinityStoneEvent e3 = new ExchangeInfinityStoneEvent(new Tuple<>(EntityID.P1, 1), targetEntity, originField, targetField, stoneType);
        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void spawnEntityEvent() {
        final SpawnEntityEvent e1 = new SpawnEntityEvent(new Rock(1));
        final SpawnEntityEvent e2 = new SpawnEntityEvent(new Rock(1));
        final SpawnEntityEvent e3 = new SpawnEntityEvent(new Rock(2));

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void meleeAttackEvent() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final var e1 = new MeleeAttackEvent(originEntity, targetEntity, originField, targetField);
        final var e2 = new MeleeAttackEvent(originEntity, targetEntity, originField, targetField);
        final var e3 = new MeleeAttackEvent(new Tuple<>(EntityID.P1, 5), targetEntity, originField, targetField);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void rangedAttackEvent() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final var e1 = new RangedAttackEvent(originEntity, targetEntity, originField, targetField);
        final var e2 = new RangedAttackEvent(originEntity, targetEntity, originField, targetField);
        final var e3 = new RangedAttackEvent(new Tuple<>(EntityID.P1, 5), targetEntity, originField, targetField);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void moveEvent() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final var e1 = new MoveEvent(originEntity, originField, targetField);
        final var e2 = new MoveEvent(originEntity, originField, targetField);
        final var e3 = new MoveEvent(originEntity, new Tuple<>(2, 7), targetField);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void roundSetupEvent() {
        Tuple<EntityID, Integer> char1 = new Tuple<>(EntityID.P1, 2);
        Tuple<EntityID, Integer> char2 = new Tuple<>(EntityID.P2, 4);
        Tuple<EntityID, Integer> char3 = new Tuple<>(EntityID.P2, 2);
        Tuple<EntityID, Integer> char4 = new Tuple<>(EntityID.P1, 4);
        List<Tuple<EntityID, Integer>> characterOrder = new ArrayList<>(Arrays.asList(char1, char2, char3, char4));
        final RoundSetupEvent e1 = new RoundSetupEvent(4, characterOrder);
        final RoundSetupEvent e2 = new RoundSetupEvent(4, characterOrder);
        final RoundSetupEvent e3 = new RoundSetupEvent(5, characterOrder);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void gameAssignment() {
        Character[] array = {new Character(), new Character(), new Character(), new Character(), new Character(), new Character(), new Character(), new Character(), new Character(), new Character(), new Character(), new Character()};
        final GameAssignment e1 = new GameAssignment("g1", array);
        final GameAssignment e2 = new GameAssignment("g1", array);
        final GameAssignment e3 = new GameAssignment("g2", array);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void consumedMPevent() {
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final ConsumedMPEvent e1 = new ConsumedMPEvent(targetEntity, targetField, 1);
        final ConsumedMPEvent e2 = new ConsumedMPEvent(targetEntity, targetField, 1);
        final ConsumedMPEvent e3 = new ConsumedMPEvent(targetEntity, targetField, 2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void consumedAPevent() {
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final ConsumedAPEvent e1 = new ConsumedAPEvent(targetEntity, targetField, 1);
        final ConsumedAPEvent e2 = new ConsumedAPEvent(targetEntity, targetField, 1);
        final ConsumedAPEvent e3 = new ConsumedAPEvent(targetEntity, targetField, 2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void takenDamageEvent() {
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final TakenDamageEvent e1 = new TakenDamageEvent(targetEntity, targetField, 1);
        final TakenDamageEvent e2 = new TakenDamageEvent(targetEntity, targetField, 1);
        final TakenDamageEvent e3 = new TakenDamageEvent(targetEntity, targetField, 2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void healedEvent() {
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final HealedEvent e1 = new HealedEvent(targetEntity, targetField, 1);
        final HealedEvent e2 = new HealedEvent(targetEntity, targetField, 1);
        final HealedEvent e3 = new HealedEvent(targetEntity, targetField, 2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void destroyEntityEvent() {
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final DestroyedEntityEvent e1 = new DestroyedEntityEvent(targetField, targetEntity);
        final DestroyedEntityEvent e2 = new DestroyedEntityEvent(targetField, targetEntity);
        final DestroyedEntityEvent e3 = new DestroyedEntityEvent(new Tuple<>(2, 6), targetEntity);


        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void TurnEvent() {
        final TurnEvent e1 = new TurnEvent(1, new Tuple<>(EntityID.P1, 1));
        final TurnEvent e2 = new TurnEvent(1, new Tuple<>(EntityID.P1, 1));
        final TurnEvent e3 = new TurnEvent(2, new Tuple<>(EntityID.P1, 1));

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void error() {
        final ErrorEvent e1 = new ErrorEvent("e", 1);
        final ErrorEvent e2 = new ErrorEvent("e", 1);
        final ErrorEvent e3 = new ErrorEvent("e", 2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void TimeoutWarningEvent() {
        final TimeoutWarningEvent e1 = new TimeoutWarningEvent("e1", 1);
        final TimeoutWarningEvent e2 = new TimeoutWarningEvent("e1", 1);
        final TimeoutWarningEvent e3 = new TimeoutWarningEvent("e3", 1);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void confirmSelection() {
        final ConfirmSelection e1 = new ConfirmSelection(true);
        final ConfirmSelection e2 = new ConfirmSelection(true);
        final ConfirmSelection e3 = new ConfirmSelection(false);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void helloClient() {
        final HelloClient e1 = new HelloClient(true);
        final HelloClient e2 = new HelloClient(true);
        final HelloClient e3 = new HelloClient(false);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void winEvent() {
        final WinEvent e1 = new WinEvent(1);
        final WinEvent e2 = new WinEvent(1);
        final WinEvent e3 = new WinEvent(2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void timeoutEvent() {
        final TimeoutEvent e1 = new TimeoutEvent("e1");
        final TimeoutEvent e2 = new TimeoutEvent("e1");
        final TimeoutEvent e3 = new TimeoutEvent("e3");

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void generalAssignment() {
        final GeneralAssignment e1 = new GeneralAssignment("e1");
        final GeneralAssignment e2 = new GeneralAssignment("e1");
        final GeneralAssignment e3 = new GeneralAssignment("e3");

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void goodbyeClient() {
        final GoodbyeClient e1 = new GoodbyeClient("e1");
        final GoodbyeClient e2 = new GoodbyeClient("e1");
        final GoodbyeClient e3 = new GoodbyeClient("e3");

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

}
