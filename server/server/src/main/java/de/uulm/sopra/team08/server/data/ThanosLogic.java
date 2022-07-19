package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.item.InfinityStone;
import de.uulm.sopra.team08.data.terrain.Board;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ThanosLogic {

    private static final Logger LOGGER = LogManager.getLogger(ThanosLogic.class);
    private final GameLogic gameLogic;
    private final Thanos thanos;
    private final Random random;
    private int[][] moveField;


    public ThanosLogic(Thanos thanos, GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.thanos = thanos;

        this.random = new Random();
    }

    /**
     * Handles Thanos needs in this turn to get closer to an InfinityStone.
     */
    private void move() {
        var moves = findPath();

        for (int i = 0; i < moves.size() - 1; i++) {
            var currentPos = moves.get(i);
            var futurePos = moves.get(i + 1);
            if (Board.distance(currentPos, futurePos) > 1)
                LOGGER.error("Thanos steps to far");
            Entity target = this.gameLogic.getEntityAt(futurePos);

            if (target instanceof Character && i + 2 == moves.size()) {
                LOGGER.debug(String.format("Character with Stone at (%d, %d)", futurePos.first, futurePos.second));
                Character characterTarget = ((Character) target);

                gameLogic.handleMove(thanos, futurePos);
                Arrays.stream(characterTarget.getInventory())
                        .filter(Objects::nonNull)
                        .forEach(infinityStone -> gameLogic.handleExchangeInfinityStone(characterTarget, thanos, infinityStone));
                gameLogic.handleMeleeAttack(thanos, target, characterTarget.getMaxHP() - characterTarget.getCurrentHP());

            } else if (target instanceof Character) {
                LOGGER.debug(String.format("Character without Stone at (%d, %d)", futurePos.first, futurePos.second));

                gameLogic.handleMove(thanos, futurePos);

            } else if (target instanceof Rock) {
                LOGGER.debug(String.format("Rock at (%d, %d)", futurePos.first, futurePos.second));
                Rock rockTarget = (Rock) target;

                gameLogic.handleDestroyEntity(rockTarget);
                gameLogic.handleConsumedAP(thanos, 1);
                gameLogic.handleMove(thanos, futurePos);

            } else if (target instanceof InfinityStoneEntity) {
                LOGGER.debug(String.format("Infinity stone at (%d, %d)", futurePos.first, futurePos.second));

                gameLogic.handleMove(thanos, futurePos);

            } else {
                LOGGER.debug(String.format("Nothing at (%d, %d)", futurePos.first, futurePos.second));

                gameLogic.handleMove(thanos, futurePos);

            }
        }
    }

    /**
     * Handles thanos disintegration of characters after he got all infinity stone.
     * One turn is handled.
     *
     * @return Still characters alive?
     */
    private boolean disintegrateCharacters() {
        final List<Character> characters = gameLogic.getPlayerCharacters();
        int size = characters.size();
        for (int i = size - 1; i >= 0; i--) {
            if (random.nextBoolean())
                gameLogic.handleDestroyEntity(characters.remove(i));
        }
        return characters.size() > 0;
    }

    /**
     * Returns true if the int in the moveField at the location (x,y) is equal to the given targetInt.
     *
     * @param x         the x coordinate in the moveField
     * @param y         the y coordinate in the moveField
     * @param targetInt the integer the moveField should contain at the given location
     * @return true if the int at the given location is equal to the given targetInt
     */
    private boolean isTargetInt(int x, int y, int targetInt) {
        boolean ret = false;
        try { // try-catch for IndexOutOfBounds (for simplicity)
            ret = moveField[x][y] == targetInt;
        } catch (Exception ignore) {
        }
        return ret;
    }

    /**
     * This method can be used to change the skipping behaviour of the searchIteration method.
     *
     * @param x the x coordinate of the field
     * @param y the y coordinate of the field
     * @return true if this field can be skipped
     */
    private boolean skip(int x, int y) {
        // if it is not MAX_VALUE than the field has already been checked
        return this.moveField[x][y] != Integer.MAX_VALUE;
    }

    /**
     * Searches the closest InfinityStone and returns all coordinates on the way
     * towards that InfinityStone that Thanos can currently walk.
     *
     * @return the shortest path to the closest InfinityStone Thanos is able to walk
     */
    protected ArrayList<Tuple<Integer, Integer>> findPath() {
        resetMoveField();
        ArrayDeque<Tuple<Integer, Integer>> deque = new ArrayDeque<>();

        var temp = searchStone();
        if (temp == null)
            throw new IllegalArgumentException("No InfinityStone found!");

        final StringBuilder sb = new StringBuilder("Move coordinates: ");
        deque.add(temp);
        sb.append(String.format("(%d, %d)", temp.first, temp.second));

        while ((temp = nextMoveIteration(deque.peekFirst().first, deque.peekFirst().second)) != null) {
            sb.append(String.format("<-(%d, %d)", temp.first, temp.second));
            deque.addFirst(temp);
        }
        LOGGER.debug(sb.toString());


        while (this.thanos.getCurrentMP() + 1 < deque.size())
            deque.removeLast(); // removes all steps that Thanos would have no MP for

        return new ArrayList<>(deque);
    }

    /**
     * Returns the field location that is closer to Thanos.
     *
     * @param x the x coordinate of the current field
     * @param y the y coordinate of the current field
     * @return the next field closer to thanos
     */
    protected Tuple<Integer, Integer> nextMoveIteration(int x, int y) {
        final int targetInt = moveField[x][y] - 1; // distance of the previous field

        // search for previous field
        if (isTargetInt(x - 1, y, targetInt)) return new Tuple<>(x - 1, y);
        else if (isTargetInt(x, y + 1, targetInt)) return new Tuple<>(x, y + 1);
        else if (isTargetInt(x + 1, y, targetInt)) return new Tuple<>(x + 1, y);
        else if (isTargetInt(x, y - 1, targetInt)) return new Tuple<>(x, y - 1);
        else if (isTargetInt(x - 1, y - 1, targetInt)) return new Tuple<>(x - 1, y - 1);
        else if (isTargetInt(x - 1, y + 1, targetInt)) return new Tuple<>(x - 1, y + 1);
        else if (isTargetInt(x + 1, y + 1, targetInt)) return new Tuple<>(x + 1, y + 1);
        else if (isTargetInt(x + 1, y - 1, targetInt)) return new Tuple<>(x + 1, y - 1);
        return null;
    }

    /**
     * This algorithm will search for the closest InfinityStone on the board by
     * searching at the closest unknown location to Thanos. While circling around
     * Thanos (going outwards) the distances in moveField are updated.
     *
     * @return the coordinates of the closest InfinityStone
     */
    protected Tuple<Integer, Integer> searchStone() {
        this.moveField[this.thanos.getX()][this.thanos.getY()] = 0; // Thanos location can be reached with 0 steps
        LinkedList<Tuple<Integer, Integer>> queue = new LinkedList<>();
        queue.add(new Tuple<>(this.thanos.getX(), this.thanos.getY()));

        boolean run = true;
        while (run && !queue.isEmpty()) { // search until a stone is found (or none is found)
            // move in all directions around the current location
            if (searchIteration(queue, -1, 0)) run = false;
            else if (searchIteration(queue, 0, 1)) run = false;
            else if (searchIteration(queue, 1, 0)) run = false;
            else if (searchIteration(queue, 0, -1)) run = false;
            else if (searchIteration(queue, -1, -1)) run = false;
            else if (searchIteration(queue, -1, 1)) run = false;
            else if (searchIteration(queue, 1, 1)) run = false;
            else if (searchIteration(queue, 1, -1)) run = false;
                // remove the head of the queue if no stone was found around the current location
            else queue.poll();
        }

        if (queue.peek() != null)
            LOGGER.debug(String.format("Field with InfinityStone found at (%d, %d)", queue.peekLast().first, queue.peekLast().second));
        else LOGGER.fatal("No InfinityStone was found");

        return queue.pollLast();
    }

    /**
     * dirX and dirY should be -1, 0 or 1 to check adjacent field.
     * <p>
     * This method will try to check the field next to the field in the queue in the given direction.
     * The field that is being checked will be added to the queue, if it was not already checked.
     *
     * @param queue the queue the new field might be added to
     * @param dirX  the checking direction in x direction
     * @param dirY  the checking direction in y direction
     * @return true if the field at the checked location contains an InfinityStone
     */
    protected boolean searchIteration(Queue<Tuple<Integer, Integer>> queue, int dirX, int dirY) {
        var location = queue.peek(); // current location
        boolean ret = false;

        if (location == null) return false;
        int nextX = location.first + dirX;
        int nextY = location.second + dirY;


        try { // try-catch for IndexOutOfBounds (for simplicity)
            if (skip(nextX, nextY)) return false;

            ret = fieldHasStone(nextX, nextY);

            // write the minimum amount of steps required to get to this field into the int[][]
            moveField[nextX][nextY] = Math.min(moveField[nextX][nextY], moveField[location.first][location.second] + 1);

            // add new location to queue to check later
            queue.add(new Tuple<>(nextX, nextY));
        } catch (Exception ignore) {
        }

        LOGGER.debug(String.format("Field at (%d, %d) contains InfinityStone: %b", nextX, nextY, ret));

        return ret;
    }

    /**
     * The field at the given coordinates gets checked if it holds an InfinityStoneEntity or a
     * Character with an InfinityStone in his inventory.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the field at the given coordinates contains an InfinityStone
     */
    protected boolean fieldHasStone(int x, int y) {
        try {
            Entity e = gameLogic.getEntityAt(new Tuple<>(x, y));
            if (e == null) return false;
            if (e instanceof Thanos) return false;
            if (e instanceof InfinityStoneEntity) return true;
            if (e instanceof InfinityStoneInventory) {
                InfinityStone[] inventory = ((InfinityStoneInventory) e).getInventory();
                for (InfinityStone stone : inventory) if (stone != null) return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    protected void resetMoveField() {
        var size = this.gameLogic.getBoardDimensions();
        this.moveField = new int[size.first][size.second];
        for (int[] ints : this.moveField) Arrays.fill(ints, Integer.MAX_VALUE);
    }

    protected int[][] getMoveField() {
        return moveField;
    }

    /**
     * Handles Thanos turn
     *
     * @return Thanos has done his turn and is not finished?
     */
    public boolean doTurn() {
        if (thanos.getInventoryList().size() < 6) {
            move();
            return true;
        } else {
            return disintegrateCharacters();
        }
    }

}
