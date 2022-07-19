namespace AiClient.Network.Events
{
    public class EventType
    {
        public static readonly string GamestateEvent = "GamestateEvent";
        public static readonly string Ack = "Ack";
        public static readonly string Nack = "Nack";
        public static readonly string CustomEvent = "CustomEvent";
        public static readonly string TakenDamageEvent = "TakenDamageEvent";
        public static readonly string HealedEvent = "HealedEvent";
        public static readonly string ConsumedAPEvent = "ConsumedAPEvent";
        public static readonly string ConsumedMPEvent = "ConsumedMPEvent";
        public static readonly string SpawnEntityEvent = "SpawnEntityEvent";
        public static readonly string DestroyedEntityEvent = "DestroyedEntityEvent";
        public static readonly string MeleeAttackEvent = "MeleeAttackEvent";
        public static readonly string RangedAttackEvent = "RangedAttackEvent";
        public static readonly string MoveEvent = "MoveEvent";
        public static readonly string ExchangeInfinityStoneEvent = "ExchangeInfinityStoneEvent";
        public static readonly string UseInfinityStoneEvent = "UseInfinityStoneEvent";
        public static readonly string TeleportedEvent = "TeleportedEvent";
        public static readonly string RoundSetupEvent = "RoundSetupEvent";
        public static readonly string TurnEvent = "TurnEvent";
        public static readonly string WinEvent = "WinEvent";
        public static readonly string PauseStartEvent = "PauseStartEvent";
        public static readonly string PauseStopEvent = "PauseStopEvent";
        public static readonly string TurnTimeoutEvent = "TurnTimeoutEvent";
        public static readonly string TimeoutWarningEvent = "TimeoutWarningEvent";
        public static readonly string TimeoutEvent = "TimeoutEvent";
        public static readonly string DisconnectEvent = "DisconnectEvent";
    }
}