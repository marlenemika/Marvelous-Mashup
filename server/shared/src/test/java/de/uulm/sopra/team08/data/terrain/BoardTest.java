package de.uulm.sopra.team08.data.terrain;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.Entity;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void canSee() {
        final Board board = new Board(10, 10);
        final Rock rock = new Rock(1);
        // fill all with Rock's
        board.forEachPosition(p -> board.setEntityAt(rock, p));

        // diagonal
        board.freeEntityAt(new Tuple<>(1, 1));
        // with Tuple to test this too
        assertFalse(board.canSee(new Tuple<>(0, 0), new Tuple<>(3, 3)));
        assertFalse(board.canSee(3, 3, 0, 0));
        board.freeEntityAt(new Tuple<>(2, 2));
        assertTrue(board.canSee(0, 0, 3, 3));
        assertTrue(board.canSee(3, 3, 0, 0));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(rock, p));

        // horizontal
        board.freeEntityAt(new Tuple<>(1, 0));
        assertFalse(board.canSee(0, 0, 3, 0));
        assertFalse(board.canSee(3, 0, 0, 0));
        board.freeEntityAt(new Tuple<>(2, 0));
        assertTrue(board.canSee(0, 0, 3, 0));
        assertTrue(board.canSee(3, 0, 0, 0));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(rock, p));

        // vertical
        board.freeEntityAt(new Tuple<>(0, 1));
        assertFalse(board.canSee(0, 0, 0, 3));
        assertFalse(board.canSee(0, 3, 0, 0));
        board.freeEntityAt(new Tuple<>(0, 2));
        assertTrue(board.canSee(0, 0, 0, 3));
        assertTrue(board.canSee(0, 3, 0, 0));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(rock, p));

        // more special cases
        assertFalse(board.canSee(0, 0, 2, 1));
        assertFalse(board.canSee(2, 1, 0, 0));
        board.freeEntityAt(new Tuple<>(1, 0));
        assertFalse(board.canSee(0, 0, 2, 1));
        assertFalse(board.canSee(2, 1, 0, 0));
        board.freeEntityAt(new Tuple<>(1, 1));
        assertTrue(board.canSee(0, 0, 2, 1));
        assertTrue(board.canSee(2, 1, 0, 0));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(rock, p));

        // trough an edge
        assertFalse(board.canSee(0, 0, 3, 1));
        assertFalse(board.canSee(3, 1, 0, 0));
        board.freeEntityAt(new Tuple<>(1, 0));
        assertFalse(board.canSee(0, 0, 2, 1));
        assertFalse(board.canSee(3, 1, 0, 0));
        board.freeEntityAt(new Tuple<>(2, 1));
        assertTrue(board.canSee(0, 0, 3, 1));
        assertTrue(board.canSee(3, 1, 0, 0));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(rock, p));

        // out of border
        board.freeEntityAt(new Tuple<>(9, 0));
        board.freeEntityAt(new Tuple<>(0, 9));
        assertFalse(board.canSee(10, 0, 9, 0));
        assertFalse(board.canSee(9, 0, 10, 0));
        assertFalse(board.canSee(0, 10, 0, 9));
        assertFalse(board.canSee(0, 9, 0, 10));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(rock, p));
    }

    @Test
    void lineLaser() {
        final Board board = new Board(5, 5);
        final List<Tuple<Integer, Integer>> list = new ArrayList<>();

        // horizontal
        list.add(new Tuple<>(3, 2));
        list.add(new Tuple<>(4, 2));
        assertEquals(list, board.lineLaser(new Tuple<>(2, 2), new Tuple<>(3, 2)));
        list.clear();

        // vertical
        list.add(new Tuple<>(1, 3));
        list.add(new Tuple<>(1, 4));
        assertEquals(list, board.lineLaser(new Tuple<>(1, 2), new Tuple<>(1, 4)));
        list.clear();

        // diagonal
        list.add(new Tuple<>(2, 2));
        list.add(new Tuple<>(1, 1));
        list.add(new Tuple<>(0, 0));
        assertEquals(list, board.lineLaser(new Tuple<>(3, 3), new Tuple<>(1, 1)));
        list.clear();

        // special
        list.add(new Tuple<>(1, 2));
        list.add(new Tuple<>(1, 1));
        list.add(new Tuple<>(2, 1));
        list.add(new Tuple<>(3, 1));
        list.add(new Tuple<>(3, 0));
        list.add(new Tuple<>(4, 0));
        assertEquals(list, board.lineLaser(new Tuple<>(0, 2), new Tuple<>(4, 0)));
        list.clear();

        // out of border
        assertEquals(list, board.lineLaser(new Tuple<>(3, 0), new Tuple<>(5, 0)));
        assertEquals(list, board.lineLaser(new Tuple<>(5, 0), new Tuple<>(3, 0)));
        assertEquals(list, board.lineLaser(new Tuple<>(0, 3), new Tuple<>(0, 5)));
        assertEquals(list, board.lineLaser(new Tuple<>(0, 5), new Tuple<>(0, 3)));
    }

    @Test
    void utilFunctions() {
        final Board board = new Board(10, 10);
        final Rock r1 = new Rock(1);
        final Rock r2 = new Rock(2);
        final Tuple<Integer, Integer> p1 = new Tuple<>(1, 1);
        final Tuple<Integer, Integer> p2 = new Tuple<>(1, 2);
        final Character c1 = new Character("test", 1, 1, 1, 1, 1, 1, EntityID.P1, 1);

        // invalid setEntityAt
        board.setEntityAt(r1, p1);
        assertThrows(IllegalArgumentException.class, () -> board.setEntityAt(r1, p1));

        // getEntities
        board.setEntityAt(r2, p2);
        final List<Entity> entities = board.getEntities();
        assertEquals(2, entities.size());
        assertTrue(entities.contains(r1));
        assertTrue(entities.contains(r2));

        // randomFreePosition
        final Tuple<Integer, Integer> randomFreePosition = board.getRandomFreePosition();
        assertNull(board.getEntityAt(randomFreePosition));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(r1, p));
        assertNull(board.getRandomFreePosition());

        // randomFreeRock
        board.forEachPosition(board::freeEntityAt);
        assertNull(board.getEntityAt(board.getRandomFreeRockPosition()));
        board.forEachPosition(p -> board.setEntityAt(r1, p));
        assertEquals(r1, board.getEntityAt(board.getRandomFreeRockPosition()));
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(c1, p));
        assertNull(board.getRandomFreeRockPosition());

        // recursive random position
        board.forEachPosition(board::freeEntityAt);
        board.forEachPosition(p -> board.setEntityAt(r1, p));
        final Tuple<Integer, Integer> freePos = new Tuple<>(1, 3);
        board.freeEntityAt(freePos);
        assertEquals(freePos, board.getRecursiveFreePosition(new Tuple<>(1, 1)));


        // distance
        assertEquals(3, Board.distance(new Tuple<>(0, 0), new Tuple<>(1, 3)));
        assertEquals(1, Board.distance(new Tuple<>(0, 0), new Tuple<>(1, 1)));
        assertEquals(5, Board.distance(new Tuple<>(0, 0), new Tuple<>(2, 5)));

    }

}
