namespace Network.Messages
{
    public class GeneralAssignment : BasicMessage
    {
        public string gameID;

        public GeneralAssignment(string gameID)
        {
            messageType = MessageType.GENERAL_ASSIGNMENT;

            this.gameID = gameID;
        }
        
        public GeneralAssignment(string gameID, string optionals)
        {
            messageType = MessageType.GENERAL_ASSIGNMENT;

            this.gameID = gameID;
            this.optionals = optionals;
        }

        public GeneralAssignment()
        {
        }
    }
}
