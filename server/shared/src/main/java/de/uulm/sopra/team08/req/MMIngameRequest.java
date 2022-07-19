package de.uulm.sopra.team08.req;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Parent class for all INGAME Requests. <br>
 *
 * @see MMRequest
 * @see MMLoginRequest
 */
public class MMIngameRequest implements MMRequest {

    /**
     * Default Gson object
     */
    protected final Gson gson = new Gson();
    /**
     * Gson object, that only uses Field with @Expose Annotation
     */
    protected final Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Expose
    private final MMRequest.requestType requestType;


    /**
     * Creates a new MMIngameRequest
     *
     * @param requestType the specific {@link MMRequest.requestType} of the request
     */
    public MMIngameRequest(MMRequest.requestType requestType) {
        this.requestType = requestType;
    }


    /**
     * Convenience method for transforming a {@link MMIngameRequest} into a JSON String
     *
     * @return {@link MMIngameRequest} as JSON String
     */
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public MMRequest.requestType getRequestType() {
        return requestType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MMIngameRequest that = (MMIngameRequest) o;

        return getRequestType() == that.getRequestType();
    }

    @Override
    public int hashCode() {
        return getRequestType().hashCode();
    }

}

