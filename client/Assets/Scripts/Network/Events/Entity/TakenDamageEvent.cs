using Network.Objects;

namespace Network.Events.Entity
{
    public class TakenDamageEvent : BasicEvent
    {
        public EventEntity targetEntity;
        public int[] targetField;
        public int amount;

        public TakenDamageEvent()
        {
            eventType = EventType.TakenDamageEvent;
        }
    }
}
