package de.uulm.sopra.team08.event;

/**
 * Disconnect INGAME Event, sent when the connection to a client is being closed.
 */
public class DisconnectEvent extends MMIngameEvent {

    /**
     * Creates a new Disconnect INGAME Event
     */
    public DisconnectEvent() {
        super(EventType.DISCONNECT);
    }

    /**
     * Convenience method for transforming a {@link DisconnectEvent} into a JSON String
     *
     * @return {@link DisconnectEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

}
