package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * ConsumedMP INGAME Event, sent to all clients when a character moves.
 */
public class ConsumedMPEvent extends MMIngameEvent {

    private final Tuple<EntityID, Integer> targetEntity;
    private final Tuple<Integer, Integer> targetField;
    private final int amount;

    /**
     * Creates a new ConsumedMP INGAME Event
     *
     * @param targetEntity the targetEntity as Tuple of EntityID and ID
     * @param targetField  the targetField as Tuple
     * @param amount       the amount of MP consumed
     */
    public ConsumedMPEvent(@NotNull Tuple<EntityID, Integer> targetEntity, @NotNull Tuple<Integer, Integer> targetField, int amount) {
        super(EventType.CONSUMED_MP);
        this.targetEntity = targetEntity;
        this.targetField = targetField;
        this.amount = amount;
    }

    /**
     * Convenience method to get a {@link ConsumedMPEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link ConsumedMPEvent}
     * @throws IllegalArgumentException if {@link ConsumedMPEvent} could not be parsed from the {@link JsonObject}
     */
    public static ConsumedMPEvent fromJson(JsonObject json) {
        try {
            // get targetEntity Object and create Tuple
            JsonObject targetEntity = json.getAsJsonObject("targetEntity");
            EntityID id = EntityID.valueOf(targetEntity.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> targetEntityTuple = new Tuple<>(id, targetEntity.get("ID").getAsInt());

            // get targetLocation Array and create Tuple
            JsonArray targetField = json.getAsJsonArray("targetField");
            Tuple<Integer, Integer> targetFieldTuple = new Tuple<>(targetField.get(0).getAsInt(), targetField.get(1).getAsInt());

            // get damage amount
            int amount = json.get("amount").getAsInt();

            // create and return new event
            return new ConsumedMPEvent(targetEntityTuple, targetFieldTuple, amount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link ConsumedMPEvent} into a JSON String
     *
     * @return {@link ConsumedMPEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject consumedMPEvent = new JsonObject();

        // add JSON properties
        consumedMPEvent.addProperty("eventType", "ConsumedMPEvent");
        consumedMPEvent.add("targetEntity", gson.toJsonTree(targetEntity));
        consumedMPEvent.add("targetField", Tuple.toJsonArray(targetField));
        consumedMPEvent.addProperty("amount", amount);

        // return JSON as String
        return gson.toJson(consumedMPEvent);
    }

    public Tuple<EntityID, Integer> getTargetEntity() {
        return targetEntity;
    }

    public Tuple<Integer, Integer> getTargetField() {
        return targetField;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsumedMPEvent that = (ConsumedMPEvent) o;

        if (getAmount() != that.getAmount()) return false;
        if (!getTargetEntity().equals(that.getTargetEntity())) return false;
        return getTargetField().equals(that.getTargetField());
    }

    @Override
    public int hashCode() {
        int result = getTargetEntity().hashCode();
        result = 31 * result + getTargetField().hashCode();
        result = 31 * result + getAmount();
        return result;
    }

}
