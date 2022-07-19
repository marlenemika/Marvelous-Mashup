package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * Turn INGAME Event, sent to inform a client of a new turn starting.
 */
public class TurnEvent extends MMIngameEvent {

    private final int turnCount;
    private final Tuple<EntityID, Integer> nextCharacter;

    /**
     * Creates a new Turn INGAME Event
     *
     * @param turnCount     counts the turns
     * @param nextCharacter the next character
     */
    public TurnEvent(int turnCount, @NotNull Tuple<EntityID, Integer> nextCharacter) {
        super(EventType.TURN);
        this.turnCount = turnCount;
        this.nextCharacter = nextCharacter;
    }

    /**
     * Convenience method to get a {@link TurnEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link TurnEvent}
     * @throws IllegalArgumentException if {@link TurnEvent} could not be parsed from the {@link JsonObject}
     */
    public static TurnEvent fromJson(JsonObject json) {
        try {
            // get roundCount
            int turnCount = json.get("turnCount").getAsInt();


            JsonObject character = json.getAsJsonObject("nextCharacter");
            EntityID entityID = EntityID.valueOf(character.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> characterTuple = new Tuple<>(entityID, character.get("ID").getAsInt());

            return new TurnEvent(turnCount, characterTuple);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link TurnEvent} into a JSON String
     *
     * @return {@link TurnEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject turnEvent = new JsonObject();

        // add JSON properties
        turnEvent.addProperty("eventType", "TurnEvent");
        turnEvent.addProperty("turnCount", turnCount);
        turnEvent.add("nextCharacter", gson.toJsonTree(nextCharacter));

        // return JSON as String
        return gson.toJson(turnEvent);
    }

    public int getTurnCount() {
        return turnCount;
    }

    public Tuple<EntityID, Integer> getNextCharacter() {
        return nextCharacter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TurnEvent turnEvent = (TurnEvent) o;

        if (getTurnCount() != turnEvent.getTurnCount()) return false;
        return getNextCharacter().equals(turnEvent.getNextCharacter());
    }

    @Override
    public int hashCode() {
        int result = getTurnCount();
        result = 31 * result + getNextCharacter().hashCode();
        return result;
    }

}
