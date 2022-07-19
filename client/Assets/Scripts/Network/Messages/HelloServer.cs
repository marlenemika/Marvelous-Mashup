namespace Network.Messages
{
    public class HelloServer : BasicMessage
    {
        public string name;
        public string deviceID;

        public HelloServer(string name, string deviceID)
        {
            messageType = "HELLO_SERVER";
            
            this.name = name;
            this.deviceID = deviceID;
        }
        
        public HelloServer(string name, string deviceID, string optionals)
        {
            messageType = MessageType.HELLO_SERVER;
            
            this.name = name;
            this.deviceID = deviceID;
            this.optionals = optionals;
        }

        public HelloServer()
        {
        }
    }
}
