package de.uulm.sopra.team08.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import de.uulm.sopra.team08.config.character.Character;
import de.uulm.sopra.team08.config.partie.PartieConfig;
import de.uulm.sopra.team08.config.scenario.ScenarioConfig;

import java.util.Arrays;
import java.util.Objects;

/**
 * GameStructure LOGIN Event, sent to all clients, as soon as both clients have chosen their characters.<br>
 * Also used as entry point for a potential reconnect.
 */
public class GameStructure extends MMLoginEvent {

    @Expose
    private final String assignment;
    @Expose
    private final String playerOneName;
    @Expose
    private final String playerTwoName;
    @Expose
    private final JsonObject[] playerOneCharacters;
    @Expose
    private final JsonObject[] playerTwoCharacters;
    @Expose
    private final PartieConfig matchconfig;
    @Expose
    private final ScenarioConfig scenarioconfig;

    /**
     * Creates a new GameStructure LOGIN Event
     * Format of character Strings: "name: ,HP: ,MP: ,AP: ,meleeDamage: ,rangeCombatDamage: ,rangeCombatReach: "
     *
     * @param assignment          what client this Event is given to. Must be one of "PlayerOne", "PlayerTwo" or "Spectator"
     * @param playerOneName       name of player 1
     * @param playerTwoName       name of player 2
     * @param playerOneCharacters selected characters from player 1
     * @param playerTwoCharacters selected characters from player 2
     * @param matchconfig         match config of this game
     * @param scenarioconfig      scenario config of this game
     */
    public GameStructure(String assignment, String playerOneName, String playerTwoName, Object[] playerOneCharacters, Object[] playerTwoCharacters, PartieConfig matchconfig, ScenarioConfig scenarioconfig) {
        super(EventType.GAME_STRUCTURE);
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.matchconfig = matchconfig;
        this.scenarioconfig = scenarioconfig;
        if (!Arrays.asList("PlayerOne", "PlayerTwo", "Spectator").contains(assignment)) {
            throw new IllegalArgumentException("assignment must be \"PlayerOne\", \"PlayerTwo\" or \"Spectator\"");
        }
        this.assignment = assignment;
        if (playerOneCharacters.length != 6 || playerTwoCharacters.length != 6) {
            throw new IllegalArgumentException("Characters Array must be 6 elements long");
        }
        JsonObject[] p1Characters = new JsonObject[6];
        JsonObject[] p2Characters = new JsonObject[6];
        // add player1 characters
        for (int i = 0; i < playerOneCharacters.length; i++) {
            Object o = playerOneCharacters[i];
            System.out.println(o.getClass());
            if (!(o instanceof Character) && !(o instanceof de.uulm.sopra.team08.data.entity.Character)) {
                throw new IllegalArgumentException("characterSelection may only be Characters!");
            } else if((o instanceof Character)){
                Character c = (Character) o;
                p1Characters[i] = gson.toJsonTree(c, Character.class).getAsJsonObject();
            } else {
                de.uulm.sopra.team08.data.entity.Character dataCharacter = (de.uulm.sopra.team08.data.entity.Character) o;
                Character c = new Character(dataCharacter);
                p1Characters[i] = gson.toJsonTree(c, Character.class).getAsJsonObject();
            }
        }
        // add player2 characters
        for (int i = 0; i < playerTwoCharacters.length; i++) {
            Object o = playerTwoCharacters[i];
            if (!(o instanceof Character) && !(o instanceof de.uulm.sopra.team08.data.entity.Character)) {
                throw new IllegalArgumentException("characterSelection may only be Characters!");
            } else if(o instanceof Character){
                Character c = (Character) o;
                p2Characters[i] = gson.toJsonTree(c, Character.class).getAsJsonObject();
            } else {
                de.uulm.sopra.team08.data.entity.Character dataCharacter = (de.uulm.sopra.team08.data.entity.Character) o;
                Character c = new Character(dataCharacter);
                p2Characters[i] = gson.toJsonTree(c, Character.class).getAsJsonObject();
            }
        }
        this.playerOneCharacters = p1Characters;
        this.playerTwoCharacters = p2Characters;
    }

    /**
     * Convenience method to get a {@link GameStructure} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link GameStructure}
     * @throws IllegalArgumentException if {@link GameStructure} could not be parsed from the {@link JsonObject}
     */
    public static GameStructure fromJson(JsonObject json) {
        return new Gson().fromJson(json, GameStructure.class);
    }

    /**
     * Convenience method for transforming a {@link GameStructure} into a JSON String
     *
     * @return {@link GameStructure} as JSON String
     */
    @Override
    public String toJsonEvent() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this, GameStructure.class);
    }

    public JsonObject[] getPlayerOneCharacters() {
        return playerOneCharacters;
    }

    public JsonObject[] getPlayerTwoCharacters() {
        return playerTwoCharacters;
    }

    public String getAssignment() {
        return assignment;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public PartieConfig getMatchconfig() {
        return matchconfig;
    }

    public ScenarioConfig getScenarioconfig() {
        return scenarioconfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameStructure that = (GameStructure) o;
        return getAssignment().equals(that.getAssignment()) && getPlayerOneName().equals(that.getPlayerOneName()) && getPlayerTwoName().equals(that.getPlayerTwoName()) && Arrays.equals(getPlayerOneCharacters(), that.getPlayerOneCharacters()) && Arrays.equals(getPlayerTwoCharacters(), that.getPlayerTwoCharacters()) && getMatchconfig().equals(that.getMatchconfig()) && getScenarioconfig().equals(that.getScenarioconfig());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), getAssignment(), getPlayerOneName(), getPlayerTwoName(), getMatchconfig(), getScenarioconfig());
        result = 31 * result + Arrays.hashCode(getPlayerOneCharacters());
        result = 31 * result + Arrays.hashCode(getPlayerTwoCharacters());
        return result;
    }

}
