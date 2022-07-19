package de.uulm.sopra.team08.req;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * Move INGAME Request, sent when a character wants to move.
 */
public class MoveRequest extends MMIngameRequest {

    private final Tuple<EntityID, Integer> originEntity;
    private final Tuple<Integer, Integer> originField;
    private final Tuple<Integer, Integer> targetField;

    /**
     * Creates a new Move INGAME Request
     *
     * @param originEntity the originEntity as Tuple of EntityID and ID
     * @param originField  the originField as Tuple
     * @param targetField  the targetField as Tuple
     */
    public MoveRequest(@NotNull Tuple<EntityID, Integer> originEntity, @NotNull Tuple<Integer, Integer> originField, @NotNull Tuple<Integer, Integer> targetField) {
        super(MMRequest.requestType.MOVE);
        this.originEntity = originEntity;
        this.originField = originField;
        this.targetField = targetField;
    }

    /**
     * Convenience method to get a {@link MoveRequest} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link MoveRequest}
     * @throws IllegalArgumentException if {@link MoveRequest} could not be parsed from the {@link JsonObject}
     */
    public static MoveRequest fromJson(JsonObject json) {
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

            // return MoveRequest with given parameters
            return new MoveRequest(originEntityTuple, originFieldTuple, targetFieldTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link MoveRequest} into a JSON String
     *
     * @return {@link MoveRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        // Create JSON Object
        JsonObject moveEvent = new JsonObject();

        // add JSON properties
        moveEvent.addProperty("requestType", "MoveRequest");
        moveEvent.add("originEntity", gson.toJsonTree(originEntity));
        moveEvent.add("originField", Tuple.toJsonArray(originField));
        moveEvent.add("targetField", Tuple.toJsonArray(targetField));

        // return JSON as String
        return gson.toJson(moveEvent);
    }

    public Tuple<EntityID, Integer> getOriginEntity() {
        return originEntity;
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

        MoveRequest that = (MoveRequest) o;

        if (!getOriginEntity().equals(that.getOriginEntity())) return false;
        if (!getOriginField().equals(that.getOriginField())) return false;
        return getTargetField().equals(that.getTargetField());
    }

    @Override
    public int hashCode() {
        int result = getOriginEntity().hashCode();
        result = 31 * result + getOriginField().hashCode();
        result = 31 * result + getTargetField().hashCode();
        return result;
    }

}
