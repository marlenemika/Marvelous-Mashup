package de.uulm.sopra.team08.server.net;

import com.google.gson.*;
import de.uulm.sopra.team08.event.ErrorEvent;
import de.uulm.sopra.team08.event.HelloClient;
import de.uulm.sopra.team08.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class WebSocketServerImpl extends WebSocketServer {

    private static final Logger LOGGER = LogManager.getLogger(WebSocketServerImpl.class);
    private final NetworkManager net;
    /**
     * To parse incoming requests from json.
     */
    private final Gson gson;


    WebSocketServerImpl(int port, NetworkManager net) {
        super(new InetSocketAddress(port));
        this.net = net;
        this.gson = new GsonBuilder().create();

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stop(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Network shutdown Hook"));
    }


    /**
     * Handles a received request from a {@link WebSocket}.
     *
     * @param con     The socket, the request was received from.
     * @param request the received request.
     */
    private void onRequest(WebSocket con, MMRequest request) {
        net.getConnectedPlayer(con).ifPresentOrElse(
                // connected player
                p -> onConnectedPlayerRequest(p, request),
                () -> net.getConnectingPlayer(con).ifPresentOrElse(
                        // connecting player
                        p -> onConnectingPlayerRequest(p, request),
                        // unknown player
                        () -> onNewPlayerRequest(con, request)
                )
        );
    }

    /**
     * Handles a received request from a connected player.
     *
     * @param player  The player who sent the request.
     * @param request The request.
     */
    private void onConnectedPlayerRequest(ConnectedPlayer player, MMRequest request) {
        LOGGER.trace("Request from connected player!");

        // DisconnectRequest
        if (request instanceof DisconnectRequest) {
            player.disconnect();
            return;
        }

        // Error
        else if (request instanceof ErrorRequest) {
            final ErrorRequest error = (ErrorRequest) request;
            player.disconnectError(new ErrorEvent(error.getMessage(), error.getType()));
            return;
        }

        // await response if request requires one
        if (request.getRequestType().isRequiresResponse())
            player.awaitResponse();

        // handle
        net.handle(player, request);
    }

    /**
     * Handles a received request from a connecting player.
     *
     * @param player  The player who sent the request.
     * @param request The request.
     */
    private void onConnectingPlayerRequest(ConnectingPlayer player, MMRequest request) {
        LOGGER.trace("Request from connecting player!");

        // PlayerReady
        if (request instanceof PlayerReady) {
            final PlayerReady pr = (PlayerReady) request;

            // wants to start game
            if (pr.getStartGame()) {
                final ConnectedPlayer connectedPlayer = new ConnectedPlayer(
                        player.getName(),
                        player.getDeviceId(),
                        pr.getRole(),
                        player.getCon()
                );

                try {
                    net.registerConnectedPlayer(connectedPlayer);

                } catch (IllegalArgumentException e) {
                    player.disconnectError(ErrorEvent.REQUEST_REGISTER_FAILED);
                }
            }
            // doesn't want to start game
            else player.disconnect();

        }

        // Reconnect
        else if (request instanceof Reconnect) {
            final Reconnect reconnect = (Reconnect) request;

            // wants to reconnect
            if (reconnect.getReconnect()) {
                net.recoverRole(player).ifPresentOrElse(
                        r -> {
                            final ConnectedPlayer connectedPlayer = new ConnectedPlayer(
                                    player.getName(),
                                    player.getDeviceId(),
                                    r,
                                    player.getCon()
                            );

                            try {
                                net.registerConnectedPlayer(connectedPlayer);

                            } catch (IllegalArgumentException e) {
                                LOGGER.debug("This IllegalArgumentException was thrown: "+e.getMessage());
                                player.disconnectError(ErrorEvent.REQUEST_REGISTER_FAILED);
                            }
                        },
                        () -> {
                            LOGGER.warn("Failed to recover reconnecting player! " + player);
                            player.disconnectError(ErrorEvent.REQUEST_RECOVER_FAILED);
                        }
                );
            }
            // doesn't want to reconnect
            else player.disconnect();
        }

        // DisconnectRequest
        else if (request instanceof DisconnectRequest) {
            player.disconnect();
        }

        // Unexpected Request
        else player.disconnectError(ErrorEvent.REQUEST_UNEXPECTED);
    }

    /**
     * Handles a received request from a new connecting player.
     *
     * @param con     The socket, the request was received from.
     * @param request The request.
     */
    private void onNewPlayerRequest(WebSocket con, MMRequest request) {
        LOGGER.trace("Request from new player!");

        // HelloServer
        if (request instanceof HelloServer) {
            LOGGER.trace("HelloServer from new player");
            final HelloServer hs = (HelloServer) request;

            final ConnectingPlayer player = new ConnectingPlayer(con, hs.getName(), hs.getDeviceID());
            try {
                // register player
                net.registerConnectingPlayer(player);

                // send HelloClient
                player.send(new HelloClient(net.getLogic().isGameRunning()));

            } catch (IllegalStateException e) {
                player.disconnectError(ErrorEvent.REQUEST_REGISTER_FAILED);
            }
        }
        // Unexpected Request
        else {
            LOGGER.trace("Unexpected Request!");
            (new ConnectingPlayer(con, "NewPlayer", "none"))
                    .disconnectError(ErrorEvent.REQUEST_UNEXPECTED);
        }
    }

    /**
     * Parses an incoming message in a list of requests.
     * Login messages are a list of one request, while ingame messages may result in a list of requests.
     *
     * @param msg The incoming message.
     * @return A list of requests.
     */
    private List<MMRequest> parseRequest(@Nullable String msg) {
        final List<MMRequest> requests = new ArrayList<>();
        if (msg == null) return requests;

        try {
            final JsonObject obj = gson.fromJson(msg, JsonObject.class);

            if (obj.has("messageType")) {
                final String messageType = obj.get("messageType").getAsString();

                // Login
                switch (messageType) {
                    case "HELLO_SERVER":
                        requests.add(HelloServer.fromJson(obj));
                        break;
                    case "RECONNECT":
                        requests.add(Reconnect.fromJson(obj));
                        break;
                    case "PLAYER_READY":
                        requests.add(PlayerReady.fromJson(obj));
                        break;
                    case "CHARACTER_SELECTION":
                        requests.add(CharacterSelection.fromJson(obj));
                        break;
                    case "ERROR":
                        requests.add(ErrorRequest.fromJson(obj));
                        break;
                    default:
                        LOGGER.warn("Unrecognized messageType: " + messageType);
                }
            }// Ingame
            else if (obj.has("requestType")) {

                final String requestType = obj.get("requestType").getAsString();
                LOGGER.trace("Parsing " + requestType);
                switch (requestType) {
                    case "MeleeAttackRequest":
                        requests.add(MeleeAttackRequest.fromJson(obj));
                        break;
                    case "RangedAttackRequest":
                        requests.add(RangedAttackRequest.fromJson(obj));
                        break;
                    case "MoveRequest":
                        requests.add(MoveRequest.fromJson(obj));
                        break;
                    case "ExchangeInfinityStoneRequest":
                        requests.add(ExchangeInfinityStoneRequest.fromJson(obj));
                        break;
                    case "UseInfinityStoneRequest":
                        requests.add(UseInfinityStoneRequest.fromJson(obj));
                        break;
                    case "EndRoundRequest":
                        requests.add(new EndRoundRequest());
                        break;
                    case "PauseStartRequest":
                        requests.add(new PauseStartRequest());
                        break;
                    case "PauseStopRequest":
                        requests.add(new PauseStopRequest());
                        break;
                    case "DisconnectRequest":
                        requests.add(new DisconnectRequest());
                        break;
                    case "Req":
                        requests.add(new Req());
                        break;
                    default:
                        LOGGER.warn("Unrecognized requestType: " + requestType);
                }
            } else{
                LOGGER.warn("Neither requestType nor messageType.");
            }
        } catch (JsonSyntaxException | NullPointerException e) {
            LOGGER.warn("Cannot parse message as JsonObject! '" + msg + "'");
        }

        return requests;
    }

    @Override
    public void onOpen(WebSocket con, ClientHandshake hs) {
        LOGGER.trace("Opened connection to Client! " + con.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket con, int code, String reason, boolean remote) {
        net.getConnectedPlayer(con).ifPresentOrElse(
                net::unregisterConnectedPlayer,
                () -> net.getConnectingPlayer(con).ifPresentOrElse(
                        net::unregisterConnectingPlayer,
                        () -> LOGGER.warn("Closed Connection to unknown player!")
                )
        );
    }

    @Override
    public void onMessage(WebSocket con, String msg) {
        LOGGER.trace("Received message form "+con.getRemoteSocketAddress()+": " + msg);
        final List<MMRequest> requests = parseRequest(msg);
        LOGGER.trace("Parsed message to request(s): " + requests);
        requests.forEach(r -> onRequest(con, r));
    }

    @Override
    public void onMessage(WebSocket con, ByteBuffer msg) {
        LOGGER.error("Received unsupported Byte-Buffer message!");
        con.send(ErrorEvent.REQUEST_UNSUPPORTED.toJsonEvent());
    }

    @Override
    public void onError(WebSocket con, Exception ex) {
        LOGGER.error("Exception in WebSocket", ex);
    }

    @Override
    public void onStart() {
        LOGGER.debug("WebSocket started...");
    }

}
