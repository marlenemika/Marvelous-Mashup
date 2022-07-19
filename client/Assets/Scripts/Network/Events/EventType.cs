namespace Network.Events
{
    public class EventType
    {
        public static string GamestateEvent = "GamestateEvent";
        public static string Ack = "Ack";
        public static string Nack = "Nack";
        public static string CustomEvent = "CustomEvent";
        public static string TakenDamageEvent = "TakenDamageEvent";
        public static string HealedEvent = "HealedEvent";
        public static string ConsumedAPEvent = "ConsumedAPEvent";
        public static string ConsumedMPEvent = "ConsumedMPEvent";
        public static string SpawnEntityEvent = "SpawnEntityEvent";
        public static string DestroyedEntityEvent = "DestroyedEntityEvent";
        public static string MeleeAttackEvent = "MeleeAttackEvent";
        public static string RangedAttackEvent = "RangedAttackEvent";
        public static string MoveEvent = "MoveEvent";
        public static string ExchangeInfinityStoneEvent = "ExchangeInfinityStoneEvent";
        public static string UseInfinityStoneEvent = "UseInfinityStoneEvent";
        public static string TeleportedEvent = "TeleportedEvent";
        public static string RoundSetupEvent = "RoundSetupEvent";
        public static string TurnEvent = "TurnEvent";
        public static string WinEvent = "WinEvent";
        public static string PauseStartEvent = "PauseStartEvent";
        public static string PauseStopEvent = "PauseStopEvent";
        public static string TurnTimeoutEvent = "TurnTimeoutEvent";
        public static string TimeoutWarningEvent = "TimeoutWarningEvent";
        public static string TimeoutEvent = "TimeoutEvent";
        public static string DisconnectEvent = "DisconnectEvent";
    }
}
