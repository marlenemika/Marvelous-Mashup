using System;
using AiClient.Network.Objects;

namespace AiClient.Network.Events.Entity
{
    public class DestroyedEntityEvent : BasicEvent
    {
        public int[] targetField;
        public RequestEntity targetEntity;

        public DestroyedEntityEvent(int[] targetField, RequestEntity targetEntity)
        {
            this.eventType = EventType.DestroyedEntityEvent;
            this.targetField = targetField;
            this.targetEntity = targetEntity;
        }

        public DestroyedEntityEvent()
        {
        }
    }
}
