package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.event.*;
import de.uulm.sopra.team08.req.MMRequest;
import de.uulm.sopra.team08.server.data.Player;
import de.uulm.sopra.team08.util.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@ParametersAreNonnullByDefault
class ConnectedPlayer extends Player {

    private static final Logger LOGGER = LogManager.getLogger(ConnectedPlayer.class);
    /**
     * The connection to this player.
     */
    private final WebSocket con;
    /**
     * Queues all events (except {@link Ack} and {@link Nack}) to be sent to the player.
     * This queue can only be flushed (see {@link #flushEvents()}) when this player is not waiting for a response.
     */
    private final BlockingQueue<MMEvent> eventQueue;
    /**
     * A lock to synchronize access to sending any events.
     */
    private final ReentrantLock lock;
    /**
     * A representation of how many responses this player is waiting for.
     *
     * @see #isAwaitingResponse()
     */
    private final AtomicInteger awaitedResponses;


    ConnectedPlayer(String name, String deviceId, Role role, WebSocket con) {
        super(name, deviceId, role);
        this.con = con;
        this.eventQueue = new LinkedBlockingQueue<>();
        this.lock = new ReentrantLock();
        this.awaitedResponses = new AtomicInteger(0);
    }


    /**
     * Clears the {@link #eventQueue} if this player is not waiting for any responses by sending all events.
     */
    private void flushEvents() {
        try {
            lock.lock();

            if (isAwaitingResponse())
                throw new IllegalStateException("Cannot flush event queue while waiting for responses!");

            MMEvent e;
            while ((e = eventQueue.poll()) != null) {
                final String send = e.toJsonEvent();
                LOGGER.trace("Sending to player (" + this.getName() + "): " + send);
                con.send(send);
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * Informs this player, that a received {@link MMRequest request} is awaiting a response.
     */
    void awaitResponse() {
        awaitedResponses.incrementAndGet();
    }

    /**
     * Sends an Event to this player.
     * If this player is awaiting a response, the event will be queued until an {@link Ack} or {@link Nack} is sent.
     *
     * @param event the event to send.
     * @throws IllegalArgumentException If the given event is a response and this player is not awaiting any responses.
     */
    void send(MMEvent event) {
        try {
            lock.lock();

            // closing socket
            if (con.isClosing() || con.isClosed()) {
                LOGGER.warn("Discarded Event! (Connection closed) " + event);
                return;
            }

            // Response
            if (event instanceof Ack || event instanceof Nack) {
                // send ack/nack when waiting for one
                if (isAwaitingResponse()) {
                    final String send = event.toJsonEvent();
                    LOGGER.trace("Sending to player: " + send);
                    con.send(send);
                    awaitedResponses.decrementAndGet();
                    LOGGER.debug("Sent Response to " + this);

                    // if last awaited response -> clear event-queue
                    if (!isAwaitingResponse()) flushEvents();

                } else throw new IllegalArgumentException(String.format("Cannot send %s-Event! (No awaited response)",
                        event.getClass().getSimpleName()));
            }

            // Normal Event
            else {
                // add to queue
                try {
                    eventQueue.add(event);
                } catch (IllegalStateException e) {
                    LOGGER.error("Cannot add Event in queue! (Queue is full!)");
                } catch (ClassCastException | NullPointerException | IllegalArgumentException e) {
                    LOGGER.fatal("Error while adding Event in queue!" + e);
                }

                // not waiting -> flush queue
                if (!isAwaitingResponse()) flushEvents();
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * Times this player out gently.
     * This closes the connection to the player.
     *
     * @param message The timeout message.
     */
    void timeout(String message) {
        try {
            lock.lock();

            // discard awaited responses
            if (isAwaitingResponse()) {
                LOGGER.warn("Player timing out is waiting for response! (GameLogic failed?)");
                if (NetSettings.DISCARD_RESPONSES_ON_DISCONNECT) {
                    LOGGER.trace("Overriding awaited responses with 0 to shutdown!");
                    awaitedResponses.set(0);
                } else {
                    LOGGER.trace("Sending Nacks to timeout gently!");
                    while (isAwaitingResponse()) send(new Nack());
                }
            }

            // send goodbye and close
            send(new TimeoutEvent(message));
            con.close();
            LOGGER.trace("Closed connection to player (timeout)! " + this);

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
    void disconnect(String message) {
        try {
            lock.lock();

            // discard awaited responses
            if (isAwaitingResponse()) {
                if (NetSettings.DISCARD_RESPONSES_ON_DISCONNECT) {
                    LOGGER.trace("Overriding awaited responses with 0 to shutdown!");
                    awaitedResponses.set(0);
                } else {
                    LOGGER.trace("Sending Nacks to disconnect gently!");
                    while (isAwaitingResponse()) send(new Nack());
                }
            }

            // send goodbye and close
            send(new GoodbyeClient(message));
            con.close();
            LOGGER.trace("Closed connection to player (disconnect)! " + this);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method for {@code disconnect("Goodbye Client!")}.
     *
     * @see #disconnect(String)
     */
    void disconnect() {
        this.disconnect("Goodbye Client!");
    }

    /**
     * Disconnects this player gently with an error.
     *
     * @param error The Error to send before closing the connection.
     */
    void disconnectError(ErrorEvent error) {
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
    WebSocket getCon() {
        return con;
    }

    /**
     * Checks if this player is waiting for a response.
     *
     * @return whether this Player is waiting for a response ({@link Ack} or {@link Nack}).
     */
    public boolean isAwaitingResponse() {
        return awaitedResponses.get() > 0;
    }

    /**
     * @return the amount of awaited responses.
     */
    public int getAwaitedResponses() {
        return awaitedResponses.get();
    }

}
