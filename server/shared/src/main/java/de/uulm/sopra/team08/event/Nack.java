package de.uulm.sopra.team08.event;

/**
 * Nack INGAME Event, sent from server to client as a response to a request.
 */
public class Nack extends MMIngameEvent {

    /**
     * Creates a new Nack INGAME Event
     */
    public Nack() {
        super(EventType.NACK);
    }

    /**
     * Convenience method for transforming a {@link Nack} into a JSON String
     *
     * @return {@link Nack} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

}
