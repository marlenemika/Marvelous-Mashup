package de.uulm.sopra.team08.data.terrain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.entity.Entity;
import de.uulm.sopra.team08.util.Tuple;

/**
 * This class represents a rock, a vulnerable entity that blocks paths and sight.
 *
 * @author Jan-Philipp
 */
public class Rock extends Entity {

    private int hp;


    public Rock(int id) {
        super(EntityID.ROCKS, id);
        this.hp = 100;
    }

    /**
     * Deals damage to this rock. Returns true if this rock has 0 or less hp after damage calculation.
     *
     * @param damage the amount of damage dealt to this rock
     * @return true if this rock has 0 or less hp
     */
    public boolean damage(int damage) {
        this.hp -= damage;
        return this.isDestroyed();
    }

    /**
     * Returns true if hp are 0 or lower.
     *
     * @return true if hp are 0 or lower
     */
    public boolean isDestroyed() {
        return this.hp <= 0;
    }

    @Override
    public boolean isVulnerable() {
        return true;
    }

    @Override
    public boolean blocksMovement() {
        return true;
    }

    @Override
    public boolean blocksSight() {
        return true;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "Rock");
        jsonObject.addProperty("HP", this.hp);
        jsonObject.addProperty("ID", this.id);
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }

    public int getHp() {
        return hp;
    }

}
