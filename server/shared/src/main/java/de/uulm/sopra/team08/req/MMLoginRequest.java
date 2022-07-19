package de.uulm.sopra.team08.req;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Parent class for all LOGIN Requests. <br>
 *
 * @see MMRequest
 * @see MMIngameRequest
 */
public class MMLoginRequest implements MMRequest {

    /**
     * Default Gson object
     */
    protected final Gson gson = new Gson();
    /**
     * Gson object, that only uses Field with @Expose Annotation
     */
    protected final Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Expose
    private final requestType messageType;


    /**
     * Creates a new MMIngameRequest
     *
     * @param messageType the specific {@link requestType} of the request
     */
    public MMLoginRequest(requestType messageType) {
        this.messageType = messageType;
    }


    /**
     * Convenience method for transforming a {@link MMLoginRequest} into a JSON String
     *
     * @return {@link MMLoginRequest} as JSON String
     */
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public requestType getRequestType() {
        return messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MMLoginRequest that = (MMLoginRequest) o;

        return messageType == that.messageType;
    }

    @Override
    public int hashCode() {
        return messageType != null ? messageType.hashCode() : 0;
    }

}

