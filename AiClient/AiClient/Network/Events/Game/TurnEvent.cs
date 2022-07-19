using AiClient.Network.Objects;

namespace AiClient.Network.Events.Game
{
    public class TurnEvent : BasicEvent
    {
        public int turnCount;
        public RequestEntity nextCharacter;

        public TurnEvent(int turnCount, RequestEntity nextCharacter)
        {
            eventType = EventType.TurnEvent;
            this.turnCount = turnCount;
            this.nextCharacter = nextCharacter;
        }

        public TurnEvent()
        {
            eventType = EventType.TurnEvent;
        }
    }
}