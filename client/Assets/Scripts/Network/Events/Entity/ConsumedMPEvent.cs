using Network.Objects;

namespace Network.Events.Entity
{
    public class ConsumedMPEvent : BasicEvent
    {
        public EventEntity targetEntity;
        public int[] targetField;
        public int amount;

        public ConsumedMPEvent()
        {
            eventType = EventType.ConsumedMPEvent;
        }
    }
}
