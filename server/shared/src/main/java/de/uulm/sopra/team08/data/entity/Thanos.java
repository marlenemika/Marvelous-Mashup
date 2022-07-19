package de.uulm.sopra.team08.data.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;

/**
 * This class represents Thanos, an invulnerable character.
 *
 * @author Jan-Philipp
 */
public class Thanos extends Character {

    public final static int ID = 2;
    private int roundCounter = 1;

    /**
     * Parameterized Constructor.
     *
     * @param mp the maximum mp of all characters
     */
    public Thanos(int mp) {
        super("Thanos", mp, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, EntityID.NPC, ID);
    }


    /**
     * Refills AP and MP. Increases MP with every call by one.
     */
    @Override
    public void refillStats() {
        this.currentMP = this.getMaxMP() + this.roundCounter++;
        this.currentAP = this.getMaxAP();
    }

    @Override
    public int damageCharacter(int amount) {
        return 0;
    }

    @Override
    public void healCharacter(int amount) {
    }

    @Override
    public boolean isVulnerable() {
        return false;
    }

    @Override
    public boolean blocksMovement() {
        return true;
    }

    @Override
    public boolean isKnockedOut() {
        return false;
    }

    /**
     * Convenience method for getting Thanos as JsonElement
     *
     * @return Thanos JsonElement representation
     */
    public JsonElement toNPCJsonElement() {
        // standard specific JSON representation
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "NPC");
        jsonObject.addProperty("ID", 2);
        jsonObject.addProperty("MP", currentMP);
        jsonObject.add("stones", new JsonArray());
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }

    public void setMP(int mp) {
        this.currentMP = mp;
    }

}
