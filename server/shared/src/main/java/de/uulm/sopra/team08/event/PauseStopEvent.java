package de.uulm.sopra.team08.event;

/**
 * PauseStop INGAME Event, sent to all Clients when a game pause ends.
 */
public class PauseStopEvent extends MMIngameEvent {

    /**
     * Creates a new PauseStop INGAME Event
     */
    public PauseStopEvent() {
        super(EventType.PAUSE_STOP);
    }

    /**
     * Convenience method for transforming a {@link PauseStopEvent} into a JSON String
     *
     * @return {@link PauseStopEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

}
