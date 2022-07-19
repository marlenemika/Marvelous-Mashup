package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.event.Nack;

final class NetSettings {

    /**
     * Defines whether awaited responses should be discarded when disconnecting a player.
     * If this is false, an {@link Nack} is sent for each awaited response.
     */
    static final boolean DISCARD_RESPONSES_ON_DISCONNECT = false;

    private NetSettings() {}

}
