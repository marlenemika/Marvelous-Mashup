namespace AiClient.Network.Events.Game
{
    public class WinEvent : BasicEvent
    {
        public int playerWon;

        public WinEvent(int playerWon)
        {
            eventType = EventType.WinEvent;
            this.playerWon = playerWon;
        }
        
        public WinEvent()
        {
            eventType = EventType.WinEvent;
        }
    }
}