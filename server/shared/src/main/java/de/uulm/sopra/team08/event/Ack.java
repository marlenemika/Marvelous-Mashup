package de.uulm.sopra.team08.event;

/**
 * Ack INGAME Event, sent from server to client as a response to a request.
 */
public class Ack extends MMIngameEvent {

    /**
     * Creates a new Ack INGAME Event
     */
    public Ack() {
        super(EventType.ACK);
    }

    /**
     * Convenience method for transforming an Ack into a JSON String
     *
     * @return Ack as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

}
