package de.uulm.sopra.team08.req;

/**
 * PauseStart INGAME Request, sent when a client wants to start a pause.
 */
public class PauseStartRequest extends MMIngameRequest {

    /**
     * Creates a new PauseStart INGAME Request
     */
    public PauseStartRequest() {
        super(MMRequest.requestType.PAUSE_START);
    }

    /**
     * Convenience method for transforming a {@link PauseStartRequest} into a JSON String
     *
     * @return {@link PauseStartRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

}
