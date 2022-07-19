package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

/**
 * Error LOGIN Event, sent when there is an error with the connection. <br>
 * Even though this is a LOGIN Event, this Event can be sent/received at any time.
 */
public class ErrorEvent extends MMLoginEvent {

    public static final ErrorEvent REQUEST_UNSUPPORTED = new ErrorEvent("Unsupported Request!", 1);
    public static final ErrorEvent REQUEST_UNEXPECTED = new ErrorEvent("Unexpected Request!", 2);
    public static final ErrorEvent REQUEST_RECONNECT_FAILED = new ErrorEvent("Could not reconnect! (Identifier not found!)", 3);
    public static final ErrorEvent REQUEST_REGISTER_FAILED = new ErrorEvent("Could not register Player!", 4);
    public static final ErrorEvent REQUEST_RECOVER_FAILED = new ErrorEvent("Could not recover known Player!", 5);
    @Expose
    private final String message;
    @Expose
    private final int type;


    /**
     * Creates a new Error LOGIN Event
     *
     * @param message the error message
     * @param type    the error type
     */
    public ErrorEvent(@NotNull String message, int type) {
        super(EventType.ERROR);
        this.message = message;
        this.type = type;

    }


    /**
     * Convenience method to get a {@link ErrorEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link ErrorEvent}
     * @throws IllegalArgumentException if {@link ErrorEvent} could not be parsed from the {@link JsonObject}
     */
    public static ErrorEvent fromJson(JsonObject json) {
        try {
            String message = json.get("message").getAsString();

            int type = json.get("type").getAsInt();

            // create and return new request
            return new ErrorEvent(message, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link ErrorEvent} into a JSON String
     *
     * @return {@link ErrorEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorEvent error = (ErrorEvent) o;

        if (getType() != error.getType()) return false;
        return getMessage().equals(error.getMessage());
    }

    @Override
    public int hashCode() {
        int result = getMessage().hashCode();
        result = 31 * result + getType();
        return result;
    }

}
