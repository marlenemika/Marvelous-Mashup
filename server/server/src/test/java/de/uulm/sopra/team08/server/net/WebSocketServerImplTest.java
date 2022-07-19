package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.req.*;
import de.uulm.sopra.team08.server.util.TestGameLogic;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static de.uulm.sopra.team08.server.net.NetTestSettings.TEST_PORT;
import static de.uulm.sopra.team08.server.net.NetTestSettings.TEST_TIMEOUT_MILLIS;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
class WebSocketServerImplTest {

    private static final Logger LOGGER = LogManager.getLogger(WebSocketServerImplTest.class);
    private static WebSocketServerImpl server;

    @BeforeAll
    static void beforeAll() {
        // only warn
        Configurator.setLevel("org.java_websocket", Level.DEBUG);

        if (!NetworkManager.isInitialized())
            NetworkManager.init(TEST_PORT, new TestGameLogic(), TEST_TIMEOUT_MILLIS);
        server = NetworkManager.getInstance().getServer();
    }

    /**
     * Helper method to access private method.
     *
     * @param s The incoming message.
     * @return A list of requests.
     */
    private List<MMRequest> parseRequest(String s) {
        try {
            final Method parseRequest = WebSocketServerImpl.class.getDeclaredMethod("parseRequest", String.class);
            parseRequest.setAccessible(true);
            //noinspection unchecked
            return (List<MMRequest>) parseRequest.invoke(server, s);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parseRequestTest() {
        List<MMRequest> expected;

        // EMPTY
        expected = new ArrayList<>();
        assertEquals(expected, parseRequest(null));
        assertEquals(expected, parseRequest(""));
        assertEquals(expected, parseRequest("-"));
        assertEquals(expected, parseRequest("some Nonsense"));
        assertEquals(expected, parseRequest("{}"));

        // LOGIN
        // invalid
        assertEquals(expected, parseRequest("{messageType: 1}"));
        assertEquals(expected, parseRequest("{messageType:\"CoolEvent\"}"));

        // valid
        expected = asList(new HelloServer("Name", "DeviceID"));
        assertEquals(expected, parseRequest("{\"name\":\"Name\",\"deviceID\":\"DeviceID\",\"messageType\":\"HELLO_SERVER\"}"));
        expected = asList(new Reconnect(true));
        assertEquals(expected, parseRequest("{\"reconnect\":true,\"messageType\":\"RECONNECT\"}"));
        expected = asList(new Reconnect(false));
        assertEquals(expected, parseRequest("{\"reconnect\":false,\"messageType\":\"RECONNECT\"}"));
        expected = asList(new PlayerReady(true, Role.PLAYER));
        assertEquals(expected, parseRequest("{\"startGame\":true,\"role\":\"PLAYER\",\"messageType\":\"PLAYER_READY\"}"));
        expected = asList(new PlayerReady(false, Role.PLAYER));
        assertEquals(expected, parseRequest("{\"startGame\":false,\"role\":\"PLAYER\",\"messageType\":\"PLAYER_READY\"}"));
        expected = asList(new PlayerReady(true, Role.KI));
        assertEquals(expected, parseRequest("{\"startGame\":true,\"role\":\"KI\",\"messageType\":\"PLAYER_READY\"}"));
        expected = asList(new PlayerReady(true, Role.SPECTATOR));
        assertEquals(expected, parseRequest("{\"startGame\":true,\"role\":\"SPECTATOR\",\"messageType\":\"PLAYER_READY\"}"));
        expected = asList(new CharacterSelection(new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true}));
        assertEquals(expected, parseRequest("{\"characters\":[true, true, true, true, true, true, true, true, true, true, true, true],\"messageType\":\"CHARACTER_SELECTION\"}"));
        expected = asList(new CharacterSelection(new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false}));
        assertEquals(expected, parseRequest("{\"characters\":[false, false, false, false, false, false, false, false, false, false, false, false],\"messageType\":\"CHARACTER_SELECTION\"}"));
        expected = asList(new ErrorRequest("", 1));
        assertEquals(expected, parseRequest("{\"message\":\"\",\"type\":1,\"messageType\":\"ERROR\"}"));
        expected = asList(new ErrorRequest("test message", 2));
        assertEquals(expected, parseRequest("{\"message\":\"test message\",\"type\":2,\"messageType\":\"ERROR\"}"));

        // INGAME
        // invalid
        expected = asList();
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\"}"));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[]}"));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{requestType:1}]}"));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{requestType:\"nice\"}]}"));

        // valid
        expected = asList(new MeleeAttackRequest(new Tuple<>(EntityID.NPC, 1), new Tuple<>(EntityID.NPC, 2), new Tuple<>(1, 1), new Tuple<>(1, 1), 100));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"MeleeAttackRequest\",\"originEntity\":{\"entityID\":\"NPC\",\"ID\":1},\"targetEntity\":{\"entityID\":\"NPC\",\"ID\":2},\"originField\":[1,1],\"targetField\":[1,1],\"value\":100}]}"));
        expected = asList(new RangedAttackRequest(new Tuple<>(EntityID.NPC, 1), new Tuple<>(EntityID.NPC, 2), new Tuple<>(1, 1), new Tuple<>(1, 1), 100));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"RangedAttackRequest\",\"originEntity\":{\"entityID\":\"NPC\",\"ID\":1},\"targetEntity\":{\"entityID\":\"NPC\",\"ID\":2},\"originField\":[1,1],\"targetField\":[1,1],\"value\":100}]}"));
        expected = asList(new MoveRequest(new Tuple<>(EntityID.NPC, 1), new Tuple<>(1, 2), new Tuple<>(2, 2)));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"MoveRequest\",\"originEntity\":{\"entityID\":\"NPC\",\"ID\":1},\"originField\":[1,2],\"targetField\":[2,2]}]}"));
        expected = asList(new ExchangeInfinityStoneRequest(new Tuple<>(EntityID.NPC, 1), new Tuple<>(EntityID.NPC, 2), new Tuple<>(1, 1), new Tuple<>(1, 2), new Tuple<>(EntityID.INFINITYSTONES, 1)));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"ExchangeInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"NPC\",\"ID\":1},\"targetEntity\":{\"entityID\":\"NPC\",\"ID\":2},\"originField\":[1,1],\"targetField\":[1,2],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":1}}]}"));
        expected = asList(new UseInfinityStoneRequest(new Tuple<>(EntityID.NPC, 1), new Tuple<>(EntityID.NPC, 2), new Tuple<>(1, 1), new Tuple<>(2, 1), new Tuple<>(EntityID.INFINITYSTONES, 1)));
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"UseInfinityStoneRequest\",\"originEntity\":{\"entityID\":\"NPC\",\"ID\":1},\"targetEntity\":{\"entityID\":\"NPC\",\"ID\":2},\"originField\":[1,1],\"targetField\":[2,1],\"stoneType\":{\"entityID\":\"InfinityStones\",\"ID\":1}}]}"));
        expected = asList(new EndRoundRequest());
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"EndRoundRequest\"}]}"));
        expected = asList(new PauseStartRequest());
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"PauseStartRequest\"}]}"));
        expected = asList(new PauseStopRequest());
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"PauseStopRequest\"}]}"));
        expected = asList(new DisconnectRequest());
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"DisconnectRequest\"}]}"));
        expected = asList(new Req());
        assertEquals(expected, parseRequest("{messageType:\"REQUESTS\",messages:[{\"requestType\":\"Req\"}]}"));
    }

}