package de.uulm.sopra.team08.req;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * Reconnect LOGIN Request, sent by a client, if they wish to reconnect to a running game.
 */
public class Reconnect extends MMLoginRequest {

    @Expose
    private final boolean reconnect;

    /**
     * Creates a new Reconnect LOGIN Request
     *
     * @param reconnect true, if client wants to reconnect; false, if client wants to start a new game
     */
    public Reconnect(boolean reconnect) {
        super(requestType.RECONNECT);
        this.reconnect = reconnect;
    }

    /**
     * Convenience method to get a {@link Reconnect} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link Reconnect}
     * @throws IllegalArgumentException if {@link Reconnect} could not be parsed from the {@link JsonObject}
     */
    public static Reconnect fromJson(JsonObject json) {
        boolean reconnect = json.get("reconnect").getAsBoolean();

        // create and return new request
        return new Reconnect(reconnect);

    }

    /**
     * Convenience method for transforming a {@link Reconnect} into a JSON String
     *
     * @return {@link Reconnect} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

    public boolean getReconnect() {
        return reconnect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reconnect reconnect1 = (Reconnect) o;

        return getReconnect() == reconnect1.getReconnect();
    }

    @Override
    public int hashCode() {
        return (getReconnect() ? 1 : 0);
    }

}
