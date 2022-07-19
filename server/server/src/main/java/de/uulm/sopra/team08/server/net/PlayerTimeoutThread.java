package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.event.TimeoutEvent;
import de.uulm.sopra.team08.event.TimeoutWarningEvent;
import de.uulm.sopra.team08.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.MILLIS;

@SuppressWarnings("BusyWait")
class PlayerTimeoutThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(PlayerTimeoutThread.class);
    private final long timeoutWarningMillis;
    private final long timeoutMillis;
    /**
     * Maps a connected player to the time he last sent a message and saves, if a warning has been sent.
     */
    private final Map<ConnectedPlayer, Tuple<Instant, Boolean>> map;
    private boolean running = true;


    /**
     * Keeps track of all players and their last message sent.
     * If a player exceeds the timeout warning time, the player receives a {@link TimeoutWarningEvent}.
     * Furthermore, if the player exceeds the timeout time, the player receives a {@link TimeoutEvent},
     * will be disconnected and the listener will be informed.
     *
     * @param timeoutWarningMillis The time between the last message and a timeout warning in milliseconds.
     * @param timeoutMillis        The time between the timeout warning and the actual timeout.
     */
    PlayerTimeoutThread(long timeoutWarningMillis, long timeoutMillis) {
        this.timeoutWarningMillis = timeoutWarningMillis;
        this.timeoutMillis = timeoutMillis;
        this.map = new HashMap<>();
    }


    private Instant getLastMessage(ConnectedPlayer player) {
        synchronized (map) {
            return map.get(player).first;
        }
    }

    private long getFullTime(boolean isTimeout) {
        return isTimeout ? timeoutMillis + timeoutWarningMillis : timeoutWarningMillis;
    }

    /**
     * Registers a player on the timeout-list.
     * This sets the last message sent of this player to {@link Instant#now()}.
     *
     * @param player The player to register.
     */
    void registerPlayer(ConnectedPlayer player) {
        synchronized (map) {
            map.put(player, new Tuple<>(Instant.now(), false));
        }
    }

    /**
     * Removes a player from the timeout list.
     *
     * @param player The player to remove.
     */
    void unregisterPlayer(ConnectedPlayer player) {
        synchronized (map) {
            map.remove(player);
        }
    }

    /**
     * Resets the timeout timer on a player.
     *
     * @param player The player to reset the timer.
     */
    void playerSentMessage(ConnectedPlayer player) {
        synchronized (map) {
            if (!map.containsKey(player)) throw new IllegalArgumentException("Player not registered!");

            map.replace(player, new Tuple<>(Instant.now(), false));
        }
    }

    @Override
    public void run() {
        try {
            // keep time-outing until stopped
            while (running) {

                // find next player to send warning or timeout
                ConnectedPlayer player = null;
                long sleep = -1;
                boolean isTimeout = false;
                synchronized (map) {
                    for (var entry : map.entrySet()) {
                        final Instant pLastMsg = entry.getValue().first;
                        final boolean pIsTimeout = entry.getValue().second;

                        final long pSleep = MILLIS.between(Instant.now(), pLastMsg.plus(getFullTime(pIsTimeout), MILLIS));
                        // smaller wait time
                        if (sleep == -1 || pSleep < sleep) {
                            player = entry.getKey();
                            sleep = pSleep;
                            isTimeout = pIsTimeout;
                        }
                    }
                }

                // no player found
                if (player == null) {
                    sleep(timeoutWarningMillis);
                    continue;
                }

                // found player to handle
                sleep(Math.max(sleep, 0));

                // check - unregistered or lastMessage updated?
                if (!map.containsKey(player) // player unregistered
                    || MILLIS.between(getLastMessage(player), Instant.now()) < getFullTime(isTimeout) // message during sleep
                ) continue;

                // send event
                if (isTimeout) player.timeout(String.format("You timed out after %dms!", getFullTime(true)));
                else {
                    player.send(new TimeoutWarningEvent("You are about to timeout!", (int) (timeoutMillis / 1000)));
                    map.replace(player, new Tuple<>(map.get(player).first, true));
                }
            }
        } catch (InterruptedException ignore) {
            // interrupted with end()
        }
    }

    /**
     * Stopps this thread.
     */
    public synchronized void end() {
        this.running = false;
        this.interrupt();
    }

}
