using Network.Objects;

namespace Network.Events.Entity
{
    public class ConsumedAPEvent : BasicEvent
    {
        public string eventType;
        public EventEntity targetEntity;
        public int[] targetField;
        public int amount;

        public ConsumedAPEvent()
        {
            eventType = EventType.ConsumedAPEvent;
        }
    }
}
