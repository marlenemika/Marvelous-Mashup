package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;


/**
 * HelloClient LOGIN Event, sent as a response to {@link de.uulm.sopra.team08.req.HelloServer}. <br>
 * Also informs client, if there is a runningGame or not.
 */
public class HelloClient extends MMLoginEvent {

    @Expose
    private final boolean runningGame;

    /**
     * Creates a new HelloClient LOGIN Event
     *
     * @param runningGame whether client already has a running game
     */
    public HelloClient(boolean runningGame) {
        super(EventType.HELLO_CLIENT);
        this.runningGame = runningGame;
    }

    /**
     * Convenience method to get a {@link HelloClient} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link HelloClient}
     * @throws IllegalArgumentException if {@link HelloClient} could not be parsed from the {@link JsonObject}
     */
    public static HelloClient fromJson(JsonObject json) {
        try {
            boolean runningGame = json.get("runningGame").getAsBoolean();

            // create and return new request
            return new HelloClient(runningGame);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link HelloClient} into a JSON String
     *
     * @return {@link HelloClient} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public boolean isRunningGame() {
        return runningGame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HelloClient that = (HelloClient) o;

        return isRunningGame() == that.isRunningGame();
    }

    @Override
    public int hashCode() {
        return (isRunningGame() ? 1 : 0);
    }

}
