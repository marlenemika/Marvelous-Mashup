package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * RangedAttack INGAME Event, sent when a character uses their ranged attack on a character.
 */
public class RangedAttackEvent extends MMIngameEvent {


    private final Tuple<EntityID, Integer> originEntity;
    private final Tuple<EntityID, Integer> targetEntity;
    private final Tuple<Integer, Integer> originField;
    private final Tuple<Integer, Integer> targetField;

    /**
     * Creates a new RangedAttack INGAME Event
     *
     * @param originEntity the originEntity as Tuple of EntityID and ID
     * @param targetEntity the targetEntity as Tuple of EntityID and ID
     * @param originField  the originField as Tuple
     * @param targetField  the targetField as Tuple
     */
    public RangedAttackEvent(@NotNull Tuple<EntityID, Integer> originEntity, @NotNull Tuple<EntityID, Integer> targetEntity, @NotNull Tuple<Integer, Integer> originField, @NotNull Tuple<Integer, Integer> targetField) {
        super(EventType.RANGED_ATTACK);
        this.originEntity = originEntity;
        this.targetEntity = targetEntity;
        this.originField = originField;
        this.targetField = targetField;
    }

    /**
     * Convenience method to get a {@link RangedAttackEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link RangedAttackEvent}
     * @throws IllegalArgumentException if {@link RangedAttackEvent} could not be parsed from the {@link JsonObject}
     */
    public static RangedAttackEvent fromJson(JsonObject json) {
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

            return new RangedAttackEvent(originEntityTuple, targetEntityTuple, originFieldTuple, targetFieldTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link RangedAttackEvent} into a JSON String
     *
     * @return {@link RangedAttackEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject rangedAttackEvent = new JsonObject();

        // add JSON properties
        rangedAttackEvent.addProperty("eventType", "RangedAttackEvent");
        rangedAttackEvent.add("originEntity", gson.toJsonTree(originEntity));
        rangedAttackEvent.add("targetEntity", gson.toJsonTree(targetEntity));
        rangedAttackEvent.add("originField", Tuple.toJsonArray(originField));
        rangedAttackEvent.add("targetField", Tuple.toJsonArray(targetField));

        // return JSON as String
        return gson.toJson(rangedAttackEvent);
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

        RangedAttackEvent that = (RangedAttackEvent) o;

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
