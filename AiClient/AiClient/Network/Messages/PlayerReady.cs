namespace AiClient.Network.Messages
{
    public class PlayerReady : BasicMessage
    {
        public bool startGame;
        public string role;

        public PlayerReady(bool startGame, string role)
        {
            messageType = MessageType.PLAYER_READY;

            this.startGame = startGame;
            this.role = role;
        }
        
        public PlayerReady(bool startGame, string role, string optionals)
        {
            messageType = MessageType.PLAYER_READY;

            this.startGame = startGame;
            this.role = role;
            this.optionals = optionals;
        }

        public PlayerReady()
        {
        }
    }
}