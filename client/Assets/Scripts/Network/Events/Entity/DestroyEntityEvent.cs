using Network.Objects;

namespace Network.Events.Entity
{
    public class DestroyEntityEvent : BasicEvent
    {
        public int[] targetField;
        public RequestEntity targetEntity;

        public DestroyEntityEvent(int[] targetField, RequestEntity targetEntity)
        {
            eventType = EventType.DestroyedEntityEvent;
            this.targetField = targetField;
            this.targetEntity = targetEntity;
        }

        public DestroyEntityEvent()
        {
            eventType = EventType.DestroyedEntityEvent;
        }
    }
}
