package de.uulm.sopra.team08.req;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import de.uulm.sopra.team08.util.Role;
import org.jetbrains.annotations.NotNull;

/**
 * PlayerReady LOGIN Request, sent when a client connects to a new game. <br>
 * In case of a reconnect use {@link Reconnect} instead.
 */
public class PlayerReady extends MMLoginRequest {

    @Expose
    private final boolean startGame;
    @Expose
    private final Role role;

    /**
     * Creates a new PlayerReady LOGIN Request
     *
     * @param startGame true, if player starts a game, false, if player exits to main menu
     * @param role      role chosen by client (Player, KI, Spectator)
     */
    public PlayerReady(boolean startGame, @NotNull Role role) {
        super(requestType.PLAYER_READY);
        this.startGame = startGame;
        this.role = role;
    }

    /**
     * Convenience method to get a {@link PlayerReady} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link PlayerReady}
     * @throws IllegalArgumentException if {@link PlayerReady} could not be parsed from the {@link JsonObject}
     */
    public static PlayerReady fromJson(JsonObject json) {
        try {
            // extract attributes
            boolean startGame = json.get("startGame").getAsBoolean();
            Role role = Role.valueOf(json.get("role").getAsString());

            // create and return new request
            return new PlayerReady(startGame, role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link PlayerReady} into a JSON String
     *
     * @return {@link PlayerReady} as JSON String
     */
    @Override
    public String toJsonRequest() {
        return gsonExpose.toJson(this);
    }

    public boolean getStartGame() {
        return startGame;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerReady that = (PlayerReady) o;

        if (getStartGame() != that.getStartGame()) return false;
        return getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        int result = (getStartGame() ? 1 : 0);
        result = 31 * result + getRole().hashCode();
        return result;
    }

}
