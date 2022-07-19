package de.uulm.sopra.team08.req;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.Arrays;

/**
 * CharacterSelection LOGIN Request, sent by the player to inform the server, which character they picked.
 */
public class CharacterSelection extends MMLoginRequest {

    /**
     * The chosen characters held in this Request.
     */
    @Expose
    private final boolean[] characters;

    /**
     * Creates a new CharacterSelection LOGIN Request
     *
     * @param characters picked characters
     */
    public CharacterSelection(boolean[] characters) {
        super(requestType.CHARACTER_SELECTION);
        if (characters.length != 12) {
            throw new IllegalArgumentException("Characters Array must be 12 elements long");
        }
        this.characters = characters;
    }

    /**
     * Convenience method to get a {@link CharacterSelection} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link CharacterSelection}
     * @throws IllegalArgumentException if {@link CharacterSelection} could not be parsed from the {@link JsonObject}
     */
    public static CharacterSelection fromJson(JsonObject json) {
        try {
            // get characters
            JsonArray charactersArray = json.getAsJsonArray("characters");
            if (charactersArray.size() != 12) {
                throw new IllegalArgumentException("Characters Array must be 12 elements long");
            }

            // assign characters
            boolean[] characters = new boolean[12];
            for (int i = 0; i < charactersArray.size(); i++) {
                characters[i] = charactersArray.get(i).getAsBoolean();
            }

            // create and return new request
            return new CharacterSelection(characters);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link CharacterSelection} into a JSON String
     *
     * @return {@link CharacterSelection} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

    public boolean[] getCharacters() {
        return characters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterSelection that = (CharacterSelection) o;

        return Arrays.equals(getCharacters(), that.getCharacters());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getCharacters());
    }

}
