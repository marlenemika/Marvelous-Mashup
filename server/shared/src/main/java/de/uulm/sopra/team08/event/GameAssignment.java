package de.uulm.sopra.team08.event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import de.uulm.sopra.team08.config.character.Character;

import java.util.Arrays;

/**
 * GameAssignment LOGIN Event, sent when a client is assigned to a game. <br>
 * Also contains the 12 characters the player may choose from.
 */
public class GameAssignment extends MMLoginEvent {

    @Expose
    private final String gameID;
    @Expose
    private final JsonObject[] characterSelection;

    /**
     * Creates a new GameAssignment LOGIN Event
     *
     * @param gameID             the gameID of the game the client belongs to
     * @param characterSelection the characters a client can choose from. Must be of type {@link Character}. Object[] chosen for utility
     */
    public GameAssignment(String gameID, Object[] characterSelection) {
        super(EventType.GAME_ASSIGNMENT);
        if (characterSelection.length != 12) {
            throw new IllegalArgumentException("Characters Array must be 12 elements long");
        }
        this.gameID = gameID;
        JsonObject[] characters = new JsonObject[12];
        for (int i = 0; i < characterSelection.length; i++) {
            Object o = characterSelection[i];
            if (!(o instanceof Character)) {
                throw new IllegalArgumentException("characterSelection may only be Characters!");
            } else {
                Character c = (Character) o;
                characters[i] = gson.toJsonTree(c, Character.class).getAsJsonObject();
            }
        }
        this.characterSelection = characters;
    }

    /**
     * Convenience method to get a {@link GameAssignment} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link GameAssignment}
     * @throws IllegalArgumentException if {@link GameAssignment} could not be parsed from the {@link JsonObject}
     */
    public static GameAssignment fromJson(JsonObject json) {
        try {
            // extract attributes
            String gameID = json.get("gameID").getAsString();
            JsonArray characterSelectionArray = json.getAsJsonArray("characterSelection");
            if (characterSelectionArray.size() != 12) {
                throw new IllegalArgumentException("Characters Array must be 12 elements long");
            }

            // assign character array
            Gson gson = new Gson();
            Character[] characterSelection = new Character[12];
            for (int i = 0; i < characterSelectionArray.size(); i++) {
                characterSelection[i] = gson.fromJson(characterSelectionArray.get(i), Character.class);
            }

            // create and return new request
            return new GameAssignment(gameID, characterSelection);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link GameAssignment} into a JSON String
     *
     * @return {@link GameAssignment} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public Object[] getCharacterSelection() {
        return characterSelection;
    }

    public String getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameAssignment that = (GameAssignment) o;

        if (!getGameID().equals(that.getGameID())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getCharacterSelection(), that.getCharacterSelection());
    }

    @Override
    public int hashCode() {
        int result = getGameID().hashCode();
        result = 31 * result + Arrays.hashCode(getCharacterSelection());
        return result;
    }

}
