package de.uulm.sopra.team08.req;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * ExchangeInfinityStone INGAME Request, sent when a character wants to pass their InfinityStone to an other character.
 */
public class ExchangeInfinityStoneRequest extends MMIngameRequest {

    private final Tuple<EntityID, Integer> originEntity;
    private final Tuple<EntityID, Integer> targetEntity;
    private final Tuple<Integer, Integer> originField;
    private final Tuple<Integer, Integer> targetField;
    private final Tuple<EntityID, Integer> stoneType;


    /**
     * Creates a new ExchangeInfinityStone INGAME Request
     *
     * @param originEntity the originEntity as Tuple of EntityID and ID
     * @param targetEntity the targetEntity as Tuple of EntityID and ID
     * @param originField  the originField as Tuple
     * @param targetField  the targetField as Tuple
     * @param stoneType    the Infinitystone
     * @throws IllegalArgumentException if the stoneType isn't a InfinityStone
     */
    public ExchangeInfinityStoneRequest(@NotNull Tuple<EntityID, Integer> originEntity, @NotNull Tuple<EntityID, Integer> targetEntity, @NotNull Tuple<Integer, Integer> originField, @NotNull Tuple<Integer, Integer> targetField, @NotNull Tuple<EntityID, Integer> stoneType) throws IllegalArgumentException {
        super(MMRequest.requestType.EXCHANGE_INFINITY_STONE);
        if (!stoneType.first.equals(EntityID.INFINITYSTONES))
            throw new IllegalArgumentException("the stoneType must be an Infinitystone");
        this.originEntity = originEntity;
        this.targetEntity = targetEntity;
        this.originField = originField;
        this.targetField = targetField;
        this.stoneType = stoneType;
    }

    /**
     * Convenience method to get a {@link ExchangeInfinityStoneRequest} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link ExchangeInfinityStoneRequest}
     * @throws IllegalArgumentException if {@link ExchangeInfinityStoneRequest} could not be parsed from the {@link JsonObject}
     */
    public static ExchangeInfinityStoneRequest fromJson(JsonObject json) {
        try {
            // get originEntity Object and create Tuple
            JsonObject originEntity = json.getAsJsonObject("originEntity");
            EntityID originid = EntityID.valueOf(originEntity.get("entityID").getAsString().toUpperCase().toUpperCase());
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

            return new ExchangeInfinityStoneRequest(originEntityTuple, targetEntityTuple, originFieldTuple, targetFieldTuple, stoneTypeTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link ExchangeInfinityStoneRequest} into a JSON String
     *
     * @return {@link ExchangeInfinityStoneRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        // create JSON Object
        JsonObject exchangeInfinityStoneEvent = new JsonObject();

        // add JSON Properties
        exchangeInfinityStoneEvent.addProperty("requestType", "ExchangeInfinityStoneRequest");
        exchangeInfinityStoneEvent.add("originEntity", gson.toJsonTree(originEntity));
        exchangeInfinityStoneEvent.add("targetEntity", gson.toJsonTree(targetEntity));
        exchangeInfinityStoneEvent.add("originField", Tuple.toJsonArray(originField));
        exchangeInfinityStoneEvent.add("targetField", Tuple.toJsonArray(targetField));
        exchangeInfinityStoneEvent.add("stoneType", gson.toJsonTree(stoneType));

        // return JsonObject as String
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

        ExchangeInfinityStoneRequest that = (ExchangeInfinityStoneRequest) o;

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
