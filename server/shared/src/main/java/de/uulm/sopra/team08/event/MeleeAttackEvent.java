package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * MeleeAttack INGAME Event, sent when a character uses their melee attack on a character.
 */
public class MeleeAttackEvent extends MMIngameEvent {

    private final Tuple<EntityID, Integer> originEntity;
    private final Tuple<EntityID, Integer> targetEntity;
    private final Tuple<Integer, Integer> originField;
    private final Tuple<Integer, Integer> targetField;

    /**
     * Creates a new MeleeAttack INGAME Event
     *
     * @param originEntity the originEntity as Tuple of EntityID and ID
     * @param targetEntity the targetEntity as Tuple of EntityID and ID
     * @param originField  the originField as Tuple
     * @param targetField  the targetField as Tuple
     */
    public MeleeAttackEvent(@NotNull Tuple<EntityID, Integer> originEntity, @NotNull Tuple<EntityID, Integer> targetEntity, @NotNull Tuple<Integer, Integer> originField, @NotNull Tuple<Integer, Integer> targetField) {
        super(EventType.MELEE_ATTACK);
        this.originEntity = originEntity;
        this.targetEntity = targetEntity;
        this.originField = originField;
        this.targetField = targetField;
    }

    /**
     * Convenience method to get a {@link MeleeAttackEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link MeleeAttackEvent}
     * @throws IllegalArgumentException if {@link MeleeAttackEvent} could not be parsed from the {@link JsonObject}
     */
    public static MeleeAttackEvent fromJson(JsonObject json) {
        try {
            // get originEntity Object and create Tuple
            JsonObject originEntity = json.getAsJsonObject("originEntity");
            EntityID originid = EntityID.valueOf(originEntity.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> originEntityTuple = new Tuple<>(originid, originEntity.get("ID").getAsInt());

            // get targetEntity Object and create Tuple
            JsonObject targetEntity = json.getAsJsonObject("targetEntity");
            EntityID targetid = EntityID.valueOf(targetEntity.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> targetEntityTuple = new Tuple<>(targetid, targetEntity.get("ID").getAsInt());

            // get originLocation Array and create Tuple
            JsonArray originField = json.getAsJsonArray("originField");
            Tuple<Integer, Integer> originFieldTuple = new Tuple<>(originField.get(0).getAsInt(), originField.get(1).getAsInt());

            // get targetLocation Array and create Tuple
            JsonArray targetField = json.getAsJsonArray("targetField");
            Tuple<Integer, Integer> targetFieldTuple = new Tuple<>(targetField.get(0).getAsInt(), targetField.get(1).getAsInt());

            return new MeleeAttackEvent(originEntityTuple, targetEntityTuple, originFieldTuple, targetFieldTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link MeleeAttackEvent} into a JSON String
     *
     * @return {@link MeleeAttackEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject meleeAttackEvent = new JsonObject();

        // add JSON properties
        meleeAttackEvent.addProperty("eventType", "MeleeAttackEvent");
        meleeAttackEvent.add("originEntity", gson.toJsonTree(originEntity));
        meleeAttackEvent.add("targetEntity", gson.toJsonTree(targetEntity));
        meleeAttackEvent.add("originField", Tuple.toJsonArray(originField));
        meleeAttackEvent.add("targetField", Tuple.toJsonArray(targetField));

        // return JSON as String
        return gson.toJson(meleeAttackEvent);
    }

    public Tuple<EntityID, Integer> getOriginEntity() {
        return originEntity;
    }

    public Tuple<EntityID, Integer> getTargetEntity() {
        return targetEntity;
    }

    public Tuple<Integer, Integer> getOriginField() {
        return originField;
    }

    public Tuple<Integer, Integer> getTargetField() {
        return targetField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeleeAttackEvent that = (MeleeAttackEvent) o;

        if (!getOriginEntity().equals(that.getOriginEntity())) return false;
        if (!getTargetEntity().equals(that.getTargetEntity())) return false;
        if (!getOriginField().equals(that.getOriginField())) return false;
        return getTargetField().equals(that.getTargetField());
    }

    @Override
    public int hashCode() {
        int result = getOriginEntity().hashCode();
        result = 31 * result + getTargetEntity().hashCode();
        result = 31 * result + getOriginField().hashCode();
        result = 31 * result + getTargetField().hashCode();
        return result;
    }

}
