package de.uulm.sopra.team08.data.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;

/**
 * This class helps implementing the functionality of Stan Lee in
 * Marvelous-Mashup.
 *
 * @author Jan-Philipp
 */
public class StanLee extends Character {

    public final static int ID = 1;

    public StanLee() {
        super("Stan Lee", 1, 1, 0, 0, 0, 0, EntityID.NPC, ID);
    }


    /**
     * Fully restores the hp of all given characters.
     *
     * @param characters all characters that should be fully healed
     */
    public void healCharacters(Character... characters) {
        for (Character c : characters) {
            c.healCharacter(c.getMaxHP());
        }
    }

    /**
     * Filters the given entities for characters and heals them fully.
     *
     * @param entities all entities that should be fully healed
     */
    public void healCharacters(Entity... entities) {
        for (Entity entity : entities) {
            if (entity instanceof Character) {
                Character c = (Character) entity;
                c.healCharacter(c.getMaxHP());
            }
        }
    }

    /**
     * Convenience method for getting Stan Lee as JsonElement
     *
     * @return Stan Lee JsonElement representation
     */
    public JsonElement toNPCJsonElement() {
        // standard specific JSON representation
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "NPC");
        jsonObject.addProperty("ID", 1);
        jsonObject.addProperty("MP", 0);
        jsonObject.add("stones", new JsonArray());
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }

}
