using AiClient.Network.Objects;

namespace AiClient.Network.Events.Character
{
    public class UseInfinityStoneEvent : BasicEvent
    {
        public RequestEntity originEntity;
        public RequestEntity targetEntity;
        public int[] originField;
        public int[] targetField;
        public RequestEntity stoneType;

        public UseInfinityStoneEvent(RequestEntity originEntity, RequestEntity targetEntity, int[] originField,
            int[] targetField, RequestEntity stoneType)
        {
            eventType = EventType.UseInfinityStoneEvent;

            this.originEntity = originEntity;
            this.targetEntity = targetEntity;
            this.originField = originField;
            this.targetField = targetField;
            this.stoneType = stoneType;
        }

        public UseInfinityStoneEvent()
        {
        }
    }
}