package de.uulm.sopra.team08.server.args;

import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.ConfigValidationException;
import de.uulm.sopra.team08.server.Server;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Map;

import static de.uulm.sopra.team08.util.ArrayUtils.getI;


/**
 * Holds values for all variables configurable by the environment variables and arguments.
 * These include:
 * <ul>
 *     <li>Log level</li>
 *     <li>Match file</li>
 *     <li>Character file</li>
 *     <li>Scenario file</li>
 *     <li>Replay file</li>
 *     <li>if the configurations should be checked</li>
 *     <li>Port</li>
 *     <li>Help (only usable with --help or -h)</li>
 * </ul>
 */
public class Arguments {

    private static final Logger LOGGER = LogManager.getLogger(Arguments.class);
    private static final String HELP_RESOURCE = "/de/uulm/sopra/team08/server/arguments-help.txt";
    private static final String ENV_LOG_LEVEL = "MMU_LOG_LEVEL";
    private static final String ENV_PORT = "MMU_PORT";
    private static final String ENV_MATCH_FILE = "MMU_CONF_MATCH";
    private static final String ENV_CHARS_FILE = "MMU_CONF_CHARS";
    private static final String ENV_SCENARIO_FILE = "MMU_CONF_SCENARIO";
    private static final String ENV_REPLAY_DIR = "MMU_REPLAY";
    private Level logLevel;
    private File matchFile;
    private File charsFile;
    private File scenarioFile;
    private File replayDir;
    private int port;


    /**
     * Creates a new Instance with default values.<br>
     * Note that files from this config are not guaranteed to exist after calling this method.
     */
    public Arguments() {
        logLevel = Level.ALL;
        matchFile = new File("default.game.json");
        charsFile = new File("default.character.json");
        scenarioFile = new File("default.scenario.json");
        replayDir = new File("replays");
        port = 1218;
    }


    /**
     * Validates all Configs (Match-, Character- and Scenario-Config) via their schema and closes the application.
     * This writes the State of the configs to the {@link System#out} Stream.
     */
    private void checkConfigs() {

        // partie/match config
        try {
            Config.validatePartieConfig(matchFile);
            LOGGER.info("The Match-Config file is valid");
        } catch (FileNotFoundException e) {
            LOGGER.error("File error at Match-Config", e);
        } catch (ConfigValidationException e) {
            LOGGER.error("Validation error at Match-Config", e);
        }

        // character config
        try {
            Config.validateCharacterConfig(charsFile);
            LOGGER.info("The Character-Config file is valid");
        } catch (FileNotFoundException e) {
            LOGGER.error("File error at Character-Config", e);
        } catch (ConfigValidationException e) {
            LOGGER.error("Validation error at Character-Config", e);
        }

        // scenario config
        try {
            Config.validateScenarioConfig(scenarioFile);
            LOGGER.info("The Scenario-Config file is valid");
        } catch (FileNotFoundException e) {
            LOGGER.error("File error at Scenario-Config", e);
        } catch (ConfigValidationException e) {
            LOGGER.error("Validation error at Scenario-Config", e);
        }

        System.exit(0);
    }

    /**
     * Prints all available arguments to the {@link System#out} Stream and exits the application.
     */
    private void printHelp() {
        final InputStream help = getClass().getResourceAsStream(HELP_RESOURCE);
        if (help != null) {
            // Print content to System.out for guaranteed visibility
            new BufferedReader(new InputStreamReader(help))
                    .lines()
                    .forEach(System.out::println);
        } else {
            LOGGER.error("Help resource not found at: " + HELP_RESOURCE);
        }
        System.exit(0);
    }

    /**
     * Sets intern values to parsed values from the given arguments.
     *
     * @param args The arguments passed to {@link Server#main(String[])}.
     */
    public void setFromArgs(String[] args) {
        boolean verbose = false;
        boolean checkConfig = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                // --log-level n / -l n
                case "--log-level":
                case "-l":
                    setLogLevel(getI(args, ++i));
                    break;
                // --verbose / -v
                case "--verbose":
                case "-v":
                    verbose = true;
                    break;
                // --port n / -p n
                case "--port":
                case "-p":
                    setPort(getI(args, ++i));
                    break;
                // --conf-match file / -m file
                case "--conf-match":
                case "-m":
                    setMatchFile(getI(args, ++i));
                    break;
                // --conf-chars file / -c file
                case "--conf-chars":
                case "-c":
                    setCharsFile(getI(args, ++i));
                    break;
                // --conf-scenario file / -s file
                case "--conf-scenario":
                case "-s":
                    setScenarioFile(getI(args, ++i));
                    break;
                // --replay dir / -r dir
                case "--replay":
                case "-r":
                    setReplayDir(getI(args, ++i));
                    break;

                // --team08-xY for custom args

                // --check-conf / -C
                case "--check-conf":
                case "-C":
                    checkConfig = true;
                    break;
                // --help / -h
                case "--help":
                case "-h":
                    printHelp();
                    break;
                default:
                    // ignore unknown args
            }
        }

        // override log-level with verbose
        if (verbose) logLevel = Level.ALL;

        if (checkConfig) checkConfigs();
    }

    /**
     * @return the set log-level.
     */
    public Level getLogLevel() {
        return logLevel;
    }

    /**
     * Sets the {@link #logLevel} by converting a numeral representation in a {@link Level}, so that:
     * <ul>
     *     <li>0 = {@link Level#OFF}</li>
     *     <li>1 = {@link Level#FATAL}</li>
     *     <li>2 = {@link Level#ERROR}</li>
     *     <li>3 = {@link Level#INFO}</li>
     *     <li>4 = {@link Level#DEBUG}</li>
     *     <li>5 = {@link Level#TRACE}</li>
     *     <li>any other number = {@link Level#ALL}</li>
     *     <li>{@code null} = the currently set value</li>
     * </ul>
     *
     * @param s The parameter of the --log-level argument or {@value ENV_LOG_LEVEL} value. Must be a numeral
     *          representation of the desired {@link Level}. Can be {@code null}.
     * @throws IllegalArgumentException If the given String is no number.
     */
    private void setLogLevel(@Nullable String s) {
        if (s == null) return;

        try {
            setLogLevel(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter for 'log-level': " + s);
        }
    }

    /**
     * Maps the given integer to a {@link Level}.
     * <ul>
     *     <li>0 = {@link Level#OFF}</li>
     *     <li>1 = {@link Level#FATAL}</li>
     *     <li>2 = {@link Level#ERROR}</li>
     *     <li>3 = {@link Level#INFO}</li>
     *     <li>4 = {@link Level#DEBUG}</li>
     *     <li>5 = {@link Level#TRACE}</li>
     *     <li>any other number = {@link Level#ALL}</li>
     *     <li>{@code null} = the currently set value</li>
     * </ul>
     *
     * @param i The new log level.
     */
    private void setLogLevel(int i) {
        switch (i) {
            case 0:
                logLevel = Level.OFF;
                break;
            case 1:
                logLevel = Level.FATAL;
                break;
            case 2:
                logLevel = Level.ERROR;
                break;
            case 3:
                logLevel = Level.INFO;
                break;
            case 4:
                logLevel = Level.DEBUG;
                break;
            case 5:
                logLevel = Level.TRACE;
                break;
            default:
                logLevel = Level.ALL;
                break;
        }

        Configurator.setRootLevel(logLevel);
    }

    /**
     * @return the set match-file.
     * @see #setMatchFile(String)
     */
    public File getMatchFile() {
        return matchFile;
    }

    /**
     * Sets the {@link #matchFile} by parsing a path to a file.
     * The given path to the file must result in an existing file
     * (no directory) with the name ending with {@code .game.json}.
     *
     * @param s The parameter of the --conf-match or {@value ENV_MATCH_FILE} value.
     * @throws IllegalArgumentException If the file does not exist.
     * @throws IllegalArgumentException If the file is a directory.
     * @throws IllegalArgumentException If the file's name does not end with {@code .game.json}.
     */
    private void setMatchFile(@Nullable String s) {
        if (s == null) return;

        final File file = new File(s);
        if (!file.exists())
            throw new IllegalArgumentException("Invalid parameter for '--conf-match' (File does not exist): " + s);
        if (file.isDirectory())
            throw new IllegalArgumentException("Invalid parameter for '--conf-match' (File is a directory): " + s);
        if (!file.getName().endsWith(".game.json"))
            throw new IllegalArgumentException("Invalid parameter for '--conf-match' (File does not end with .game.json): " + s);

        matchFile = file;
    }

    /**
     * @return the set chars-file.
     * @see #setCharsFile(String)
     */
    public File getCharsFile() {
        return charsFile;
    }

    /**
     * Sets the {@link #charsFile} by parsing a path to a file.
     * The given path to the file must result in an existing file
     * (no directory) with the name ending with {@code .character.json}.
     *
     * @param s The parameter of the --conf-chars or {@value ENV_CHARS_FILE} value.
     * @throws IllegalArgumentException If the file does not exist.
     * @throws IllegalArgumentException If the file is a directory.
     * @throws IllegalArgumentException If the file's name does not end with {@code .character.json}.
     */
    private void setCharsFile(@Nullable String s) {
        if (s == null) return;

        final File file = new File(s);
        if (!file.exists())
            throw new IllegalArgumentException("Invalid parameter for '--conf-chars' (File does not exist): " + s);
        if (file.isDirectory())
            throw new IllegalArgumentException("Invalid parameter for '--conf-chars' (File is a directory): " + s);
        if (!file.getName().endsWith(".character.json"))
            throw new IllegalArgumentException("Invalid parameter for '--conf-chars' (File does not end with .character.json): " + s);

        charsFile = file;
    }

    /**
     * @return the set chars-file.
     * @see #setScenarioFile(String)
     */
    public File getScenarioFile() {
        return scenarioFile;
    }

    /**
     * Sets the {@link #scenarioFile} by parsing a path to a file.
     * The given path to the file must result in an existing file
     * (no directory) with the name ending with {@code .scenario.json}.
     *
     * @param s The parameter of the --conf-scenario or {@value ENV_SCENARIO_FILE} value.
     * @throws IllegalArgumentException If the file does not exist.
     * @throws IllegalArgumentException If the file is a directory.
     * @throws IllegalArgumentException If the file's name does not end with {@code .scenario.json}.
     */
    private void setScenarioFile(@Nullable String s) {
        if (s == null) return;

        final File file = new File(s);
        if (!file.exists())
            throw new IllegalArgumentException("Invalid parameter for '--conf-scenario' (File does not exist): " + s);
        if (file.isDirectory())
            throw new IllegalArgumentException("Invalid parameter for '--conf-scenario' (File is a directory): " + s);
        if (!file.getName().endsWith(".scenario.json"))
            throw new IllegalArgumentException("Invalid parameter for '--conf-scenario' (File does not end with .scenario.json): " + s);

        scenarioFile = file;
    }

    /**
     * @return the set chars-file.
     * @see #setReplayDir(String)
     */
    public File getReplayDir() {
        return replayDir;
    }

    /**
     * Sets the {@link #replayDir} by parsing a path to a directory.
     * If the given path does not exist, the directory will be created recursively.
     * The given path must result in an existing file
     * (no directory) with the name ending with {@code .scenario.json}.
     *
     * @param s The parameter of the --conf-scenario or {@value ENV_SCENARIO_FILE} value.
     * @throws RuntimeException         If the path could not be created recursively.
     * @throws IllegalArgumentException If the path does not resolve in a directory.
     */
    private void setReplayDir(@Nullable String s) {
        if (s == null) return;

        final File dir = new File(s);
        if (!dir.isDirectory())
            throw new IllegalArgumentException("Invalid parameter for '--replay' (File is no directory): " + s);

        replayDir = dir;
    }

    /**
     * @return the set port.
     * @see #setPort(String)
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the {@link #port} by parsing the given String to an int.
     *
     * @param s The parameter of the --port or {@value ENV_PORT} value.
     * @throws IllegalArgumentException If the given value is out of port range (0-65535).
     * @throws IllegalArgumentException If the given value could not be parsed to an int.
     * @see #setPort(int)
     */
    private void setPort(@Nullable String s) {
        if (s == null) return;

        try {
            setPort(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter for 'port': " + s);
        }
    }

    /**
     * @param i The new port.
     * @throws IllegalArgumentException If the given value is out of port range (0-65535).
     */
    private void setPort(int i) {
        if (i < 0 || i > 65535)
            throw new IllegalArgumentException(
                    String.format("Invalid parameter for 'port': %d (Must be 0 <= port <= 65535)", i));
        port = i;
    }

    /**
     * Sets intern values to parsed values from the environment.<br>
     * Uses: {@link #ENV_LOG_LEVEL}, {@link #ENV_MATCH_FILE}, {@link #ENV_CHARS_FILE},
     * {@link #ENV_SCENARIO_FILE}, {@link #ENV_REPLAY_DIR} and {@link #ENV_PORT}
     */
    public void setFromEnv(Map<String, String> env) {
        setLogLevel(env.get(ENV_LOG_LEVEL));
        setPort(env.get(ENV_PORT));
        setMatchFile(env.get(ENV_MATCH_FILE));
        setCharsFile(env.get(ENV_CHARS_FILE));
        setScenarioFile(env.get(ENV_SCENARIO_FILE));
        setReplayDir(env.get(ENV_REPLAY_DIR));
    }

}
