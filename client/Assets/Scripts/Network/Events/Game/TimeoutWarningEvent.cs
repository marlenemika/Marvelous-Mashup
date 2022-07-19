namespace Network.Events.Game
{
    public class TimeoutWarningEvent : BasicEvent
    {
        public string message;
        public int timeLeft;

        public TimeoutWarningEvent(string message, int timeLeft)
        {
            eventType = EventType.TimeoutWarningEvent;
            this.message = message;
            this.timeLeft = timeLeft;
        }

        public TimeoutWarningEvent()
        {
            eventType = EventType.TimeoutWarningEvent;
        }
    }
}
