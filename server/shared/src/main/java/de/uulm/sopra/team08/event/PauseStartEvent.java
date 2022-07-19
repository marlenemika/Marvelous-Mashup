package de.uulm.sopra.team08.event;

/**
 * PauseStart INGAME Event, sent to all clients when a the game is paused.
 */
public class PauseStartEvent extends MMIngameEvent {

    /**
     * Creates a new PauseStart INGAME Event
     */
    public PauseStartEvent() {
        super(EventType.PAUSE_START);
    }

    /**
     * Convenience method for transforming a {@link PauseStartEvent} into a JSON String
     *
     * @return {@link PauseStartEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

}
