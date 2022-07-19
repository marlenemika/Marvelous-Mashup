package de.uulm.sopra.team08.req;

/**
 * Req INGAME Request, sent if the client wants the current Gamestate
 */
public class Req extends MMIngameRequest {

    /**
     * Creates a new Req INGAME Request.
     */
    public Req() {
        super(MMRequest.requestType.REQ);
    }

    /**
     * Convenience method for transforming a {@link Req} into a JSON String
     *
     * @return {@link Req} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

}
