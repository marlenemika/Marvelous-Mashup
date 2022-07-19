package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.config.character.Character;
import de.uulm.sopra.team08.event.*;
import de.uulm.sopra.team08.req.*;
import de.uulm.sopra.team08.server.data.Player;
import de.uulm.sopra.team08.server.util.TestGameLogic;
import de.uulm.sopra.team08.util.Role;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.uulm.sopra.team08.server.net.NetTestSettings.TEST_PORT;
import static de.uulm.sopra.team08.server.net.NetTestSettings.TEST_TIMEOUT_MILLIS;
import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

    private static final Logger LOGGER = LogManager.getLogger(NetworkTest.class);
    private static TestGameLogic logic;
    private static List<Player> logicPlayers;
    private static NetworkManager net;
    private TestWebSocketClient client;
    private List<String> clientExpected;
    private List<String> clientReceived;


    @BeforeAll
    static void beforeAll() {
        // Set level of WebSocket
        Configurator.setLevel("org.java_websocket", Level.WARN);

        // init
        logic = new TestGameLogic();
        logicPlayers = new ArrayList<>();
        logic.setRegisterPlayer(logicPlayers::add);
        logic.setUnregisterPlayer(logicPlayers::remove);

        if (!NetworkManager.isInitialized())
            NetworkManager.init(TEST_PORT, logic, TEST_TIMEOUT_MILLIS);
        net = NetworkManager.getInstance();
        net.setLogic(logic);
    }

    private void waitForReceivedToMatchExpectedAndClear() {
        final Instant start = Instant.now();
        while (clientReceived.size() < clientExpected.size()) {
            // fail on timeout
            if (ChronoUnit.SECONDS.between(start, Instant.now()) > 2)
                Assertions.fail("Timed out on waiting for size of Collection!");
            // no Thread.onSpinWait(); cause we have a timeout
        }

        // check
        assertEquals(clientExpected, clientReceived);

        clientExpected.clear();
        clientReceived.clear();
    }

    @BeforeEach
    void beforeEach() throws InterruptedException {
        // player-logic
        logicPlayers.clear();

        clientExpected = new ArrayList<>();
        clientReceived = new ArrayList<>();

        client = new TestWebSocketClient("localhost", TEST_PORT);
        client.setOnError(Assertions::fail);
        client.setOnOpen(sh -> LOGGER.info("Client Connected!"));
        client.setOnClose((c, m, r) -> LOGGER.info("Client Disconnected!"));
        client.setOnMessage(clientReceived::add);

        client.connectBlocking();
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        client.closeBlocking();
    }

    @Test
    void invalidByteRequestTest() {
        // set expected
        clientExpected.add(ErrorEvent.REQUEST_UNSUPPORTED.toJsonEvent());

        // send
        client.send(ByteBuffer.allocate(10));
        waitForReceivedToMatchExpectedAndClear();
    }

    @Test
    void helloServerTest() {
        logic.setIsGameRunning(() -> true);

        // set expected
        clientExpected.add(new HelloClient(true).toJsonEvent());

        // send
        client.sendRequest(new HelloServer("Test Client", "helloServerTest"));
        waitForReceivedToMatchExpectedAndClear();

        assertEquals(new ArrayList<>(), logicPlayers);
    }

    @Test
    void newPlayerUnexpectedRequestTest() {
        // set expected
        clientExpected.add(ErrorEvent.REQUEST_UNEXPECTED.toJsonEvent());
        clientExpected.add(new GoodbyeClient("Disconnected due to an error!").toJsonEvent());

        // send
        client.sendRequest(new Reconnect(true));
        waitForReceivedToMatchExpectedAndClear();

        assertEquals(new ArrayList<>(), logicPlayers);
    }

    @Test
    void connectingPlayerDisconnectRequestTest() {
        logic.setIsGameRunning(() -> false);

        // register with HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "connectingPlayerDisconnectRequestTest"));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        clientExpected.add(new GoodbyeClient("Goodbye Client!").toJsonEvent());

        // send
        client.sendRequest(new DisconnectRequest());
        waitForReceivedToMatchExpectedAndClear();

        assertEquals(new ArrayList<>(), logicPlayers);
    }

    @Test
    void connectingPlayerUnexpectedRequestTest() {
        logic.setIsGameRunning(() -> false);

        // register with HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "connectingPlayerUnexpectedRequestTest"));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        clientExpected.add(ErrorEvent.REQUEST_UNEXPECTED.toJsonEvent());
        clientExpected.add(new GoodbyeClient("Disconnected due to an error!").toJsonEvent());

        // send
        client.sendRequest(new HelloServer("", ""));
        waitForReceivedToMatchExpectedAndClear();

        assertEquals(new ArrayList<>(), logicPlayers);
    }

    @Test
    void playerReadyTrueTest() {
        AtomicBoolean loggedIn = new AtomicBoolean(false);
        logic.setIsGameRunning(() -> false);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            loggedIn.set(true);
            // send event to notify test
            net.send(p, new HelloClient(true));
            return true;
        });

        // register with HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "playerReadyTrueTest"));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        // use HelloClient for notify
        clientExpected.add(new HelloClient(true).toJsonEvent());

        // send
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();

        assertTrue(loggedIn.get());
    }

    @Test
    void playerReadyTrueRegisterFailedTest() {
        logic.setIsGameRunning(() -> false);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            return false; // fail to register
        });

        // register with HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "readyPlayerTrueRegisterFailedTest"));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        // use HelloClient for notify
        clientExpected.add(ErrorEvent.REQUEST_REGISTER_FAILED.toJsonEvent());
        clientExpected.add(new GoodbyeClient("Disconnected due to an error!").toJsonEvent());

        // send
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();
    }

    @Test
    void playerReadyFalseTest() {
        logic.setIsGameRunning(() -> false);

        // register with HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "playerReadyFalseTest"));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        clientExpected.add(new GoodbyeClient("Goodbye Client!").toJsonEvent());

        // send
        client.sendRequest(new PlayerReady(false, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();

        assertEquals(new ArrayList<>(), logicPlayers);
    }

    @Test
    void reconnectTrueTest() throws InterruptedException {
        AtomicBoolean loggedIn = new AtomicBoolean(false);
        logic.setIsGameRunning(() -> true);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            loggedIn.set(true);
            // send event to notify test
            net.send(p, new HelloClient(true));
            return true;
        });

        // HelloServer
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "reconnectTrueTest"));
        waitForReceivedToMatchExpectedAndClear();

        // PlayerReady
        // use HelloClient to notify test
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();

        // DisconnectRequest
        clientExpected.add(new GoodbyeClient("Goodbye Client!").toJsonEvent());
        client.sendRequest(new DisconnectRequest());
        waitForReceivedToMatchExpectedAndClear();

        // reconnect
        client.reconnectBlocking();

        // HelloServer
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "reconnectTrueTest"));
        waitForReceivedToMatchExpectedAndClear();

        // set expected
        // use HelloClient for notify
        clientExpected.add(new HelloClient(true).toJsonEvent());

        // send
        client.sendRequest(new Reconnect(true));
        waitForReceivedToMatchExpectedAndClear();

        assertTrue(loggedIn.get());
    }

    @Test
    void reconnectTrueRegisterFailedTest() throws InterruptedException {
        AtomicBoolean loggedIn = new AtomicBoolean(false);
        logic.setIsGameRunning(() -> true);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            loggedIn.set(true);
            // send event to notify test
            net.send(p, new HelloClient(true));
            return true;
        });

        // HelloServer
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "reconnectTrueRegisterFailedTest"));
        waitForReceivedToMatchExpectedAndClear();

        // PlayerReady
        // use HelloClient to notify test
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();

        // DisconnectRequest
        clientExpected.add(new GoodbyeClient("Goodbye Client!").toJsonEvent());
        client.sendRequest(new DisconnectRequest());
        waitForReceivedToMatchExpectedAndClear();

        // prepare logic
        logic.setRegisterPlayer(p -> {
            System.out.println("Test!");
            logicPlayers.add(p);
            return false;
        });

        // reconnect
        client.reconnectBlocking();

        // HelloServer
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "reconnectTrueRegisterFailedTest"));
        waitForReceivedToMatchExpectedAndClear();

        // set expected
        clientExpected.add(ErrorEvent.REQUEST_REGISTER_FAILED.toJsonEvent());
        clientExpected.add(new GoodbyeClient("Disconnected due to an error!").toJsonEvent());

        // send
        client.sendRequest(new Reconnect(true));
        waitForReceivedToMatchExpectedAndClear();
    }

    @Test
    void reconnectTrueRecoverFailedTest() {
        logic.setIsGameRunning(() -> true);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            return true;
        });

        // HelloServer
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "reconnectTrueRecoverFailedTest"));
        waitForReceivedToMatchExpectedAndClear();

        // set expected
        clientExpected.add(ErrorEvent.REQUEST_RECOVER_FAILED.toJsonEvent());
        clientExpected.add(new GoodbyeClient("Disconnected due to an error!").toJsonEvent());

        // send
        client.sendRequest(new Reconnect(true));
        waitForReceivedToMatchExpectedAndClear();
    }

    @Test
    void reconnectFalseTest() {
        logic.setIsGameRunning(() -> true);

        // register with HelloServer
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "reconnectFalseTest"));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        clientExpected.add(new GoodbyeClient("Goodbye Client!").toJsonEvent());

        // send
        client.sendRequest(new Reconnect(false));
        waitForReceivedToMatchExpectedAndClear();

        assertEquals(new ArrayList<>(), logicPlayers);
    }

    @Test
    void characterSelectionTest() {
        final Character[] chars = new Character[12];
        Arrays.fill(chars, new Character());
        final AtomicBoolean loggedIn = new AtomicBoolean(false);
        logic.setIsGameRunning(() -> false);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            loggedIn.set(true);
            net.send(p, new GameAssignment("testGame", chars));
            return true;
        });
        logic.setHandle((p, r) -> {
            // send HelloClient to notify test
            net.send(p, new HelloClient(true));
            return r instanceof CharacterSelection;
        });

        // HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "characterSelectionTest"));
        waitForReceivedToMatchExpectedAndClear();

        // PlayerReady
        clientExpected.add(new GameAssignment("testGame", chars).toJsonEvent());
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        // use HelloClient for notify
        clientExpected.add(new HelloClient(true).toJsonEvent());

        // send
        client.sendRequest(new CharacterSelection(new boolean[12]));
        waitForReceivedToMatchExpectedAndClear();

        // logged in
        assertTrue(loggedIn.get());
        // player must not await response from character selection
        net.getConnectedPlayer(logicPlayers.get(0)).ifPresentOrElse(
                p -> assertFalse(p.isAwaitingResponse()),
                Assertions::fail
        );
    }

    @Test
    void responseRequestAckTest() {
        final AtomicBoolean loggedIn = new AtomicBoolean(false);
        logic.setIsGameRunning(() -> false);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            loggedIn.set(true);
            // send HelloClient to notify test
            net.send(p, new HelloClient(true));
            return true;
        });
        logic.setHandle((p, r) -> {
            // player must await response from the used request
            net.getConnectedPlayer(p).ifPresentOrElse(
                    np -> assertTrue(np.isAwaitingResponse()),
                    Assertions::fail
            );
            return true; // for ack
        });

        // HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "responseRequestAckTest"));
        waitForReceivedToMatchExpectedAndClear();

        // PlayerReady
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        // use HelloClient for notify
        clientExpected.add(new Ack().toJsonEvent());

        // send
        client.sendRequest(new PauseStartRequest());
        waitForReceivedToMatchExpectedAndClear();

        // logged in
        assertTrue(loggedIn.get());
    }

    @Test
    void responseRequestNackTest() {
        final AtomicBoolean loggedIn = new AtomicBoolean(false);
        logic.setIsGameRunning(() -> false);
        logic.setRegisterPlayer(p -> {
            logicPlayers.add(p);
            loggedIn.set(true);
            // send HelloClient to notify test
            net.send(p, new HelloClient(true));
            return true;
        });
        logic.setHandle((p, r) -> {
            // player must await response from the used request
            net.getConnectedPlayer(p).ifPresentOrElse(
                    np -> assertTrue(np.isAwaitingResponse()),
                    Assertions::fail
            );
            return false; // for nack
        });

        // HelloServer
        clientExpected.add(new HelloClient(false).toJsonEvent());
        client.sendRequest(new HelloServer("Test Client", "responseRequestAckTest"));
        waitForReceivedToMatchExpectedAndClear();

        // PlayerReady
        clientExpected.add(new HelloClient(true).toJsonEvent());
        client.sendRequest(new PlayerReady(true, Role.PLAYER));
        waitForReceivedToMatchExpectedAndClear();


        // set expected
        // use HelloClient for notify
        clientExpected.add(new Nack().toJsonEvent());

        // send
        client.sendRequest(new PauseStartRequest());
        waitForReceivedToMatchExpectedAndClear();

        // logged in
        assertTrue(loggedIn.get());
    }

}
