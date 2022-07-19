package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.event.*;
import de.uulm.sopra.team08.server.data.PlayerBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.locks.ReentrantLock;

@ParametersAreNonnullByDefault
class ConnectingPlayer extends PlayerBase {

    private static final Logger LOGGER = LogManager.getLogger(ConnectingPlayer.class);
    /**
     * The connection to this player.
     */
    private final WebSocket con;
    /**
     * A lock to synchronize access to sending any events.
     */
    private final ReentrantLock lock;


    ConnectingPlayer(WebSocket con, String name, String deviceId) {
        super(name, deviceId);
        this.con = con;
        this.lock = new ReentrantLock();
    }


    /**
     * Sends an Event to this player.
     *
     * @param event the event to send.
     * @throws IllegalArgumentException If the given event is a response ({@link Ack} / {@link Nack}).
     */
    public void send(MMEvent event) {
        try {
            lock.lock();

            // closing socket
            if (con.isClosing() || con.isClosed()) {
                LOGGER.warn("Discarded Event! (Connection closed) " + event);
                return;
            }

            // ingame-Response
            if (event.getEventType().isIngame())
                throw new IllegalArgumentException("Cannot send ingame events to connecting player!");

            // Normal event
            final String send = event.toJsonEvent();
            LOGGER.trace("Sending to player: " + send);
            con.send(send);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Disconnects this player gently.
     * This closes the connection to the player.
     *
     * @param message The goodbye message.
     */
    public void disconnect(String message) {
        try {
            lock.lock();

            send(new GoodbyeClient(message));
            con.close();
            LOGGER.trace("Closed connection to player! " + this);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method for {@code disconnect("Goodbye Client!")}.
     *
     * @see #disconnect(String)
     */
    public void disconnect() {
        this.disconnect("Goodbye Client!");
    }

    /**
     * Disconnects this player gently with an error.
     *
     * @param error The Error to send before closing the connection.
     */
    public void disconnectError(ErrorEvent error) {
        try {
            lock.lock();

            send(error);
            disconnect("Disconnected due to an error!");

        } finally {
            lock.unlock();
        }
    }

    /**
     * @return the Connection to this player.
     */
    public WebSocket getCon() {
        return con;
    }

}
