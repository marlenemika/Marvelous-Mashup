namespace AiClient.Network.Events.Game
{
    public class PauseStopEvent : BasicEvent
    {

        public PauseStopEvent()
        {
            eventType = EventType.PauseStopEvent;
        }
    }
}