package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

/**
 * Timeout INGAME Event, sent before a connection to client is closed, due to timeout.
 */
public class TimeoutEvent extends MMIngameEvent {

    @Expose
    private final String message;

    /**
     * Creates a new Timeout INGAME Event
     *
     * @param message the timeout message
     */
    public TimeoutEvent(@NotNull String message) {
        super(EventType.TIMEOUT);
        this.message = message;
    }

    /**
     * Convenience method to get a {@link TimeoutEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link TimeoutEvent}
     * @throws IllegalArgumentException if {@link TimeoutEvent} could not be parsed from the {@link JsonObject}
     */
    public static TimeoutEvent fromJson(JsonObject json) {
        try {
            // get message
            String message = json.get("message").getAsString();

            return new TimeoutEvent(message);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link TimeoutEvent} into a JSON String
     *
     * @return {@link TimeoutEvent} as JSON String
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

        TimeoutEvent that = (TimeoutEvent) o;

        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }

}
