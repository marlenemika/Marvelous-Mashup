package de.uulm.sopra.team08.data.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;

/**
 * This entity represents an infinity stone that has been placed on a field.
 *
 * @author Jan-Philipp
 */
public class InfinityStoneEntity extends Entity {

    /**
     * Parameterized constructor.
     */
    public InfinityStoneEntity(int id) {
        super(EntityID.INFINITYSTONES, id);
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "InfinityStone");
        jsonObject.addProperty("ID", this.id);
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }

}
