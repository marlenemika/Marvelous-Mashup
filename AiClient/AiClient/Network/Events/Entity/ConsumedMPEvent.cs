using AiClient.Network.Objects;

namespace AiClient.Network.Events.Entity
{
    public class ConsumedMPEvent : BasicEvent
    {
        public EventEntity targetEntity;
        public int[] targetField;
        public int amount;

        public ConsumedMPEvent()
        {
        }
    }
}