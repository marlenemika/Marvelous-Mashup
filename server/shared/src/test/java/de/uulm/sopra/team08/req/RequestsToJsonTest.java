package de.uulm.sopra.team08.req;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for testing the parsing Requests to correct JSON
 */
public class RequestsToJsonTest {

    @Test
    void testRequestsWithoutParameters() {
        String endRoundRequest = "{\"requestType\":\"EndRoundRequest\"}";
        assertEquals(endRoundRequest, (new EndRoundRequest()).toJsonRequest());

        String pauseStartRequest = "{\"requestType\":\"PauseStartRequest\"}";
        assertEquals(pauseStartRequest, (new PauseStartRequest()).toJsonRequest());

        String pauseStopRequest = "{\"requestType\":\"PauseStopRequest\"}";
        assertEquals(pauseStopRequest, (new PauseStopRequest()).toJsonRequest());

        String disconnectRequest = "{\"requestType\":\"DisconnectRequest\"}";
        assertEquals(disconnectRequest, (new DisconnectRequest()).toJsonRequest());

        String req = "{\"requestType\":\"Req\"}";
        assertEquals(req, (new Req()).toJsonRequest());
    }

    @Test
    void testExchangeInfinityStoneRequestToJson() {
        String exchangeInfinityStoneRequest = "{\"requestType\":\"ExchangeInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[2,5],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":5}}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 5);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 5);

        assertEquals(exchangeInfinityStoneRequest, (new ExchangeInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType)).toJsonRequest());
    }

    @Test
    void testMeleeAttackRequestToJson() {
        String meleeAttackRequest = "{\"requestType\":\"MeleeAttackRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[2,5],\"value\":14}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 5);

        assertEquals(meleeAttackRequest, (new MeleeAttackRequest(originEntity, targetEntity, originField, targetField, 14)).toJsonRequest());
    }

    @Test
    void testMoveRequestToJson() {
        String moveRequest = "{\"requestType\":\"MoveRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"originField\":[1,4],\"targetField\":[2,4]}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(1, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 4);

        assertEquals(moveRequest, (new MoveRequest(originEntity, originField, targetField)).toJsonRequest());
    }

    @Test
    void testRangedAttackRequestToJson() {
        String rangedAttackRequest = "{\"requestType\":\"RangedAttackRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[4,7],\"value\":14}";
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(4, 7);

        assertEquals(rangedAttackRequest, (new RangedAttackRequest(originEntity, targetEntity, originField, targetField, 14)).toJsonRequest());
    }

    @Test
    void testUseInfinityStoneRequestToJson() {
        String useInfinityStoneRequest = "{\"requestType\":\"UseInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":4},\"originField\":[2,4],\"targetField\":[14,17],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":5}}";

        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(14, 17);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 5);

        assertEquals(useInfinityStoneRequest, (new UseInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType)).toJsonRequest());
    }

    /**
     * The following Requests are LOGIN Requests and were parsed with Gson, causing a different order of JSON Properties
     */

    @Test
    void testCharacterSelectionToJson() {
        String characterSelection = "{\"characters\":[true,true,true,true,true,true,true,true,true,true,true,true],\"messageType\":\"CHARACTER_SELECTION\"}";
        assertEquals(characterSelection, (new CharacterSelection(new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true})).toJsonRequest());

        characterSelection = "{\"characters\":[false,false,false,false,false,false,false,false,false,false,false,false],\"messageType\":\"CHARACTER_SELECTION\"}";
        assertEquals(characterSelection, (new CharacterSelection(new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false})).toJsonRequest());

        assertThrows(IllegalArgumentException.class, () -> new CharacterSelection(new boolean[]{true}));
    }

    @Test
    void testErrorToJson() {
        String error = "{\"message\":\"\",\"type\":1,\"messageType\":\"ERROR\"}";
        assertEquals(error, (new ErrorRequest("", 1)).toJsonRequest());

        error = "{\"message\":\"This is a test!\",\"type\":2,\"messageType\":\"ERROR\"}";
        assertEquals(error, (new ErrorRequest("This is a test!", 2)).toJsonRequest());
    }

    @Test
    void testHelloServerToJson() {
        String helloServer = "{\"name\":\"TestName\",\"deviceID\":\"deviceID\",\"messageType\":\"HELLO_SERVER\"}";
        assertEquals(helloServer, (new HelloServer("TestName", "deviceID")).toJsonRequest());
    }

    @Test
    void testPlayerReadyToJson() {
        String playerReady = "{\"startGame\":true,\"role\":\"PLAYER\",\"messageType\":\"PLAYER_READY\"}";
        assertEquals(playerReady, (new PlayerReady(true, Role.PLAYER)).toJsonRequest());

        playerReady = "{\"startGame\":false,\"role\":\"PLAYER\",\"messageType\":\"PLAYER_READY\"}";
        assertEquals(playerReady, (new PlayerReady(false, Role.PLAYER)).toJsonRequest());

        playerReady = "{\"startGame\":true,\"role\":\"KI\",\"messageType\":\"PLAYER_READY\"}";
        assertEquals(playerReady, (new PlayerReady(true, Role.KI)).toJsonRequest());

        playerReady = "{\"startGame\":true,\"role\":\"SPECTATOR\",\"messageType\":\"PLAYER_READY\"}";
        assertEquals(playerReady, (new PlayerReady(true, Role.SPECTATOR)).toJsonRequest());
    }

    @Test
    void testReconnectToJson() {
        String reconnect = "{\"reconnect\":true,\"messageType\":\"RECONNECT\"}";
        assertEquals(reconnect, (new Reconnect(true)).toJsonRequest());

        reconnect = "{\"reconnect\":false,\"messageType\":\"RECONNECT\"}";
        assertEquals(reconnect, (new Reconnect(false)).toJsonRequest());
    }

}
