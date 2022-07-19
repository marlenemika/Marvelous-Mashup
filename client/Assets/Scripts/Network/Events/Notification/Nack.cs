namespace Network.Events.Notification
{
    public class Nack : BasicEvent
    {
        public Nack()
        {
            eventType = EventType.Nack;
        }
    }
}
