package de.uulm.sopra.team08.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WinEventTest {

    @Test
    void testInit() {

        // 1 or 2
        assertDoesNotThrow(() -> new WinEvent(1));
        assertDoesNotThrow(() -> new WinEvent(2));

        // not 1 or 2
        assertThrows(IllegalArgumentException.class, () -> new WinEvent(-1));
        assertThrows(IllegalArgumentException.class, () -> new WinEvent(0));
        assertThrows(IllegalArgumentException.class, () -> new WinEvent(3));

    }

}