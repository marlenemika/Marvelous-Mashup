using AiClient.Network.Objects;

namespace AiClient.Network.Events.Gamestate
{
    public class GamestateEvent : BasicEvent
    {
        public EventEntity[] entities;
        public int[] mapSize;
        public RequestEntity[] turnOrder;
        public RequestEntity activeCharacter;
        public int[] stoneCooldowns;
        public bool winCondition;

        public GamestateEvent(EventEntity[] entities, int[] mapSize, RequestEntity[] turnOrder,
            RequestEntity activeCharacter, int[] stoneCooldowns, bool winCondition)
        {
            eventType = EventType.GamestateEvent;
            
            this.entities = entities;
            this.mapSize = mapSize;
            this.turnOrder = turnOrder;
            this.activeCharacter = activeCharacter;
            this.stoneCooldowns = stoneCooldowns;
            this.winCondition = winCondition;
        }

        public GamestateEvent()
        {
        }
    }
}