package de.uulm.sopra.team08.config;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.uulm.sopra.team08.config.character.Character;
import de.uulm.sopra.team08.config.character.CharacterConfig;
import de.uulm.sopra.team08.config.partie.PartieConfig;
import de.uulm.sopra.team08.config.scenario.ScenarioConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * A Collection of game specific Settings.
 * <p>
 * Including:
 * <ul>
 *     <li>{@link CharacterConfig}</li>
 *     <li>{@link ScenarioConfig}</li>
 *     <li>{@link PartieConfig}</li>
 * </ul>
 * <p>
 * Also contains some validation methods
 */
public class Config {

    private static final Logger LOGGER = LogManager.getLogger(Config.class);

    // Annotations to match the Replay-File
    @SerializedName("character")
    @Expose
    private final CharacterConfig characterConfig;

    @SerializedName("match")
    @Expose
    private final PartieConfig partieConfig;

    @SerializedName("scenario")
    @Expose
    private final ScenarioConfig scenarioConfig;


    /**
     * Loads, validate and parses the given json
     *
     * @param partieConfigFile    the partieConfig json file
     * @param characterConfigFile the characterConfig json file
     * @param scenarioConfigFile  the scenarioConfig json file
     * @throws FileNotFoundException     if a file was not found
     * @throws ConfigValidationException if a json isn't valid
     */
    public Config(@NotNull File partieConfigFile, @NotNull File characterConfigFile, @NotNull File scenarioConfigFile) throws FileNotFoundException, ConfigValidationException {
        final Gson gson = new Gson();

        // region partie config
        validatePartieConfig(partieConfigFile);
        LOGGER.log(Level.DEBUG, "partieConfig was valid");
        this.partieConfig = gson.fromJson(new FileReader(partieConfigFile), PartieConfig.class);
        LOGGER.log(Level.DEBUG, "partieConfig was parsed");
        // endregion

        // region character config
        validateCharacterConfig(characterConfigFile);
        LOGGER.log(Level.DEBUG, "characterConfig was valid");


        final HashSet<Character> characterSet = new HashSet<>();
        JSONObject jsonCharacterConfig = new JSONObject(
                new JSONTokener(new FileInputStream(characterConfigFile)));


        final JSONArray characters = jsonCharacterConfig.getJSONArray("characters");
        characters.forEach(element ->
                characterSet.add(gson.fromJson(element.toString(), Character.class))
        );
        this.characterConfig = new CharacterConfig(characterSet);

        LOGGER.log(Level.DEBUG, "characterConfig was parsed");
        // endregion

        // region scenario config
        validateScenarioConfig(scenarioConfigFile);
        LOGGER.log(Level.DEBUG, "scenarioConfig was valid");

        JSONObject jsonScenarioConfig = new JSONObject(
                new JSONTokener(new FileInputStream(scenarioConfigFile)));
        final JSONArray jsonScenarioConfigJSONArray = jsonScenarioConfig.getJSONArray("scenario");

        // scenario array
        List<List<ScenarioConfig.Scenario>> scenarioList = new ArrayList<>();
        for (var outer : jsonScenarioConfigJSONArray) {
            List<ScenarioConfig.Scenario> innerList = new ArrayList<>();
            for (var inner : ((JSONArray) outer)) {
                innerList.add(ScenarioConfig.Scenario.valueOf(inner.toString()));
            }
            scenarioList.add(innerList);
        }
        this.scenarioConfig = new ScenarioConfig(scenarioList, jsonScenarioConfig.getString("name"), jsonScenarioConfig.getString("author"));

        LOGGER.log(Level.DEBUG, "scenarioConfig was parsed");
        // endregion
    }

    /**
     * checks weather the config ist valid, if not throws an exception
     *
     * @param partieConfigFile the json File
     * @throws FileNotFoundException     if the file was not found
     * @throws ConfigValidationException if the json isn't valid
     */
    public static void validatePartieConfig(@NotNull File partieConfigFile) throws FileNotFoundException, ConfigValidationException {
        try {
            // schema validation
            JSONObject jsonSchema = new JSONObject(
                    new JSONTokener(Objects.requireNonNull(Config.class.getResourceAsStream("/de/uulm/sopra/team08/schema/partiekonfig.schema"))));
            JSONObject jsonSubject = new JSONObject(
                    new JSONTokener(new FileInputStream(partieConfigFile)));

            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(jsonSubject);
        } catch (ValidationException | JSONException e) {
            LOGGER.log(Level.ERROR, "validateScenarioConfig error", e);
            throw new ConfigValidationException(e.getMessage());
        }
    }

    /**
     * checks weather the config ist valid, if not throws an exception
     *
     * @param characterConfigFile the json file
     * @throws FileNotFoundException     if the file was not found
     * @throws ConfigValidationException if the json isn't valid
     */
    public static void validateCharacterConfig(@NotNull File characterConfigFile) throws FileNotFoundException, ConfigValidationException {
        try {
            // schema validation
            JSONObject jsonSchema = new JSONObject(
                    new JSONTokener(Objects.requireNonNull(Config.class.getResourceAsStream("/de/uulm/sopra/team08/schema/characterConfig.schema"))));
            JSONObject jsonSubject = new JSONObject(
                    new JSONTokener(new FileInputStream(characterConfigFile)));

            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(jsonSubject);


            // check for duplicate id's
            final HashSet<Integer> ids = new HashSet<>();
            final JSONArray characters = jsonSubject.getJSONArray("characters");
            for (var element : characters) {
                if (!ids.add(((JSONObject) element).getInt("characterID")))
                    throw new ValidationException(schema, "Duplicated ID's in character config");
            }
        } catch (ValidationException | JSONException e) {
            LOGGER.log(Level.ERROR, "validateCharacterConfig error", e);
            throw new ConfigValidationException(e.getMessage());
        }

    }

    /**
     * checks weather the config ist valid, if not throws an exception
     *
     * @param scenarioConfigFile the json file
     * @throws FileNotFoundException     if the file was not found
     * @throws ConfigValidationException if the json isn't valid
     */
    public static void validateScenarioConfig(@NotNull File scenarioConfigFile) throws FileNotFoundException, ConfigValidationException {
        try {
            // schema validation
            JSONObject jsonSchema = new JSONObject(
                    new JSONTokener(Objects.requireNonNull(Config.class.getResourceAsStream("/de/uulm/sopra/team08/schema/scenarioconfig.schema"))));
            JSONObject jsonSubject = new JSONObject(
                    new JSONTokener(new FileInputStream(scenarioConfigFile)));

            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(jsonSubject);

            // check for 20 GRASS
            int counter = 0;
            final JSONArray scenario = jsonSubject.getJSONArray("scenario");
            for (Object row : scenario) {
                // needs to be an JSONArray otherwise the schema would throw an exception
                for (Object elem : (JSONArray) row) {
                    if (ScenarioConfig.Scenario.valueOf(elem.toString()).equals(ScenarioConfig.Scenario.GRASS)) {
                        counter++;
                    }
                }
            }
            if (counter < 20) throw new ConfigValidationException("Scenario has less then 20 GRASS");

        } catch (ValidationException | JSONException | ConfigValidationException e) {
            LOGGER.log(Level.ERROR, "validateScenarioConfig error", e);
            throw new ConfigValidationException(e.getMessage());
        }

    }


    public PartieConfig getPartieConfig() {
        return partieConfig;
    }

    public CharacterConfig getCharacterConfig() {
        return characterConfig;
    }

    public ScenarioConfig getScenarioConfig() {
        return scenarioConfig;
    }

}
