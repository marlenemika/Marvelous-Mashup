package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RoundSetup INGAME Event, sent to clients at the start of a new round. <br>
 * Also informs the client about the order of characters.
 */
public class RoundSetupEvent extends MMIngameEvent {

    private final int roundCount;
    private final List<Tuple<EntityID, Integer>> characterOrder;

    /**
     * @param roundCount     counts the rounds
     * @param characterOrder the characterOrder as a List
     */
    public RoundSetupEvent(int roundCount, @NotNull List<Tuple<EntityID, Integer>> characterOrder) {
        super(EventType.ROUND_SETUP);
        this.roundCount = roundCount;
        this.characterOrder = characterOrder;
    }

    /**
     * @param roundCount     counts the rounds
     * @param characterOrder the characterOrder as an array
     */
    public RoundSetupEvent(int roundCount, @NotNull Tuple<EntityID, Integer>[] characterOrder) {
        super(EventType.ROUND_SETUP);
        this.roundCount = roundCount;
        this.characterOrder = Arrays.asList(characterOrder);
    }

    /**
     * Convenience method to get a {@link RoundSetupEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link RoundSetupEvent}
     * @throws IllegalArgumentException if {@link RoundSetupEvent} could not be parsed from the {@link JsonObject}
     */
    public static RoundSetupEvent fromJson(JsonObject json) {
        try {
            // get roundCount
            int roundCount = json.get("roundCount").getAsInt();

            // get list of characters
            List<Tuple<EntityID, Integer>> characterOrder = new ArrayList<>();
            JsonArray characterOrderArray = json.getAsJsonArray("characterOrder");
            // extract every character
            for (int i = 0; i < characterOrderArray.size(); i++) {
                JsonObject character = characterOrderArray.get(i).getAsJsonObject();
                EntityID entityID = EntityID.valueOf(character.get("entityID").getAsString().toUpperCase());
                Tuple<EntityID, Integer> characterTuple = new Tuple<>(entityID, character.get("ID").getAsInt());
                characterOrder.add(characterTuple);
            }
            return new RoundSetupEvent(roundCount, characterOrder);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link RoundSetupEvent} into a JSON String
     *
     * @return {@link RoundSetupEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject roundSetupEvent = new JsonObject();

        // add JSON properties
        roundSetupEvent.addProperty("eventType", "RoundSetupEvent");
        roundSetupEvent.addProperty("roundCount", roundCount);
        JsonArray characterOrder = new JsonArray();
        for (Tuple<EntityID, Integer> tuple : this.characterOrder) {
            characterOrder.add(gson.toJsonTree(tuple));
        }
        roundSetupEvent.add("characterOrder", characterOrder);

        // return JSON as String
        return gson.toJson(roundSetupEvent);
    }

    public int getRoundCount() {
        return roundCount;
    }

    public List<Tuple<EntityID, Integer>> getCharacterOrder() {
        return characterOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoundSetupEvent that = (RoundSetupEvent) o;

        if (getRoundCount() != that.getRoundCount()) return false;
        return getCharacterOrder().equals(that.getCharacterOrder());
    }

    @Override
    public int hashCode() {
        int result = getRoundCount();
        result = 31 * result + getCharacterOrder().hashCode();
        return result;
    }

}
