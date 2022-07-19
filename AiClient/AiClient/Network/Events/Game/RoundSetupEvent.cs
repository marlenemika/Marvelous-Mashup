using AiClient.Network.Objects;

namespace AiClient.Network.Events.Game
{
    public class RoundSetupEvent : BasicEvent
    {
        public int roundCount;
        public RequestEntity[] characterOrder;

        public RoundSetupEvent(int roundCount, RequestEntity[] characterOrder)
        {
            eventType = EventType.RoundSetupEvent;
            this.roundCount = roundCount;
            this.characterOrder = characterOrder;
        }

        public RoundSetupEvent()
        {
            eventType = EventType.RoundSetupEvent;
        }
    }
}