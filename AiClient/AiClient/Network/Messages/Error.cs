namespace AiClient.Network.Messages
{
    public class Error : BasicMessage
    {
        public string message;
        public int type;

        public Error(string message, int type)
        {
            messageType = MessageType.ERROR;

            this.message = message;
            this.type = type;
        }
        
        public Error(string message, int type, string optionals)
        {
            messageType = MessageType.ERROR;

            this.message = message;
            this.type = type;
            this.optionals = optionals;
        }

        public Error()
        {
        }
    }
}
