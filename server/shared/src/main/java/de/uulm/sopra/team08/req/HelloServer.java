package de.uulm.sopra.team08.req;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

/**
 * HelloServer LOGIN Request, sent by a client to initiate connection to server.
 */
public class HelloServer extends MMLoginRequest {

    /**
     * Client name
     */
    @Expose
    private final String name;
    /**
     * Client deviceID
     */
    @Expose
    private final String deviceID;

    /**
     * Creates a new HelloServer LOGIN Request
     *
     * @param name     the name of the connecting client
     * @param deviceID the deviceID of the connecting client
     */
    public HelloServer(@NotNull String name, @NotNull String deviceID) {
        super(requestType.HELLO_SERVER);
        this.name = name;
        this.deviceID = deviceID;
    }

    /**
     * Convenience method to get a {@link HelloServer} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link HelloServer}
     * @throws IllegalArgumentException if {@link HelloServer} could not be parsed from the {@link JsonObject}
     */
    public static HelloServer fromJson(JsonObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json, HelloServer.class);
    }

    /**
     * Convenience method for transforming a {@link HelloServer} into a JSON String
     *
     * @return {@link HelloServer} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

    public String getName() {
        return name;
    }

    public String getDeviceID() {
        return deviceID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HelloServer that = (HelloServer) o;

        if (!getName().equals(that.getName())) return false;
        return getDeviceID().equals(that.getDeviceID());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDeviceID().hashCode();
        return result;
    }

}
