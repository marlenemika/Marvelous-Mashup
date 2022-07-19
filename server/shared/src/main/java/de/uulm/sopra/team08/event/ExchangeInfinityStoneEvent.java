package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * ExchangeInfinityStone INGAME Event, sent when a character passes their InfinityStone to an other character.
 */
public class ExchangeInfinityStoneEvent extends MMIngameEvent {

    private final Tuple<EntityID, Integer> originEntity;
    private final Tuple<EntityID, Integer> targetEntity;
    private final Tuple<Integer, Integer> originField;
    private final Tuple<Integer, Integer> targetField;
    private final Tuple<EntityID, Integer> stoneType;


    /**
     * Creates a new ExchangeInfinityStone INGAME Event
     *
     * @param originEntity the originEntity as Tuple of EntityID and ID
     * @param targetEntity the targetEntity as Tuple of EntityID and ID
     * @param originField  the originField as Tuple
     * @param targetField  the targetField as Tuple
     * @param stoneType    the Infinitystone
     * @throws IllegalArgumentException if the stoneType isn't a InfinityStone
     */
    public ExchangeInfinityStoneEvent(@NotNull Tuple<EntityID, Integer> originEntity, @NotNull Tuple<EntityID, Integer> targetEntity, @NotNull Tuple<Integer, Integer> originField, @NotNull Tuple<Integer, Integer> targetField, @NotNull Tuple<EntityID, Integer> stoneType) throws IllegalArgumentException {
        super(EventType.EXCHANGE_INFINITY_STONE);
        if (!stoneType.first.equals(EntityID.INFINITYSTONES))
            throw new IllegalArgumentException("the stoneType must be an Infinitystone");
        this.originEntity = originEntity;
        this.targetEntity = targetEntity;
        this.originField = originField;
        this.targetField = targetField;
        this.stoneType = stoneType;
    }

    /**
     * Convenience method to get a {@link ExchangeInfinityStoneEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link ExchangeInfinityStoneEvent}
     * @throws IllegalArgumentException if {@link ExchangeInfinityStoneEvent} could not be parsed from the {@link JsonObject}
     */
    public static ExchangeInfinityStoneEvent fromJson(JsonObject json) {
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

            // get InfinitystoneEntity and create Tuple
            JsonObject stoneType = json.getAsJsonObject("stoneType");
            EntityID stoneid = EntityID.valueOf(stoneType.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> stoneTypeTuple = new Tuple<>(stoneid, stoneType.get("ID").getAsInt());

            return new ExchangeInfinityStoneEvent(originEntityTuple, targetEntityTuple, originFieldTuple, targetFieldTuple, stoneTypeTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link ExchangeInfinityStoneEvent} into a JSON String
     *
     * @return {@link ExchangeInfinityStoneEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject exchangeInfinityStoneEvent = new JsonObject();

        // add JSON properties
        exchangeInfinityStoneEvent.addProperty("eventType", "ExchangeInfinityStoneEvent");
        exchangeInfinityStoneEvent.add("originEntity", gson.toJsonTree(originEntity));
        exchangeInfinityStoneEvent.add("targetEntity", gson.toJsonTree(targetEntity));
        exchangeInfinityStoneEvent.add("originField", Tuple.toJsonArray(originField));
        exchangeInfinityStoneEvent.add("targetField", Tuple.toJsonArray(targetField));
        exchangeInfinityStoneEvent.add("stoneType", gson.toJsonTree(stoneType));

        // return JSON to String
        return gson.toJson(exchangeInfinityStoneEvent);
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

    public Tuple<EntityID, Integer> getStoneType() {
        return stoneType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeInfinityStoneEvent that = (ExchangeInfinityStoneEvent) o;

        if (!getOriginEntity().equals(that.getOriginEntity())) return false;
        if (!getTargetEntity().equals(that.getTargetEntity())) return false;
        if (!getOriginField().equals(that.getOriginField())) return false;
        if (!getTargetField().equals(that.getTargetField())) return false;
        return getStoneType().equals(that.getStoneType());
    }

    @Override
    public int hashCode() {
        int result = getOriginEntity().hashCode();
        result = 31 * result + getTargetEntity().hashCode();
        result = 31 * result + getOriginField().hashCode();
        result = 31 * result + getTargetField().hashCode();
        result = 31 * result + getStoneType().hashCode();
        return result;
    }

}
