package de.uulm.sopra.team08.data.terrain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.entity.Entity;
import de.uulm.sopra.team08.util.Tuple;

/**
 * This class represents a portal, a invulnerable entity that blocks paths and sight.
 *
 * @author Markus Thielker
 */
public class Portal extends Entity {

    public Portal(int id) {
        super(EntityID.PORTALS, id);
    }

    @Override
    public boolean blocksMovement() {
        return false;
    }

    @Override
    public boolean blocksSight() {
        return true;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "Portal");
        jsonObject.addProperty("ID", this.id);
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }
}
