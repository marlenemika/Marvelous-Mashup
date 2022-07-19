package de.uulm.sopra.team08.data.terrain;


import de.uulm.sopra.team08.data.entity.Entity;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Board {

    private final Field[][] fields;

    public Board(int sizeX, int sizeY) {
        // ensure the board is larger than 0
        if (sizeX <= 0 || sizeY <= 0) throw new IllegalArgumentException("the board size must be greater than 0");
        this.fields = new Field[sizeY][sizeX];
        // init field
        for (int y = 0; y < getDimensions().second; y++) {
            for (int x = 0; x < getDimensions().first; x++) {
                fields[y][x] = new Field();
            }
        }
    }

    /**
     * Calculates the distance between the two coordinates.
     *
     * @param pos0 Coordinate 0
     * @param pos1 Coordinate 1
     * @return min steps for the king on a chess field
     */
    public static int distance(Tuple<Integer, Integer> pos0, Tuple<Integer, Integer> pos1) {
        return Math.max(Math.abs(pos1.first - pos0.first), Math.abs(pos1.second - pos0.second));
    }

    /**
     * @param pos field coordinate
     * @return Field
     * @throws IllegalArgumentException coordinates out of board border
     */
    private Field getFieldAt(Tuple<Integer, Integer> pos) {
        if (isOutOfBorder(pos)) throw new IllegalArgumentException("coordinates out of board border");
        return fields[pos.second][pos.first];
    }

    /**
     * Returns the game board width and height as tuple
     *
     * @return dimensions
     */
    public Tuple<Integer, Integer> getDimensions() {
        return new Tuple<>(fields[0].length, fields.length);
    }

    /**
     * Returns the entity at pos
     *
     * @param pos entity coordinates
     * @return entity or null if field free
     * @throws IllegalArgumentException coordinates out of board border
     */
    public Entity getEntityAt(Tuple<Integer, Integer> pos) {
        return getFieldAt(pos).getEntity();
    }

    /**
     * Places an entity with pos to the game board
     *
     * @param entity entity to place
     * @param pos    coordinates to set on
     * @throws IllegalArgumentException field is not free or coordinates out of board border
     */
    public void setEntityAt(@NotNull Entity entity, Tuple<Integer, Integer> pos) {
        if (getFieldAt(pos).getEntity() != null)
            throw new IllegalArgumentException("field is not free");
        entity.setCoordinates(pos);
        getFieldAt(pos).setEntity(entity);
    }

    public void freeEntityAt(Tuple<Integer, Integer> pos) {
        getFieldAt(pos).setEntity(null);
    }

    /**
     * Get all entities on the board
     *
     * @return List of entities
     */
    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        forEachPosition(pos -> {
            if (getEntityAt(pos) != null) entities.add(getEntityAt(pos));
        });
        return entities;
    }

    /**
     * @param action Procedure called for each position on the Board
     */
    public void forEachPosition(Consumer<Tuple<Integer, Integer>> action) {
        for (int y = 0; y < getDimensions().second; y++) {
            for (int x = 0; x < getDimensions().first; x++) {
                action.accept(new Tuple<>(x, y));
            }
        }
    }

    /**
     * Returns a random field coordinate of any free field
     *
     * @return random free field coordinate
     */
    @Nullable
    public Tuple<Integer, Integer> getRandomFreePosition() {
        List<Tuple<Integer, Integer>> freePositions = getFreePositions();
        if (freePositions.size() == 0) return null;
        Collections.shuffle(freePositions);
        return freePositions.get(0);
    }

    /**
     * Returns a random field coordinate of any free field.
     * If no free field is available choose a random field with rock
     *
     * @return random free or rock field coordinate
     */
    @Nullable
    public Tuple<Integer, Integer> getRandomFreeRockPosition() {
        Tuple<Integer, Integer> randPos = getRandomFreePosition();
        if (randPos != null) return randPos;

        // random rock
        List<Tuple<Integer, Integer>> randCoordinates =
                getPositions().stream()
                        .filter(p -> getEntityAt(p) instanceof Rock)
                        .collect(Collectors.toList());
        Collections.shuffle(randCoordinates);
        if (randCoordinates.size() > 0) return randCoordinates.get(0);
        else return null;
    }

    /**
     * Recursively searches for a random free neighbour to the position start.
     * The recursion depth is limited to the amount of field on the game board.
     * When the limit is reached the method check for any random free field or return null if none is available
     *
     * @param start starting position
     * @return coordinate or null
     */
    @Nullable
    public Tuple<Integer, Integer> getRecursiveFreePosition(Tuple<Integer, Integer> start) {
        return getRecursiveFreePosition(start, getDimensions().first * getDimensions().second);
    }

    /**
     * Recursively searches for a random free neighbour to the position start.
     * The recursion depth is limited.
     * When the limit is reached the method check for any random free field or return null if none is available
     *
     * @param start starting position
     * @param limit depth limit for the recursion
     * @return coordinate or null
     */
    @Nullable
    public Tuple<Integer, Integer> getRecursiveFreePosition(Tuple<Integer, Integer> start, int limit) {
        // if recursion depth limit is reached get any random coordinate
        if (limit <= 0) return getRandomFreePosition();
        // get free neighbours
        List<Tuple<Integer, Integer>> freeNeighbours = getFreeNeighbourPositions(start);
        Collections.shuffle(freeNeighbours);
        if (freeNeighbours.size() > 0) {
            return freeNeighbours.get(0);
        } else {
            // get all neighbours
            List<Tuple<Integer, Integer>> neighbours = getNeighbourPositions(start);
            Collections.shuffle(neighbours);
            // recurse
            return getRecursiveFreePosition(neighbours.get(0), limit - 1);
        }
    }

    /**
     * Get all valid coordinates
     *
     * @return list of coordinates
     */
    public List<Tuple<Integer, Integer>> getPositions() {
        List<Tuple<Integer, Integer>> list = new ArrayList<>();
        forEachPosition(list::add);
        return list;
    }

    /**
     * Get all free valid coordinates
     *
     * @return list of coordinates
     */
    public List<Tuple<Integer, Integer>> getFreePositions() {
        return getPositions().stream().filter(p -> getEntityAt(p) == null).collect(Collectors.toList());
    }

    /**
     * Returns all coordinates of fields next to pos
     *
     * @param pos coordinate
     * @return List of neighbours coordinates. list size &lt; 8 if pos is a the edge of the game board.
     */
    public List<Tuple<Integer, Integer>> getNeighbourPositions(Tuple<Integer, Integer> pos) {
        List<Tuple<Integer, Integer>> next = new ArrayList<>(8);
        next.add(new Tuple<>(pos.first + 1, pos.second - 1));
        next.add(new Tuple<>(pos.first + 1, pos.second));
        next.add(new Tuple<>(pos.first + 1, pos.second + 1));
        next.add(new Tuple<>(pos.first - 1, pos.second - 1));
        next.add(new Tuple<>(pos.first - 1, pos.second));
        next.add(new Tuple<>(pos.first - 1, pos.second + 1));
        next.add(new Tuple<>(pos.first, pos.second + 1));
        next.add(new Tuple<>(pos.first, pos.second - 1));
        return next.stream().filter(p -> !isOutOfBorder(p)).collect(Collectors.toList());
    }

    /**
     * Returns all coordinates of fields next to pos which are free
     *
     * @param pos coordinate
     * @return List of free neighbours fields coordinates.
     */
    public List<Tuple<Integer, Integer>> getFreeNeighbourPositions(Tuple<Integer, Integer> pos) {
        return getNeighbourPositions(pos).stream().filter(p -> getEntityAt(p) == null).collect(Collectors.toList());
    }

    /**
     * Returns true if pos is a invalid coordinate
     *
     * @param pos coordinate
     * @return invalid coordinate?
     */
    public boolean isOutOfBorder(Tuple<Integer, Integer> pos) {
        return pos.first < 0 || pos.first >= getDimensions().first
               || pos.second < 0 || pos.second >= getDimensions().second;
    }

    /**
     * @param xStart x of first Field
     * @param yStart y of first Field
     * @param xEnd   x of second Field
     * @param yEnd   y of second Field
     * @return true if there is a line of sight between these Fields
     */
    public boolean canSee(int xStart, int yStart, int xEnd, int yEnd) {
        // out of border
        if (isOutOfBorder(new Tuple<>(xStart, yStart)) || isOutOfBorder(new Tuple<>(xEnd, yEnd)))
            return false;

        // init
        int dx = xEnd - xStart;
        int dy = yEnd - yStart;

        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        int sdx = Integer.compare(dx, 0); // signum
        int sdy = Integer.compare(dy, 0); // signum

        // prepare loop variable
        int ix = 0;
        int iy = 0;
        int decision;
        while (ix < adx || iy < ady) {
            // which direction should i go next
            // check weather ((0.5+ix) / adx) or ((0.5+iy) / ady) is smaller
            // rewritten to avoid division and float
            decision = (1 + 2 * ix) * ady - (1 + 2 * iy) * adx;
            if (decision == 0) {
                // diagonal
                xStart += sdx;
                yStart += sdy;
                ix++;
                iy++;
            } else if (decision < 0) {
                // horizontal
                xStart += sdx;
                ix++;
            } else {
                // vertical
                yStart += sdy;
                iy++;
            }
            // break if its the target
            if (xStart == xEnd && yStart == yEnd) break;
            // check weather the Field blocks sight
            Entity entity = getEntityAt(new Tuple<>(xStart, yStart));
            if (entity != null && entity.blocksSight())
                return false;
        }
        return true;
    }

    /**
     * @param start first Field
     * @param end   second Field
     * @return true if there is a line of sight between these Fields
     */
    public boolean canSee(Tuple<Integer, Integer> start, Tuple<Integer, Integer> end) {
        return canSee(start.first, start.second, end.first, end.second);
    }

    /**
     * @param start  first Field
     * @param target second Field through which the line goes
     * @return all field coordinates on a line from (excluding) first Field over second Field to the end of the board
     */
    public List<Tuple<Integer, Integer>> lineLaser(Tuple<Integer, Integer> start, Tuple<Integer, Integer> target) {
        List<Tuple<Integer, Integer>> coordinates = new ArrayList<>();

        // out of border
        if (isOutOfBorder(start) || isOutOfBorder(target))
            return coordinates;

        int xStart = start.first;
        int yStart = start.second;
        int xEnd = target.first;
        int yEnd = target.second;
        // init
        int dx = xEnd - xStart;
        int dy = yEnd - yStart;

        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        int sdx = Integer.compare(dx, 0); // signum
        int sdy = Integer.compare(dy, 0); // signum

        // prepare loop variable
        int ix = 0;
        int iy = 0;
        int decision;

        // init list
        while (true) {
            // which direction should i go next
            // check weather ((0.5+ix) / adx) or ((0.5+iy) / ady) is smaller
            // rewritten to avoid division and float
            decision = (1 + 2 * ix) * ady - (1 + 2 * iy) * adx;
            if (decision == 0) {
                // diagonal
                xStart += sdx;
                yStart += sdy;
                ix++;
                iy++;
            } else if (decision < 0) {
                // horizontal
                xStart += sdx;
                ix++;
            } else {
                // vertical
                yStart += sdy;
                iy++;
            }
            // out of bounds?
            if (xStart < 0 || xStart >= getDimensions().first || yStart < 0 || yStart >= getDimensions().second) break;
            coordinates.add(new Tuple<>(xStart, yStart));
        }
        return coordinates;
    }

}
