package de.uulm.sopra.team08.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Parent class for all LOGIN Events. <br>
 *
 * @see MMEvent
 * @see MMIngameEvent
 */
public class MMLoginEvent implements MMEvent {

    /**
     * Default Gson object
     */
    protected final Gson gson = new Gson();
    /**
     * Gson object, that only uses Field with @Expose Annotation
     */
    protected final Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Expose
    private final EventType messageType;

    /**
     * Creates a new MMIngameEvent
     *
     * @param eventType the specific {@link EventType} of the event
     */
    public MMLoginEvent(EventType eventType) {
        this.messageType = eventType;
    }


    public EventType getEventType() {
        return messageType;
    }

    /**
     * Convenience method for transforming a {@link MMLoginEvent} into a JSON String
     *
     * @return {@link MMLoginEvent} as JSON String
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

        MMLoginEvent that = (MMLoginEvent) o;

        return messageType == that.messageType;
    }

    @Override
    public int hashCode() {
        return messageType.hashCode();
    }

}
