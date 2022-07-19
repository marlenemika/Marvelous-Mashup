namespace AiClient.Network.Events.Game
{
    public class DisconnectEvent : BasicEvent
    {

        public DisconnectEvent()
        {
            eventType = EventType.DisconnectEvent;
        }
    }
}