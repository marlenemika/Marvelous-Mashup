package de.uulm.sopra.team08.req;

/**
 * EndRound INGAME Request, sent when a turn is ended prematurely.
 */
public class EndRoundRequest extends MMIngameRequest {

    /**
     * Creates a new EndRound INGAME Request
     */
    public EndRoundRequest() {
        super(MMRequest.requestType.END_ROUND);
    }

    /**
     * Convenience method for transforming a {@link EndRoundRequest} into a JSON String
     *
     * @return {@link EndRoundRequest} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

}
