namespace AiClient.Network.Messages
{
    public class GoodbyeClient : BasicMessage
    {
        public string message;

        public GoodbyeClient(string message)
        {
            messageType = MessageType.GOODBYE_CLIENT;
            
            this.message = message;
        }
        
        public GoodbyeClient(string message, string optionals)
        {
            messageType = MessageType.GOODBYE_CLIENT;
            
            this.message = message;
            this.optionals = optionals;
        }

        public GoodbyeClient()
        {
        }
    }
}