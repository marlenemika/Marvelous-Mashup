package de.uulm.sopra.team08.req;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

/**
 * Error LOGIN Request, sent when there is an error with the connection. <br>
 * Even though this is a LOGIN Request, this Request can be sent/received at any time.
 */
public class ErrorRequest extends MMLoginRequest {

    /**
     * error message
     */
    @Expose
    private final String message;

    /**
     * error type
     */
    @Expose
    private final int type;

    /**
     * Creates a new Error LOGIN Request
     *
     * @param message the error message
     * @param type    the error type
     */
    public ErrorRequest(@NotNull String message, int type) {
        super(requestType.ERROR);
        this.message = message;
        this.type = type;

    }

    /**
     * Convenience method to get a {@link ErrorRequest} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link ErrorRequest}
     * @throws IllegalArgumentException if {@link ErrorRequest} could not be parsed from the {@link JsonObject}
     */
    public static ErrorRequest fromJson(JsonObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ErrorRequest.class);
    }

    /**
     * Convenience method for transforming a {@link ErrorRequest} into a JSON String
     *
     * @return {@link ErrorRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
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

        ErrorRequest error = (ErrorRequest) o;

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
