package de.uulm.sopra.team08.server.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.event.MMEvent;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A small class containing the players, the config and all events of the game.
 * Accessed via Singleton-Pattern
 */
public class Replay {

    private static @Nullable Replay instance;
    private final List<MMEvent> events;
    @Expose
    private String nameP1;
    @Expose
    private String nameP2;
    @Expose
    private Config config;
    private File location;

    private Replay() {
        events = new ArrayList<>();
    }

    /**
     * Returns the current replay instance if it has already been initialized, if not it will be initialized.
     *
     * @return the current replay instance
     */
    public static synchronized Replay getInstance() {
        if (instance == null) {
            instance = new Replay();
        }
        return instance;
    }

    /**
     * @return the json string of this object
     */
    private String toJson() {
        final JsonObject jsonObject = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJsonTree(this, Replay.class).getAsJsonObject();
        final JsonArray jsonElements = new JsonArray();
        final Gson gson = new Gson();
        for (MMEvent e : events) {
            jsonElements.add(gson.fromJson(e.toJsonEvent(), JsonObject.class));
        }
        jsonObject.add("events", jsonElements);
        return jsonObject.toString();
    }

    /**
     * clears the events list
     */
    void resetEvents() {
        events.clear();
    }

    public void setNameP1(String nameP1) {
        this.nameP1 = nameP1;
    }

    public void setNameP2(String nameP2) {
        this.nameP2 = nameP2;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Append the specified event to the replay
     *
     * @param event event to be appended to the list
     */
    public synchronized void addEvent(MMEvent event) {
        events.add(event);
    }

    public void setLocation(File location) {
        this.location = location;
    }

    /**
     * Saves the file to the in {@link Replay#location} specified directory
     *
     * @return true if it was possible to save the file
     */
    public boolean saveFile() {
        final File file = new File(location, nameP1 + "_vs_" + nameP2 + "_replay.json");
        try {
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(toJson().getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.flush();
        } catch (IOException e) {
            LogManager.getLogger(Replay.class).error("could not save the replay file", e);
            return false;
        }
        return true;
    }

}
