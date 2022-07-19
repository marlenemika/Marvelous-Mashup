package de.uulm.sopra.team08.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {

    @Test
    void testTuple() {
        Tuple<String, Integer> t1 = new Tuple<>("abcTest", 123);

        assertEquals("(abcTest, 123)", t1.toString());

        assertNotEquals(t1, null);
        assertNotEquals(t1, "test");
    }

    @Test
    void testArrayUtils() {
        String s = ArrayUtils.getI(new String[]{"abc", "bcd", "def"}, 2);
        assertEquals("def", s);

        assertNull(ArrayUtils.getI(new String[]{"abc", "bcd", "def"}, 6));
    }

}
