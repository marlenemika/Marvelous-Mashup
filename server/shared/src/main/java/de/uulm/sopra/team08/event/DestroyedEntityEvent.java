package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * DestroyedEntity INGAME Event, sent to all clients when a Entity is destroyed.<br>
 * Includes when a InfinityStone is picked up.
 */
public class DestroyedEntityEvent extends MMIngameEvent {

    private final Tuple<EntityID, Integer> targetEntity;
    private final Tuple<Integer, Integer> targetField;


    /**
     * Creates a new DestroyedEntity INGAME Event
     *
     * @param targetField  the targetField as Tuple
     * @param targetEntity the targetEntity as Tuple of EntityID and ID
     */
    public DestroyedEntityEvent(@NotNull Tuple<Integer, Integer> targetField, @NotNull Tuple<EntityID, Integer> targetEntity) {
        super(EventType.DESTROYED_ENTITY);
        this.targetEntity = targetEntity;
        this.targetField = targetField;

    }

    /**
     * Convenience method to get a {@link DestroyedEntityEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link DestroyedEntityEvent}
     * @throws IllegalArgumentException if {@link DestroyedEntityEvent} could not be parsed from the {@link JsonObject}
     */
    public static DestroyedEntityEvent fromJson(JsonObject json) {
        try {
            // get targetEntity Object and create Tuple
            JsonObject targetEntity = json.getAsJsonObject("targetEntity");
            EntityID id = EntityID.valueOf(targetEntity.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> targetEntityTuple = new Tuple<>(id, targetEntity.get("ID").getAsInt());

            // get targetLocation Array and create Tuple
            JsonArray targetField = json.getAsJsonArray("targetField");
            Tuple<Integer, Integer> targetFieldTuple = new Tuple<>(targetField.get(0).getAsInt(), targetField.get(1).getAsInt());

            return new DestroyedEntityEvent(targetFieldTuple, targetEntityTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link DestroyedEntityEvent} into a JSON String
     *
     * @return {@link DestroyedEntityEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject destroyedEntityEvent = new JsonObject();

        // add JSON properties
        destroyedEntityEvent.addProperty("eventType", "DestroyedEntityEvent");
        destroyedEntityEvent.add("targetField", Tuple.toJsonArray(targetField));
        destroyedEntityEvent.add("targetEntity", gson.toJsonTree(targetEntity));

        // return JSON as String
        return gson.toJson(destroyedEntityEvent);
    }

    public Tuple<EntityID, Integer> getTargetEntity() {
        return targetEntity;
    }

    public Tuple<Integer, Integer> getTargetField() {
        return targetField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestroyedEntityEvent that = (DestroyedEntityEvent) o;

        if (!getTargetEntity().equals(that.getTargetEntity())) return false;
        return getTargetField().equals(that.getTargetField());
    }

    @Override
    public int hashCode() {
        int result = getTargetEntity().hashCode();
        result = 31 * result + getTargetField().hashCode();
        return result;
    }

}
