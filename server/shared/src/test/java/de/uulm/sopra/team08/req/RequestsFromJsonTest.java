package de.uulm.sopra.team08.req;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for testing the parsing of JSON to Requests
 */
public class RequestsFromJsonTest {

    final Gson gson = new Gson();

    @Test
    void testExchangeInfinityStoneRequestFromJson() {
        JsonObject exchangeInfinityStoneRequest = gson.fromJson("{\"requestType\":\"ExchangeInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[2,5],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":5}}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 5);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 5);

        assertEquals(ExchangeInfinityStoneRequest.fromJson(exchangeInfinityStoneRequest), new ExchangeInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType));

        JsonObject exchangeInfinityStoneRequestError = gson.fromJson("{\"requestType\":\"ExchangeInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[2,5],\"stoneType\":{\"entityID\":\"NPC\",\"ID\":5}}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> ExchangeInfinityStoneRequest.fromJson(exchangeInfinityStoneRequestError));
    }

    @Test
    void testMeleeAttackRequestFromJson() {
        JsonObject meleeAttackRequest = gson.fromJson("{\"requestType\":\"MeleeAttackRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[2,5],\"value\":14}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 5);

        assertEquals(MeleeAttackRequest.fromJson(meleeAttackRequest), new MeleeAttackRequest(originEntity, targetEntity, originField, targetField, 14));

        JsonObject meleeAttackRequestError = gson.fromJson("{\"requestType\":\"MeleeAttackRequest\",\"originEntity\":{\"entityID\":\"P3\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[2,5],\"value\":14}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> RangedAttackRequest.fromJson(meleeAttackRequestError));
    }

    @Test
    void testMoveRequestFromJson() {
        JsonObject moveRequest = gson.fromJson("{\"requestType\":\"MoveRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"originField\":[1,4],\"targetField\":[2,4]}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(1, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(2, 4);

        assertEquals(MoveRequest.fromJson(moveRequest), new MoveRequest(originEntity, originField, targetField));

        JsonObject moveRequestError = gson.fromJson("{\"requestType\":\"MoveRequest\",\"originEntity\":{\"entityID\":\"P3\",\"ID\":3},\"originField\":[1,4],\"targetField\":[2,4]}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> RangedAttackRequest.fromJson(moveRequestError));
    }

    @Test
    void testRangedAttackRequestFromJson() {
        JsonObject rangedAttackRequest = gson.fromJson("{\"requestType\":\"RangedAttackRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[4,7],\"value\":14}", JsonObject.class);
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 1);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(4, 7);

        assertEquals(RangedAttackRequest.fromJson(rangedAttackRequest), new RangedAttackRequest(originEntity, targetEntity, originField, targetField, 14));

        JsonObject rangedAttackRequestError = gson.fromJson("{\"requestType\":\"RangedAttackRequest\",\"originEntity\":{\"entityID\":\"P3\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P2\",\"ID\":1},\"originField\":[2,4],\"targetField\":[4,7],\"value\":14}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> RangedAttackRequest.fromJson(rangedAttackRequestError));
    }

    @Test
    void testUseInfinityStoneRequestFromJson() {
        JsonObject useInfinityStoneRequest = gson.fromJson("{\"requestType\":\"UseInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":4},\"originField\":[2,4],\"targetField\":[14,17],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":5}}", JsonObject.class);

        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(2, 4);
        Tuple<Integer, Integer> targetField = new Tuple<>(14, 17);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 5);

        assertEquals(UseInfinityStoneRequest.fromJson(useInfinityStoneRequest), new UseInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType));

        JsonObject useInfinityStoneRequestError = gson.fromJson("{\"requestType\":\"UseInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"P1\",\"ID\":3},\"targetEntity\":{\"entityID\":\"P1\",\"ID\":4},\"originField\":[2,4],\"targetField\":[14,17],\"stoneType\":{\"entityID\":\"NPC\",\"ID\":5}}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> UseInfinityStoneRequest.fromJson(useInfinityStoneRequestError));
    }

    /**
     * The following Requests are LOGIN Requests and were parsed with Gson, causing a different order of JSON Properties
     */

    @Test
    void testCharacterSelectionFromJson() {
        JsonObject characterSelection = gson.fromJson("{\"characters\":[true,true,true,true,true,true,true,true,true,true,true,true],\"messageType\":\"CHARACTER_SELECTION\"}", JsonObject.class);
        assertEquals(CharacterSelection.fromJson(characterSelection), new CharacterSelection(new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true}));

        characterSelection = gson.fromJson("{\"characters\":[false,false,false,false,false,false,false,false,false,false,false,false],\"messageType\":\"CHARACTER_SELECTION\"}", JsonObject.class);
        assertEquals(CharacterSelection.fromJson(characterSelection), new CharacterSelection(new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false}));

        assertThrows(IllegalArgumentException.class, () -> new CharacterSelection(new boolean[]{true}));

        JsonObject characterSelectionError = gson.fromJson("{\"characters\":[],\"messageType\":\"CHARACTER_SELECTION\"}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> CharacterSelection.fromJson(characterSelectionError));
    }

    @Test
    void testErrorFromJson() {
        JsonObject error = gson.fromJson("{\"message\":\"\",\"type\":1,\"messageType\":\"ERROR\"}", JsonObject.class);
        assertEquals(ErrorRequest.fromJson(error), new ErrorRequest("", 1));

        error = gson.fromJson("{\"message\":\"This is a test!\",\"type\":2,\"messageType\":\"ERROR\"}", JsonObject.class);
        assertEquals(ErrorRequest.fromJson(error), new ErrorRequest("This is a test!", 2));
    }

    @Test
    void testHelloServerFromJson() {
        JsonObject helloServer = gson.fromJson("{\"name\":\"TestName\",\"deviceID\":\"deviceID\",\"messageType\":\"HELLO_SERVER\"}", JsonObject.class);
        assertEquals(HelloServer.fromJson(helloServer), new HelloServer("TestName", "deviceID"));
    }

    @Test
    void testPlayerReadyFromJson() {
        JsonObject playerReady = gson.fromJson("{\"startGame\":true,\"role\":\"PLAYER\",\"messageType\":\"PLAYER_READY\"}", JsonObject.class);
        assertEquals(PlayerReady.fromJson(playerReady), new PlayerReady(true, Role.PLAYER));

        playerReady = gson.fromJson("{\"startGame\":false,\"role\":\"PLAYER\",\"messageType\":\"PLAYER_READY\"}", JsonObject.class);
        assertEquals(PlayerReady.fromJson(playerReady), new PlayerReady(false, Role.PLAYER));

        playerReady = gson.fromJson("{\"startGame\":true,\"role\":\"KI\",\"messageType\":\"PLAYER_READY\"}", JsonObject.class);
        assertEquals(PlayerReady.fromJson(playerReady), new PlayerReady(true, Role.KI));

        playerReady = gson.fromJson("{\"startGame\":true,\"role\":\"SPECTATOR\",\"messageType\":\"PLAYER_READY\"}", JsonObject.class);
        assertEquals(PlayerReady.fromJson(playerReady), new PlayerReady(true, Role.SPECTATOR));

        JsonObject finalPlayerReady = gson.fromJson("{\"startGame\":true,\"role\":\"TEST_ROLE\",\"messageType\":\"PLAYER_READY\"}", JsonObject.class);
        assertThrows(IllegalArgumentException.class, () -> PlayerReady.fromJson(finalPlayerReady));
    }

    @Test
    void testReconnectFromJson() {
        JsonObject reconnect = gson.fromJson("{\"reconnect\":true,\"messageType\":\"RECONNECT\"}", JsonObject.class);
        assertEquals(Reconnect.fromJson(reconnect), new Reconnect(true));

        reconnect = gson.fromJson("{\"reconnect\":false,\"messageType\":\"RECONNECT\"}", JsonObject.class);
        assertEquals(Reconnect.fromJson(reconnect), new Reconnect(false));
    }

}
