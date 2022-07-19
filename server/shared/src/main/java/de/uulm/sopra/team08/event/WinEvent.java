package de.uulm.sopra.team08.event;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * Win INGAME Event, sent to inform clients, that a player has won
 */
public class WinEvent extends MMIngameEvent {

    @Expose
    private final int playerWon;

    /**
     * Creates a new Win INGAME Event
     *
     * @param playerWon which player won as an int between 1 and 2
     * @throws IllegalArgumentException if player won isn't 1 or 2
     */
    public WinEvent(int playerWon) {
        super(EventType.WIN);
        if (playerWon < 1 || playerWon > 2)
            throw new IllegalArgumentException("playerWon may only be 1 or 2");
        this.playerWon = playerWon;
    }

    /**
     * Convenience method to get a {@link WinEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link WinEvent}
     * @throws IllegalArgumentException if {@link WinEvent} could not be parsed from the {@link JsonObject}
     */
    public static WinEvent fromJson(JsonObject json) {
        try {
            // get playerWon
            int playerWon = json.get("playerWon").getAsInt();

            return new WinEvent(playerWon);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link WinEvent} into a JSON String
     *
     * @return {@link WinEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        return gsonExpose.toJson(this);
    }

    public int getPlayerWon() {
        return playerWon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WinEvent winEvent = (WinEvent) o;

        return getPlayerWon() == winEvent.getPlayerWon();
    }

    @Override
    public int hashCode() {
        return getPlayerWon();
    }

}
