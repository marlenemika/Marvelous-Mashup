namespace Network.Events.Notification
{
    public class Ack : BasicEvent
    {
        public Ack()
        {
            eventType = EventType.Ack;
        }
    }
}
