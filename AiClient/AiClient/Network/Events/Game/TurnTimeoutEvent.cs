namespace AiClient.Network.Events.Game
{
    public class TurnTimeoutEvent : BasicEvent
    {

        public TurnTimeoutEvent()
        {
            eventType = EventType.TurnTimeoutEvent;
        }
    }
}