namespace Network.Events.Game
{
    public class PauseStartEvent : BasicEvent
    {
        public PauseStartEvent()
        {
            eventType = EventType.PauseStartEvent;
        }
    }
}
