namespace AiClient.Network.Messages
{
    public class Reconnect : BasicMessage
    {
        public bool reconnect;
        
        public Reconnect(bool reconnect)
        {
            messageType = MessageType.RECONNECT;
            
            this.reconnect = reconnect;
        }

        public Reconnect(bool reconnect, string optionals)
        {
            messageType = MessageType.RECONNECT;

            this.reconnect = reconnect;
            this.optionals = optionals;
        }

        public Reconnect()
        {
        }
    }
}