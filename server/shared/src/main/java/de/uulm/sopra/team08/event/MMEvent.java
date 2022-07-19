package de.uulm.sopra.team08.event;

import com.google.gson.annotations.SerializedName;
import de.uulm.sopra.team08.req.MMIngameRequest;
import de.uulm.sopra.team08.req.MMLoginRequest;
import de.uulm.sopra.team08.req.MMRequest;

/**
 * Interface for all Events. <br>
 * Events are always sent from the server to a client.<br>
 * <ul>
 *     <li>INGAME Events are classes, that extend {@link MMIngameEvent}.</li>
 *     <li>LOGIN Events are  classes, that exten {@link MMLoginEvent}.</li>
 * </ul>
 *
 * @see MMIngameEvent
 * @see MMLoginEvent
 * @see MMRequest
 * @see MMIngameRequest
 * @see MMLoginRequest
 */
public interface MMEvent {

    enum EventType {
        @SerializedName("Ack")
        ACK(true),
        @SerializedName("Nack")
        NACK(true),
        @SerializedName("GamestateEvent")
        GAMESTATE(true),
        @SerializedName("DestroyedEntityEvent")
        DESTROYED_ENTITY(true),
        @SerializedName("HealedEvent")
        HEALED(true),
        @SerializedName("TakenDamageEvent")
        TAKEN_DAMAGE(true),
        @SerializedName("SpawnEntityEvent")
        SPAWN_ENTITY(true),
        @SerializedName("MeleeAttackEvent")
        MELEE_ATTACK(true),
        @SerializedName("RangedAttackEvent")
        RANGED_ATTACK(true),
        @SerializedName("MoveEvent")
        MOVE(true),
        @SerializedName("UseInfinityStoneEvent")
        USE_INFINITY_STONE(true),
        @SerializedName("ExchangeInfinityStoneEvent")
        EXCHANGE_INFINITY_STONE(true),
        @SerializedName("TimeoutEvent")
        TIMEOUT(true),
        @SerializedName("TimeoutWarningEvent")
        TIMEOUT_WARNING(true),
        @SerializedName("WinEvent")
        WIN(true),
        @SerializedName("TeleportedEvent")
        TELEPORTED(true),
        @SerializedName("RoundSetupEvent")
        ROUND_SETUP(true),
        @SerializedName("TurnEvent")
        TURN(true),
        @SerializedName("TurnTimeoutEvent")
        TURN_TIMEOUT(true),
        @SerializedName("DisconnectEvent")
        DISCONNECT(true),
        @SerializedName("ConsumedAPEvent")
        CONSUMED_AP(true),
        @SerializedName("ConsumedMPEvent")
        CONSUMED_MP(true),
        @SerializedName("PauseStartEvent")
        PAUSE_START(true),
        @SerializedName("PauseStopEvent")
        PAUSE_STOP(true),
        HELLO_CLIENT(false),
        GAME_ASSIGNMENT(false),
        GENERAL_ASSIGNMENT(false),
        CONFIRM_SELECTION(false),
        GAME_STRUCTURE(false),
        GOODBYE_CLIENT(false),
        ERROR(false),
        ;

        private final boolean ingame;


        EventType(boolean ingame) {
            this.ingame = ingame;
        }


        /**
         * @return true, if this type is declared by the ingame team (Standard document).
         */
        public boolean isIngame() {
            return ingame;
        }
    }

    String toJsonEvent();

    @Override
    String toString();

    EventType getEventType();

}
