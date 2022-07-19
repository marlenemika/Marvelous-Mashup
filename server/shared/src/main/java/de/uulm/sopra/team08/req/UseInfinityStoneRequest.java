package de.uulm.sopra.team08.req;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.terrain.Board;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * UseInfinityStone INGAME Request, sent when a character wants to use any InfinityStone
 */
public class UseInfinityStoneRequest extends MMIngameRequest {

    private final Tuple<EntityID, Integer> originEntity;
    private final Tuple<Integer, Integer> originField;
    private final Tuple<Integer, Integer> targetField;
    private final Tuple<EntityID, Integer> stoneType;


    /**
     * Creates a new UseInfinityStone INGAME Request
     *
     * @param originEntity the originEntity as Tuple of EntityID and ID
     * @param originField  the originField as Tuple
     * @param targetField  the targetField as Tuple
     * @param stoneType    the Infinitystone
     * @throws IllegalArgumentException if the stoneType isn't a InfinityStone
     */
    public UseInfinityStoneRequest(@NotNull Tuple<EntityID, Integer> originEntity, @NotNull Tuple<Integer, Integer> originField, @NotNull Tuple<Integer, Integer> targetField, @NotNull Tuple<EntityID, Integer> stoneType) throws IllegalArgumentException {
        super(MMRequest.requestType.USE_INFINITY_STONE);
        if (!stoneType.first.equals(EntityID.INFINITYSTONES))
            throw new IllegalArgumentException("the stoneType must be an Infinitystone");
        this.originEntity = originEntity;
        this.originField = originField;
        this.targetField = targetField;
        this.stoneType = stoneType;
    }

    /**
     * Convenience method to get a {@link UseInfinityStoneRequest} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link UseInfinityStoneRequest}
     * @throws IllegalArgumentException if {@link UseInfinityStoneRequest} could not be parsed from the {@link JsonObject}
     */
    public static UseInfinityStoneRequest fromJson(JsonObject json) {
        try {
            // get originEntity Object and create Tuple
            JsonObject originEntity = json.getAsJsonObject("originEntity");
            EntityID originid = EntityID.valueOf(originEntity.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> originEntityTuple = new Tuple<>(originid, originEntity.get("ID").getAsInt());

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

            return new UseInfinityStoneRequest(originEntityTuple, originFieldTuple, targetFieldTuple, stoneTypeTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link UseInfinityStoneRequest} into a JSON String
     *
     * @return {@link UseInfinityStoneRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        // create JSON object
        JsonObject useInfinityStoneEvent = new JsonObject();

        // add JSON property
        useInfinityStoneEvent.addProperty("requestType", "UseInfinityStoneRequest");
        useInfinityStoneEvent.add("originEntity", gson.toJsonTree(originEntity));
        useInfinityStoneEvent.add("originField", Tuple.toJsonArray(originField));
        useInfinityStoneEvent.add("targetField", Tuple.toJsonArray(targetField));
        useInfinityStoneEvent.add("stoneType", gson.toJsonTree(stoneType));

        // return JSON as String
        return gson.toJson(useInfinityStoneEvent);
    }

    public Tuple<EntityID, Integer> getOriginEntity() {
        return originEntity;
    }

    public Tuple<EntityID, Integer> getTargetEntity(Board gameBoard) {
        return gameBoard.getEntityAt(targetField).getIDs();
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

        UseInfinityStoneRequest that = (UseInfinityStoneRequest) o;

        if (!getOriginEntity().equals(that.getOriginEntity())) return false;
        if (!getOriginField().equals(that.getOriginField())) return false;
        if (!getTargetField().equals(that.getTargetField())) return false;
        return getStoneType().equals(that.getStoneType());
    }

    @Override
    public int hashCode() {
        int result = getOriginEntity().hashCode();
        result = 31 * result + getOriginField().hashCode();
        result = 31 * result + getTargetField().hashCode();
        result = 31 * result + getStoneType().hashCode();
        return result;
    }

}
