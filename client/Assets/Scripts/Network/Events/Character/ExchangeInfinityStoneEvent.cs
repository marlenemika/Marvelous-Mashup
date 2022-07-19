using Network.Objects;

namespace Network.Events.Character
{
    public class ExchangeInfinityStoneEvent : BasicEvent
    {
        public RequestEntity originEntity;
        public RequestEntity targetEntity;
        public int[] originField;
        public int[] targetField;
        public RequestEntity stoneType;

        public ExchangeInfinityStoneEvent(RequestEntity originEntity, RequestEntity targetEntity, int[] originField,
            int[] targetField, RequestEntity stoneType)
        {
            eventType = EventType.ExchangeInfinityStoneEvent;

            this.originEntity = originEntity;
            this.targetEntity = targetEntity;
            this.originField = originField;
            this.targetField = targetField;
            this.stoneType = stoneType;
        }

        public ExchangeInfinityStoneEvent()
        {
        }
    }
}
