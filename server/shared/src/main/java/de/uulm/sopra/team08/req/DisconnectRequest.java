package de.uulm.sopra.team08.req;

/**
 * Disconnect INGAME Request, sent when a client wishes to disconnect from the server.
 */
public class DisconnectRequest extends MMIngameRequest {

    /**
     * Creates a new Disconnect INGAME Request
     */
    public DisconnectRequest() {
        super(MMRequest.requestType.DISCONNECT);
    }

    /**
     * Convenience method for transforming a {@link DisconnectRequest} into a JSON String
     *
     * @return {@link DisconnectRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

}
