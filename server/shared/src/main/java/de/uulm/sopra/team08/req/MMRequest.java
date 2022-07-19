package de.uulm.sopra.team08.req;

import com.google.gson.annotations.SerializedName;
import de.uulm.sopra.team08.event.*;

/**
 * Interface for all Requests. <br>
 * Events are always sent from the server to a client.<br>
 * <ul>
 *     <li>INGAME Requests are classes, that extend {@link MMIngameRequest}.</li>
 *     <li>LOGIN Events are  classes, that extend {@link MMLoginRequest}.</li>
 * </ul>
 *
 * @see MMIngameRequest
 * @see MMLoginRequest
 * @see MMEvent
 * @see MMIngameEvent
 * @see MMLoginEvent
 */
public interface MMRequest {

    enum requestType {
        @SerializedName("MeleeAttackRequest")
        MELEE_ATTACK(true, true),
        @SerializedName("RangedAttackRequest")
        RANGED_ATTACK(true, true),
        @SerializedName("MoveRequest")
        MOVE(true, true),
        @SerializedName("UseInfinityStoneRequest")
        USE_INFINITY_STONE(true, true),
        @SerializedName("ExchangeInfinityRequest")
        EXCHANGE_INFINITY_STONE(true, true),
        @SerializedName("DisconnectRequest")
        DISCONNECT(true, false),
        @SerializedName("PauseStartRequest")
        PAUSE_START(true, true),
        @SerializedName("PauseStopRequest")
        PAUSE_STOP(true, true),
        @SerializedName("EndRoundRequest")
        END_ROUND(true, true),
        @SerializedName("Req")
        REQ(true, false),
        HELLO_SERVER(false, false),
        RECONNECT(false, false),
        PLAYER_READY(false, false),
        CHARACTER_SELECTION(false, false),
        ERROR(false, false),
        ;

        private final boolean ingame;
        private final boolean requiresResponse;


        requestType(boolean ingame, boolean requiresResponse) {
            this.ingame = ingame;
            this.requiresResponse = requiresResponse;
        }


        /**
         * @return true, if this type is declared by the ingame team (Standard document).
         */
        public boolean isIngame() {
            return ingame;
        }

        /**
         * @return true, if this request requires an {@link Ack} or {@link Nack} response/event.
         */
        public boolean isRequiresResponse() {
            return requiresResponse;
        }
    }

    String toJsonRequest();

    String toString();

    requestType getRequestType();

}
