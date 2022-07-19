package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

/**
 * TimeoutWarning INGAME Event, sent to warn the player of a impending timeout
 */
public class TimeoutWarningEvent extends MMIngameEvent {

    @Expose
    private final String message;
    @Expose
    private final int timeLeft;

    /**
     * Creates a new TimeoutWarning INGAME Event
     *
     * @param message  the warning message
     * @param timeLeft the time left till a timeout
     */
    public TimeoutWarningEvent(@NotNull String message, int timeLeft) {
        super(EventType.TIMEOUT_WARNING);
        this.message = message;
        this.timeLeft = timeLeft;
    }

    /**
     * Convenience method to get a {@link TimeoutWarningEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link TimeoutWarningEvent}
     * @throws IllegalArgumentException if {@link TimeoutWarningEvent} could not be parsed from the {@link JsonObject}
     */
    public static TimeoutWarningEvent fromJson(JsonObject json) {
        try {
            // get message
            String message = json.get("message").getAsString();

            // get timeLeft
            int timeLeft = json.get("timeLeft").getAsInt();

            return new TimeoutWarningEvent(message, timeLeft);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link TimeoutWarningEvent} into a JSON String
     *
     * @return {@link TimeoutWarningEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }


    public String getMessage() {
        return message;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeoutWarningEvent that = (TimeoutWarningEvent) o;

        if (getTimeLeft() != that.getTimeLeft()) return false;
        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        int result = getMessage().hashCode();
        result = 31 * result + getTimeLeft();
        return result;
    }

}
