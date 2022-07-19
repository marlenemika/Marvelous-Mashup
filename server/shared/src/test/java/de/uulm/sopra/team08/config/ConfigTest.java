package de.uulm.sopra.team08.config;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigTest {

    @Test
    void validateCharacterConfig() {
        // valid
        assertDoesNotThrow(() -> Config.validateCharacterConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterConfig.character.json")));
        // invalid
        assertThrows(ConfigValidationException.class, () -> Config.validateCharacterConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterNotUniqueID.character.json")));
        assertThrows(ConfigValidationException.class, () -> Config.validateCharacterConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterDuplicated.character.json")));
        assertThrows(FileNotFoundException.class, () -> Config.validateCharacterConfig(new File("thiseFileDoesNotExist")));
        assertThrows(ConfigValidationException.class, () -> Config.validateCharacterConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/NotAnJson.json")));
        assertThrows(FileNotFoundException.class, () -> Config.validateCharacterConfig(new File("src/test")));
    }

    @Test
    void validateScenarioConfig() {
        // valid
        assertDoesNotThrow(() -> Config.validateScenarioConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioexample.scenario.json")));
        // invalid
        assertThrows(FileNotFoundException.class, () -> Config.validateScenarioConfig(new File("thiseFileDoesNotExist")));
        assertThrows(ConfigValidationException.class, () -> Config.validateScenarioConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/NotAnJson.json")));
        assertThrows(ConfigValidationException.class, () -> Config.validateScenarioConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioWrongEnum.scenario.json")));
        assertThrows(ConfigValidationException.class, () -> Config.validateScenarioConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioMissingScenario.scenario.json")));
        assertThrows(FileNotFoundException.class, () -> Config.validateScenarioConfig(new File("src/test")));
        assertThrows(ConfigValidationException.class, () -> Config.validateScenarioConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioNotEnoughGRASS.scenario.json")));
    }

    @Test
    void validatePartieConfig() {
        // valid
        assertDoesNotThrow(() -> Config.validatePartieConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieexample.game.json")));
        // invalid
        assertThrows(ConfigValidationException.class, () -> Config.validatePartieConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieMissingElement.game.json")));
        assertThrows(ConfigValidationException.class, () -> Config.validatePartieConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieWrongType.game.json")));
        assertThrows(ConfigValidationException.class, () -> Config.validatePartieConfig(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/NotAnJson.json")));
        assertThrows(FileNotFoundException.class, () -> Config.validatePartieConfig(new File("thiseFileDoesNotExist")));
        assertThrows(FileNotFoundException.class, () -> Config.validatePartieConfig(new File("src/test")));
    }


    @Test
    void config() throws FileNotFoundException, ConfigValidationException {
        final Config config = new Config(new File("src/test/resources/de/uulm/sopra/team08/config/testcases/partieexample.game.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/characterConfig.character.json"),
                new File("src/test/resources/de/uulm/sopra/team08/config/testcases/scenarioexample.scenario.json"));
    }

}