package de.uulm.sopra.team08.event;

/**
 * TurnTimeout INGAME Event, sent to a client, to inform them, that the turn took too long and resulted in a timeout.
 */
public class TurnTimeoutEvent extends MMIngameEvent {

    /**
     * Creates a new TurnTimeout INGAME Event
     */
    public TurnTimeoutEvent() {
        super(EventType.TURN_TIMEOUT);
    }

    /**
     * Convenience method for transforming a {@link TurnTimeoutEvent} into a JSON String
     *
     * @return {@link TurnTimeoutEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

}
