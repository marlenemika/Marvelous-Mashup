package de.uulm.sopra.team08.data.entity;

import com.google.gson.JsonElement;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;

import java.util.Objects;

/**
 * This class offers basic functionality of a Marvelous-Mashup entity.
 *
 * @author Jan-Philipp
 */
public abstract class Entity implements Vulnerable {

    protected final EntityID eID;
    protected final int id;
    protected int x;
    protected int y;

    protected Entity(EntityID eID, int id) {
        this.eID = eID;
        this.id = id;
    }

    /**
     * Returns true if other entities can't move on the field this entity is placed on.
     *
     * @return true if this entity blocks the field it's placed on
     */
    public boolean blocksMovement() {
        return false;
    }

    /**
     * Returns true if this entity blocks the sight of other entities.
     *
     * @return true if this entity blocks sight
     */
    public boolean blocksSight() {
        return false;
    }

    /**
     * Returns the current coordinates in a tuple in (x,y) format.
     *
     * @return the current coordinates as x,y
     */
    public Tuple<Integer, Integer> getCoordinates() {
        return new Tuple<>(this.x, this.y);
    }

    /**
     * Sets the current coordinates to the given tuple in (x,y) format.
     *
     * @param coordinates new coordinates with x, y coordinates
     */
    public void setCoordinates(Tuple<Integer, Integer> coordinates) {
        this.x = coordinates.first;
        this.y = coordinates.second;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public EntityID getEID() {
        return eID;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the ids combined to a tuple
     *
     * @return The id tuple
     */
    public Tuple<EntityID, Integer> getIDs() {
        return new Tuple<>(this.eID, this.id);
    }

    public abstract JsonElement toJsonElement();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return getX() == entity.getX() && getY() == entity.getY() && getId() == entity.getId() && getEID() == entity.getEID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getEID(), getId());
    }

    /**
     * @param tuple A Tuple of EntityID and ID
     * @return true if the id's match
     */
    public boolean doesIDMatch(Tuple<EntityID, Integer> tuple) {
        return eID.equals(tuple.first) && tuple.second.equals(id);
    }

}
