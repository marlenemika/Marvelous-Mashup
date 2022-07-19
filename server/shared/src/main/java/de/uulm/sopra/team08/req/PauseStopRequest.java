package de.uulm.sopra.team08.req;

/**
 * PauseStop INGAME Request, sent when a client wants to end a pause.
 */
public class PauseStopRequest extends MMIngameRequest {

    /**
     * Creates a new PauseStop INGAME Request
     */
    public PauseStopRequest() {
        super(MMRequest.requestType.PAUSE_STOP);
    }

    /**
     * Convenience method for transforming a {@link PauseStopRequest} into a JSON String
     *
     * @return {@link PauseStopRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

}
