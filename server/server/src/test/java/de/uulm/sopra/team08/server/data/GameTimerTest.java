package de.uulm.sopra.team08.server.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTimerTest {

    @Test
    void testTimer() throws InterruptedException {
        GameTimer gameTimer = new GameTimer();
        // simple start stop
        gameTimer.startGameTimer();
        Thread.sleep(1000);
        assertEquals(1, gameTimer.getTime());
        assertFalse(gameTimer.isPause());

        // with pause
        gameTimer.startGameTimer();
        Thread.sleep(1000);
        gameTimer.pause();
        assertTrue(gameTimer.isPause());
        Thread.sleep(2000);
        gameTimer.unpause();
        Thread.sleep(1000);
        assertTrue(4 > gameTimer.getTime() && gameTimer.getTime() >= 2);

        // invalid
        gameTimer = new GameTimer();
        assertThrows(IllegalStateException.class, gameTimer::pause);
        assertThrows(IllegalStateException.class, gameTimer::unpause);
        assertThrows(IllegalStateException.class, gameTimer::getTime);
        gameTimer.startGameTimer();
        assertThrows(IllegalStateException.class, gameTimer::unpause);
        gameTimer.pause();
        assertThrows(IllegalStateException.class, gameTimer::pause);
        assertThrows(IllegalStateException.class, gameTimer::getTime);

    }

}