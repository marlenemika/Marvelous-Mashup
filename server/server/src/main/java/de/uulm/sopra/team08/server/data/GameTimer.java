package de.uulm.sopra.team08.server.data;

import java.time.Instant;

/**
 * A Timer with the option to pause
 */
public class GameTimer {

    private Instant gameStartTime;

    private Instant pauseStartTime;


    public GameTimer() {

    }

    /**
     * Starts the Timer
     */
    public void startGameTimer() {
        gameStartTime = Instant.now();
    }

    /**
     * Returns the current time
     *
     * @return the stoped time in seconds
     */
    public long getTime() {
        if (gameStartTime == null) throw new IllegalStateException("The Timer was stoped before it was started");
        if (pauseStartTime != null) throw new IllegalStateException("The Timer was stoped before it was unpaused");
        return Instant.now().minusSeconds(gameStartTime.getEpochSecond()).getEpochSecond();
    }

    /**
     * pauses the timer
     */
    public void pause() {
        if (gameStartTime == null) throw new IllegalStateException("GameTimer wasn't started");
        if (pauseStartTime != null) throw new IllegalStateException("GameTimer is already paused");
        pauseStartTime = Instant.now();
    }

    /**
     * unpauses the timer
     */
    public void unpause() {
        if (gameStartTime == null) throw new IllegalStateException("GameTimer wasn't started");
        if (pauseStartTime == null) throw new IllegalStateException("GameTimer wasn't paused");
        gameStartTime = gameStartTime.plusSeconds(Instant.now().minusSeconds(pauseStartTime.getEpochSecond()).getEpochSecond());
        pauseStartTime = null;
    }

    /**
     * @return true if the game is paused
     */
    public boolean isPause() {
        return pauseStartTime != null;
    }

}
