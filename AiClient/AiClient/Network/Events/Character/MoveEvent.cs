using AiClient.Network.Objects;

namespace AiClient.Network.Events.Character
{
    public class MoveEvent : BasicEvent
    {
        public RequestEntity originEntity;
        public int[] originField;
        public int[] targetField;

        public MoveEvent(RequestEntity originEntity, int[] originField, int[] targetField)
        {
            eventType = EventType.MoveEvent;

            this.originEntity = originEntity;
            this.originField = originField;
            this.targetField = targetField;
        }

        public MoveEvent()
        {
        }
    }
}