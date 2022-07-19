package de.uulm.sopra.team08.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Parent class for all INGAME Events. <br>
 *
 * @see MMEvent
 * @see MMLoginEvent
 */
public class MMIngameEvent implements MMEvent {

    /**
     * Default Gson object
     */
    protected final Gson gson = new Gson();
    /**
     * Gson object, that only uses Field with @Expose Annotation
     */
    protected final Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Expose
    private final EventType eventType;

    /**
     * Creates a new MMIngameEvent
     *
     * @param eventType the specific {@link EventType} of the event
     */
    public MMIngameEvent(EventType eventType) {
        this.eventType = eventType;
    }


    public EventType getEventType() {
        return eventType;
    }

    /**
     * Convenience method for transforming a {@link MMIngameEvent} into a JSON String
     *
     * @return {@link MMIngameEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MMIngameEvent that = (MMIngameEvent) o;

        return getEventType() == that.getEventType();
    }

    @Override
    public int hashCode() {
        return getEventType().hashCode();
    }

}
