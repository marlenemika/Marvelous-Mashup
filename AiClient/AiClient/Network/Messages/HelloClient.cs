namespace AiClient.Network.Messages
{
    public class HelloClient : BasicMessage
    {
        public bool runningGame;

        public HelloClient(bool runningGame)
        {
            messageType = MessageType.HELLO_CLIENT;

            this.runningGame = runningGame;
        }
        
        public HelloClient(bool runningGame, string optionals)
        {
            messageType = MessageType.HELLO_CLIENT;

            this.runningGame = runningGame;
            this.optionals = optionals;
        }

        public HelloClient()
        {
        }
    }
}
