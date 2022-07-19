using Network.Objects;

namespace Network.Events.Portal
{
    public class TeleportedEvent : BasicEvent
    {
        public RequestEntity teleportedEntity;
        public int[] originField; 
        public int[] targetField;
        public RequestEntity originalPortal;
        public RequestEntity targetPortal;

        public TeleportedEvent(RequestEntity teleportedEntity, RequestEntity originalPortal, RequestEntity targetPortal,
            int[] originField, int[] targetField)
        {
            eventType = EventType.TeleportedEvent;

            this.teleportedEntity = teleportedEntity;
            this.originalPortal = originalPortal;
            this.targetPortal = targetPortal;
            this.originField = originField;
            this.targetField = targetField;
        }

        public TeleportedEvent()
        {
        }
    }
}
