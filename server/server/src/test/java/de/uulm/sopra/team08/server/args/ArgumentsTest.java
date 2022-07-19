package de.uulm.sopra.team08.server.args;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ArgumentsTest {

    private static final String ENV_LOG_LEVEL = "MMU_LOG_LEVEL";
    private static final String ENV_PORT = "MMU_PORT";
    private static final String ENV_MATCH_FILE = "MMU_CONF_MATCH";
    private static final String ENV_CHARS_FILE = "MMU_CONF_CHARS";
    private static final String ENV_SCENARIO_FILE = "MMU_CONF_SCENARIO";
    private static final String ENV_REPLAY_DIR = "MMU_REPLAY";

    @Test
    void setFromArgs() {
        Arguments arguments = new Arguments();

        // LogLevel
        assertEquals(Level.ALL, arguments.getLogLevel());
        arguments.setFromArgs(new String[]{"-l", "0"});
        assertEquals(Level.OFF, arguments.getLogLevel());
        arguments.setFromArgs(new String[]{"-l", "1"});
        assertEquals(Level.FATAL, arguments.getLogLevel());
        arguments.setFromArgs(new String[]{"-l", "2"});
        assertEquals(Level.ERROR, arguments.getLogLevel());
        arguments.setFromArgs(new String[]{"-l", "3"});
        assertEquals(Level.INFO, arguments.getLogLevel());
        arguments.setFromArgs(new String[]{"-l", "4"});
        assertEquals(Level.DEBUG, arguments.getLogLevel());
        arguments.setFromArgs(new String[]{"-l", "5"});
        assertEquals(Level.TRACE, arguments.getLogLevel());
        // multiple -l = last one
        arguments.setFromArgs(new String[]{"-l", "5", "-l", "0"});
        assertEquals(Level.OFF, arguments.getLogLevel());

        // Invalid = default
        arguments.setFromArgs(new String[]{"-l", "6"});
        assertEquals(Level.ALL, arguments.getLogLevel());

        arguments.setFromArgs(new String[]{"-v"});
        assertEquals(Level.ALL, arguments.getLogLevel());

        // port
        arguments = new Arguments();
        // default = 1218 as specified
        assertEquals(1218, arguments.getPort());
        arguments.setFromArgs(new String[]{"-p", "123"});
        assertEquals(123, arguments.getPort());
        // invalid
        Arguments finalArguments = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments.setFromArgs(new String[]{"-p", "-2"}));
        assertThrows(IllegalArgumentException.class, () -> finalArguments.setFromArgs(new String[]{"-p", "65536"}));
        assertEquals(123, arguments.getPort());

        // Replay
        arguments = new Arguments();
        assertEquals(new File("replays"), arguments.getReplayDir());
        arguments.setFromArgs(new String[]{"-r", "."});
        assertEquals(new File("."), arguments.getReplayDir());

        // invalid
        Arguments finalArguments1 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments1.setFromArgs(new String[]{"-r", "thisDirDoesNotExist"}));

        // config
        arguments = new Arguments();
        assertEquals(new File("default.character.json"), arguments.getCharsFile());
        assertEquals(new File("default.game.json"), arguments.getMatchFile());
        assertEquals(new File("default.scenario.json"), arguments.getScenarioFile());
        arguments.setFromArgs(new String[]{
                "-m", "src/test/resources/de/uulm/sopra/team08/server/config/partieexample.game.json",
                "-c", "src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.character.json",
                "-s", "src/test/resources/de/uulm/sopra/team08/server/config/scenarioexample.scenario.json"}
        );

        assertEquals(new File("src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.character.json"), arguments.getCharsFile());
        assertEquals(new File("src/test/resources/de/uulm/sopra/team08/server/config/partieexample.game.json"), arguments.getMatchFile());
        assertEquals(new File("src/test/resources/de/uulm/sopra/team08/server/config/scenarioexample.scenario.json"), arguments.getScenarioFile());

        // invalid
        Arguments finalArguments2 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments2.setFromArgs(new String[]{"-m", "src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.test.json"}));
        assertThrows(IllegalArgumentException.class, () -> finalArguments2.setFromArgs(new String[]{"-m", "src/test/resources/de/uulm/sopra/team08/server/config"}));
        assertThrows(IllegalArgumentException.class, () -> finalArguments2.setFromArgs(new String[]{"-m", "src/test/resources/de/uulm/sopra/team08/server/config/DoesNotExist"}));

    }

    @Test
    void setFromEnv() {
        Arguments arguments = new Arguments();


        // LogLevel
        assertEquals(Level.ALL, arguments.getLogLevel());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "0");
        }});
        assertEquals(Level.OFF, arguments.getLogLevel());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "1");
        }});
        assertEquals(Level.FATAL, arguments.getLogLevel());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "2");
        }});
        assertEquals(Level.ERROR, arguments.getLogLevel());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "3");
        }});
        assertEquals(Level.INFO, arguments.getLogLevel());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "4");
        }});
        assertEquals(Level.DEBUG, arguments.getLogLevel());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "5");
        }});
        assertEquals(Level.TRACE, arguments.getLogLevel());
        // multiple -l = last one
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "5");
            this.put(ENV_LOG_LEVEL, "0");
        }});
        assertEquals(Level.OFF, arguments.getLogLevel());

        // Invalid = default
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_LOG_LEVEL, "6");
        }});
        assertEquals(Level.ALL, arguments.getLogLevel());


        // port
        arguments = new Arguments();
        // default = 1218 as specified
        assertEquals(1218, arguments.getPort());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_PORT, "123");
        }});
        assertEquals(123, arguments.getPort());
        // invalid

        Arguments finalArguments = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_PORT, "-2");
        }}));
        Arguments finalArguments3 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments3.setFromEnv(new HashMap<>() {{
            this.put(ENV_PORT, "65536");
        }}));
        assertEquals(123, arguments.getPort());

        // Replay
        arguments = new Arguments();
        assertEquals(new File("replays"), arguments.getReplayDir());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_REPLAY_DIR, ".");
        }});
        assertEquals(new File("."), arguments.getReplayDir());

        // invalid
        Arguments finalArguments1 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments1.setFromEnv(new HashMap<>() {{
            this.put(ENV_REPLAY_DIR, "thisDirDoesNotExist");
        }}));

        // config
        arguments = new Arguments();
        assertEquals(new File("default.character.json"), arguments.getCharsFile());
        assertEquals(new File("default.game.json"), arguments.getMatchFile());
        assertEquals(new File("default.scenario.json"), arguments.getScenarioFile());
        arguments.setFromEnv(new HashMap<>() {{
            this.put(ENV_CHARS_FILE, "src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.character.json");
            this.put(ENV_MATCH_FILE, "src/test/resources/de/uulm/sopra/team08/server/config/partieexample.game.json");
            this.put(ENV_SCENARIO_FILE, "src/test/resources/de/uulm/sopra/team08/server/config/scenarioexample.scenario.json");
        }});

        assertEquals(new File("src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.character.json"), arguments.getCharsFile());
        assertEquals(new File("src/test/resources/de/uulm/sopra/team08/server/config/partieexample.game.json"), arguments.getMatchFile());
        assertEquals(new File("src/test/resources/de/uulm/sopra/team08/server/config/scenarioexample.scenario.json"), arguments.getScenarioFile());

        // invalid
        Arguments finalArguments4 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments4.setFromEnv(new HashMap<>() {{
            this.put(ENV_MATCH_FILE, "src/test/resources/de/uulm/sopra/team08/server/config/characterConfig.test.json");
        }}));
        Arguments finalArguments5 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments5.setFromEnv(new HashMap<>() {{
            this.put(ENV_MATCH_FILE, "src/test/resources/de/uulm/sopra/team08/server/config");
        }}));
        Arguments finalArguments6 = arguments;
        assertThrows(IllegalArgumentException.class, () -> finalArguments6.setFromEnv(new HashMap<>() {{
            this.put(ENV_MATCH_FILE, "src/test/resources/de/uulm/sopra/team08/server/config/DoesNotExist");
        }}));

    }

}