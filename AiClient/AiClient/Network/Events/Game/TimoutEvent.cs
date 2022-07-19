namespace AiClient.Network.Events.Game
{
    public class TimeoutEvent : BasicEvent
    {
        public string message;

        public TimeoutEvent(string message)
        {
            eventType = EventType.TimeoutEvent;
            this.message = message;
        }
        
        public TimeoutEvent()
        {
            eventType = EventType.TimeoutEvent;
        }
    }
}