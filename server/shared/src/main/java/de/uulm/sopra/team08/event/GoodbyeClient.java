package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * GoodbyeClient LOGIN Event, sent to a client as the last message.
 */
public class GoodbyeClient extends MMLoginEvent {

    @Expose
    private final String message;


    /**
     * Creates a new GoodbyeClient LOGIN Event
     *
     * @param message goodbye message
     */
    public GoodbyeClient(String message) {
        super(EventType.GOODBYE_CLIENT);
        this.message = message;
    }

    /**
     * Convenience method to get a {@link GoodbyeClient} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link GoodbyeClient}
     * @throws IllegalArgumentException if {@link GoodbyeClient} could not be parsed from the {@link JsonObject}
     */
    public static GoodbyeClient fromJson(JsonObject json) {
        try {
            String message = json.get("message").getAsString();

            // create and return new request
            return new GoodbyeClient(message);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link GoodbyeClient} into a JSON String
     *
     * @return {@link GoodbyeClient} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodbyeClient that = (GoodbyeClient) o;

        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }

}
