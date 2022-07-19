package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * GeneralAssignment LOGIN Event, sent to spectators informing them, what game they have been assigned to.
 */
public class GeneralAssignment extends MMLoginEvent {

    @Expose
    private final String gameID;

    /**
     * Creates a new GeneralAssignment LOGIN Event
     *
     * @param gameID the gameID of the game the client belongs to
     */
    public GeneralAssignment(String gameID) {
        super(EventType.GENERAL_ASSIGNMENT);
        this.gameID = gameID;
    }

    /**
     * Convenience method to get a {@link GeneralAssignment} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link GeneralAssignment}
     * @throws IllegalArgumentException if {@link GeneralAssignment} could not be parsed from the {@link JsonObject}
     */
    public static GeneralAssignment fromJson(JsonObject json) {
        try {
            String gameID = json.get("gameID").getAsString();

            // create and return new request
            return new GeneralAssignment(gameID);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link GeneralAssignment} into a JSON String
     *
     * @return {@link GeneralAssignment} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public String getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralAssignment that = (GeneralAssignment) o;

        return getGameID().equals(that.getGameID());
    }

    @Override
    public int hashCode() {
        return getGameID().hashCode();
    }

}
