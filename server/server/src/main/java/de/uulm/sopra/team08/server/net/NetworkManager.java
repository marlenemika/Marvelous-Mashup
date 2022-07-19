package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.event.Ack;
import de.uulm.sopra.team08.event.MMEvent;
import de.uulm.sopra.team08.event.Nack;
import de.uulm.sopra.team08.req.HelloServer;
import de.uulm.sopra.team08.req.MMRequest;
import de.uulm.sopra.team08.req.PlayerReady;
import de.uulm.sopra.team08.req.Reconnect;
import de.uulm.sopra.team08.server.data.GameLogic;
import de.uulm.sopra.team08.server.data.IGameLogic;
import de.uulm.sopra.team08.server.data.Player;
import de.uulm.sopra.team08.util.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkManager {

    private static final Logger LOGGER = LogManager.getLogger(NetworkManager.class);
    private static NetworkManager instance;
    /**
     * A set of all {@link ConnectingPlayer connecting players}.
     * A player counts as connecting during the login phase of the server, while he has no role.
     * More specific: from {@link HelloServer} to {@link Reconnect} or {@link PlayerReady}.
     * <br>
     * Note: All players in this set are guaranteed to NOT exist in {@link #connectedPlayers}.
     */
    private final Set<ConnectingPlayer> connectingPlayers;
    /**
     * A set of all {@link ConnectedPlayer connected players}.
     * A player counts as connected, when he's fully connected to the server.
     * More specific: when the player was successfully transferred from a {@link ConnectingPlayer connecting player}
     * to a connected player after {@link Reconnect} or {@link PlayerReady}.
     * <br>
     * Note: All players in this set are guaranteed to NOT exist in {@link #connectingPlayers}.
     */
    private final Set<ConnectedPlayer> connectedPlayers;
    /**
     * A set of all known {@link Player players}.
     * A player counts as known, when he was successfully connected to the server once.
     * More specific: after he was registered as a {@link ConnectedPlayer}.
     * <br>
     * Note: A disconnected player will not be removed from this set.
     * Furthermore this set does not have a direct reference to a connected player (Only copys).
     */
    private final Set<Player> knownPlayers;
    private final Map<ConnectedPlayer, Boolean> invalidRequestMap;
    /**
     * A lock to synchronize access to all saved players.
     * More specific: {@link #connectingPlayers}, {@link #connectedPlayers} and {@link #knownPlayers}.
     */
    private final ReentrantLock playerLock;
    private final ReentrantLock logicLock;
    private final WebSocketServerImpl server;
    private final PlayerTimeoutThread timeoutThread;
    private boolean isShutdown = false;
    private IGameLogic logic;


    private NetworkManager(int port, IGameLogic logic, long timeoutMillis) {
        this.logic = logic;
        this.connectingPlayers = new HashSet<>();
        this.connectedPlayers = new HashSet<>();
        this.knownPlayers = new HashSet<>();
        this.invalidRequestMap = new HashMap<>();
        this.playerLock = new ReentrantLock();
        this.logicLock = new ReentrantLock();

        server = new WebSocketServerImpl(port, this);
        server.start();

        // 60% = warning
        timeoutThread = new PlayerTimeoutThread((long) (timeoutMillis * .6), timeoutMillis);
        timeoutThread.start();
    }


    public static void init(int port, IGameLogic logic, long timeoutMillis) {
        assert instance == null : "Cannot init a second time!";
        instance = new NetworkManager(port, logic, timeoutMillis);
    }

    public static NetworkManager getInstance() {
        assert instance != null : "Initialize first!";
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Disconnects a player if a request of this player could not be handled twice in a row.
     *
     * @param player  The player who sent the request.
     * @param success Whether the request could be handled successfully.
     */
    private void updateInvalidRequestMap(ConnectedPlayer player, boolean success) {
        // keep track of responses
        invalidRequestMap.putIfAbsent(player, false);
        boolean hasInvalidRequest = invalidRequestMap.get(player);
        // ack
        if (success) {
            if (hasInvalidRequest) invalidRequestMap.put(player, false);
        }
        // nack
        else {
            if (hasInvalidRequest) {
                player.disconnect("Too many invalid requests");
            } else invalidRequestMap.put(player, true);
        }
    }

    /**
     * Adds the given player to the {@link #knownPlayers}.
     *
     * @param player The player to add.
     */
    private void addToKnownPlayers(Player player) {
        try {
            playerLock.lock();

            // create copy to free eny reference to the original player, holding a reference to a WebSocket
            knownPlayers.add(new Player(player.getName(), player.getDeviceId(), player.getRole()));

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Retrieves a {@link ConnectingPlayer} with the connection.
     *
     * @param con The connection to match the {@link ConnectingPlayer}.
     * @return A full Optional with the matching {@link ConnectingPlayer} or an empty Optional, if no match was found.
     */
    Optional<ConnectingPlayer> getConnectingPlayer(WebSocket con) {
        try {
            playerLock.lock();

            for (ConnectingPlayer p : connectingPlayers)
                if (p.getCon().equals(con))
                    return Optional.of(p);
            return Optional.empty();

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Retrieves a connected {@link ConnectedPlayer} matching the given player.
     *
     * @param player The player to match the {@link ConnectedPlayer}.
     * @return A full Optional with the matching {@link ConnectedPlayer} or an empty Optional, if no match was found.
     */
    Optional<ConnectedPlayer> getConnectedPlayer(Player player) {
        final String uniqueID = player.getUniqueID();
        try {
            playerLock.lock();

            for (ConnectedPlayer p : connectedPlayers)
                if (p.getUniqueID().equals(uniqueID))
                    return Optional.of(p);
            return Optional.empty();

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Retrieves a connected {@link ConnectedPlayer} with the connection.
     *
     * @param con The connection to match the {@link ConnectedPlayer}.
     * @return A full Optional with the matching {@link ConnectedPlayer} or an empty Optional, if no match was found.
     */
    Optional<ConnectedPlayer> getConnectedPlayer(WebSocket con) {
        try {
            playerLock.lock();

            for (ConnectedPlayer p : connectedPlayers)
                if (p.getCon().equals(con))
                    return Optional.of(p);
            return Optional.empty();

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Searches the {@link #knownPlayers known players} for the given player matched by
     * {@link ConnectingPlayer#getUniqueID()}.
     *
     * @param player The player to search for.
     * @return An Optional with the Role present, or an empty Optional, if the player could not be found.
     */
    Optional<Role> recoverRole(ConnectingPlayer player) {
        try {
            playerLock.lock();

            for (Player p : knownPlayers)
                if (p.getUniqueID().equals(player.getUniqueID()))
                    return Optional.of(p.getRole());
            return Optional.empty();

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Registers a connecting player.
     *
     * @param player The player to register.
     * @throws IllegalArgumentException If a player with the same uniqueId exists in {@link #connectedPlayers}.
     * @throws IllegalArgumentException If this player is already registered as connecting.
     */
    void registerConnectingPlayer(ConnectingPlayer player) {
        try {
            playerLock.lock();
            LOGGER.trace("Registering connecting player " + player);
            if (isShutdown) throw new IllegalStateException("Server is down!");

            // already connected
            for (ConnectedPlayer p : connectedPlayers) {
                if (p.getUniqueID().equals(player.getUniqueID())) {
                    LOGGER.error("Cannot register connecting player, with uniqueId of connected player!");
                    throw new IllegalArgumentException("Player already registered as connected!");
                }
            }

            // already connecting
            if (!connectingPlayers.add(player))
                throw new IllegalArgumentException("Player already registered as connecting!");

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Unregisters a connecting player.
     *
     * @param player The player to unregister.
     */
    void unregisterConnectingPlayer(ConnectingPlayer player) {
        try {
            playerLock.lock();
            LOGGER.trace("Unregistering connecting player " + player);
            if (isShutdown) throw new IllegalStateException("Server is down!");

            if (!connectingPlayers.remove(player))
                LOGGER.warn("Removed non existing connecting player! " + player);

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Registers a connected player.
     * If the player could be added to {@link #connectedPlayers} successfully, he's added to {@link #knownPlayers}.
     * If the player is no {@link Role#SPECTATOR}, he's also registered in the {@link GameLogic}.
     *
     * @param player The player to register.
     * @throws IllegalArgumentException If the player could not be registered in the {@link GameLogic}.
     * @throws IllegalArgumentException If the given player could not be transferred from {@link #connectingPlayers}.
     * @throws IllegalStateException    If the player was found in {@link #connectingPlayers} but
     *                                  could not be added to {@link #connectedPlayers}.
     */
    void registerConnectedPlayer(ConnectedPlayer player) {
        try {
            playerLock.lock();
            LOGGER.trace("Registering connected player " + player);
            if (isShutdown) throw new IllegalStateException("Server is down!");

            // find connecting player
            ConnectingPlayer connectingPlayer = null;
            for (ConnectingPlayer p : connectingPlayers) {
                if (p.getUniqueID().equals(player.getUniqueID())) {
                    connectingPlayer = p;
                    break;
                }
            }

            // transfer to connected players
            if (connectingPlayer != null) {
                if (connectedPlayers.add(player)) {
                    connectingPlayers.remove(connectingPlayer);
                    timeoutThread.registerPlayer(player);
                    LOGGER.trace("Transferred connecting player to connected player! " + player);

                    // add to known players for reconnecting
                    addToKnownPlayers(player);

                    // register in logic
                    try {
                        logicLock.lock();

                        if (!logic.registerPlayer(player)) {
                            LOGGER.error("Could not register player in game logic! " + player);
                            throw new IllegalArgumentException("Could not register player in game logic!");
                        }
                    } finally {
                        logicLock.unlock();
                    }

                } else {
                    LOGGER.fatal("Illegal state! ConnectingPlayer in connectedPlayers!");
                    throw new IllegalStateException("ConnectingPlayer in connectedPlayers!");
                }
            } else {
                LOGGER.error("Could not transfer ConnectedPlayer from connectingPlayers!");
                throw new IllegalArgumentException("The given player is no connecting player!");
            }

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Unregisters a connected player.
     * If the player is no {@link Role#SPECTATOR}, then he's also unregistered from the {@link GameLogic}.
     *
     * @param player The player to unregister.
     * @throws IllegalStateException If the unregistered connected player could not be unregistered
     *                               from the {@link GameLogic}.
     */
    void unregisterConnectedPlayer(ConnectedPlayer player) {
        try {
            playerLock.lock();
            LOGGER.trace("Unregistering connected player " + player);
            if (isShutdown) throw new IllegalStateException("Server is down!");

            // unregister
            if (connectedPlayers.remove(player)) {
                timeoutThread.unregisterPlayer(player);

                // unregister player / ki in game logic
                try {
                    logicLock.lock();

                    if (!player.getRole().equals(Role.SPECTATOR)
                        && !logic.unregisterPlayer(player)
                    ) {
                        LOGGER.error("Could not unregister connected player from game logic!");
                        throw new IllegalStateException("Could not unregister connected player from game logic!");
                    }

                } finally {
                    logicLock.unlock();
                }

            } else LOGGER.warn("Removed non existing connected player! " + player);

        } finally {
            playerLock.unlock();
        }
    }

    void playerSentMessage(ConnectedPlayer player) {
        // reset timeout
        timeoutThread.playerSentMessage(player);
    }

    /**
     * Handles the given request in the {@link #logic} and sends a response, if the player is awaiting one.
     *
     * @param player  The player from whom the request was sent.
     * @param request The received request.
     */
    synchronized void handle(ConnectedPlayer player, MMRequest request) {
        if (isShutdown) throw new IllegalStateException("Server is down!");

        playerSentMessage(player);

        boolean success;
        try {
            logicLock.lock();
            success = logic.handle(player, request);
        } finally {
            logicLock.unlock();
        }

        // send response
        if (player.isAwaitingResponse()) {
            final MMEvent res = success ? new Ack() : new Nack();
            LOGGER.debug("Responding " + player + " with " + res + "on" + request.toJsonRequest());
            player.send(res);

            updateInvalidRequestMap(player, success);
        }

        // log
        else LOGGER.debug("Handled request " + request + " without response");
    }

    WebSocketServerImpl getServer() {
        return server;
    }

    /**
     * Send an event to the given (connected) player.
     * If the player is null, the method returns without effect.
     *
     * @param player The player to send the message to.
     * @param event  The event to send.
     */
    public synchronized void send(@Nullable Player player, MMEvent event) {
        LOGGER.debug("Send to " + player.toString()+": " + event);
        if (player == null) return;

        getConnectedPlayer(player).ifPresentOrElse(
                p -> p.send(event),
                () -> LOGGER.warn(String.format("Cannot send Event to not connected Player! (%s, %s)", player, event))
        );
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts an event to all (connected) players.
     *
     * @param event The event to broadcast.
     */
    public void send(MMEvent event) {
        try {
            playerLock.lock();
            if (isShutdown) throw new IllegalStateException("Server is down!");

            connectedPlayers.forEach(p -> send(p, event));

        } finally {
            playerLock.unlock();
        }
    }

    /**
     * Stopps the server and timeout thread.
     */
    public void shutdown() {
        try {
            playerLock.lock();

            if (isShutdown) {
                LOGGER.error("Already shutdown!");
                return;
            }

            // timeout
            timeoutThread.end();
            timeoutThread.join();

            // server
            server.stop(1000);

            isShutdown = true;
        } catch (InterruptedException e) {
            LOGGER.error(String.format("Could not shutdown %s!", NetworkManager.class.getSimpleName()), e);
        } finally {
            playerLock.unlock();
        }
    }

    /**
     * @return The {@link GameLogic} where all requests are handled.
     */
    public IGameLogic getLogic() {
        return logic;
    }

    public void setLogic(IGameLogic logic) {
        try {
            logicLock.lock();
            this.logic = logic;
        } finally {
            logicLock.unlock();
        }
    }

}
