package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * ConfirmSelection LOGIN Event, sent from server to the client, that picked their characters first.
 */
public class ConfirmSelection extends MMLoginEvent {

    @Expose
    private final boolean selectionComplete;

    /**
     * Creates a new ConfirmSelection LOGIN Event
     *
     * @param selectionComplete true if client picked first and has to wait
     */
    public ConfirmSelection(boolean selectionComplete) {
        super(EventType.CONFIRM_SELECTION);
        this.selectionComplete = selectionComplete;
    }

    /**
     * Convenience method to get a {@link ConfirmSelection} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link ConfirmSelection}
     * @throws IllegalArgumentException if {@link ConfirmSelection} could not be parsed from the {@link JsonObject}
     */
    public static ConfirmSelection fromJson(JsonObject json) {
        try {
            boolean selectionComplete = json.get("selectionComplete").getAsBoolean();

            // create and return new request
            return new ConfirmSelection(selectionComplete);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link ConfirmSelection} into a JSON String
     *
     * @return {@link ConfirmSelection} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public boolean isSelectionComplete() {
        return selectionComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfirmSelection that = (ConfirmSelection) o;

        return isSelectionComplete() == that.isSelectionComplete();
    }

    @Override
    public int hashCode() {
        return (isSelectionComplete() ? 1 : 0);
    }

}
