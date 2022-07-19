package de.uulm.sopra.team08.server;

import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.server.args.Arguments;
import de.uulm.sopra.team08.server.data.GameLogic;
import de.uulm.sopra.team08.server.data.Replay;
import de.uulm.sopra.team08.server.net.NetworkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    private static final Marker STARTUP_MARKER = MarkerManager.getMarker("Startup");


    public static void main(String[] args) {
        LOGGER.info("Starting Server...");

        try {
            // Arguments
            final Arguments a = new Arguments();
            a.setFromEnv(System.getenv());
            a.setFromArgs(args);
            LOGGER.debug(STARTUP_MARKER, "Arguments done!");

            // Replay
            Replay.getInstance().setLocation(a.getReplayDir());
            LOGGER.debug(STARTUP_MARKER, "Replay done!");

            // Config
            final Config config = new Config(a.getMatchFile(), a.getCharsFile(), a.getScenarioFile());
            LOGGER.debug(STARTUP_MARKER, "Config done!");

            // GameLogic
            final GameLogic logic = new GameLogic(config);
            LOGGER.trace(STARTUP_MARKER, "Logic done!");

            // Network
            NetworkManager.init(a.getPort(), logic, config.getPartieConfig().getMaxResponseTime() * 1000L);
            LOGGER.trace(STARTUP_MARKER, "Network done!");

            LOGGER.info(STARTUP_MARKER, "Server ready!");

        } catch (Exception e) {
            LOGGER.fatal("Exception in while starting up!", e);
            System.exit(1);
        }
    }

    /**
     * Shuts down the server, exiting with the given status code.
     *
     * @param message The reason, why the server stopps.
     * @param status  The status code.
     */
    public static void shutdown(String message, int status) {
        LOGGER.info(message.length() > 0 ? "Shutting down! Reason: " + message : "Shutting down!");

        // network
        if (NetworkManager.isInitialized())
            NetworkManager.getInstance().shutdown();
        // replay
        if (status == 0)
            if (!Replay.getInstance().saveFile()) LOGGER.error("Replay could not be saved");

        System.exit(status);

    }

}
