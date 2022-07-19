using Network.Objects;

namespace Network.Events.Entity
{
    public class SpawnEntityEvent : BasicEvent
    {
        public EventEntity entity;

        public SpawnEntityEvent()
        {
            eventType = EventType.SpawnEntityEvent;
        }
    }
}
