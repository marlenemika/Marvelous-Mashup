using AiClient.Network.Objects;

namespace AiClient.Network.Messages
{
    public class GameAssignment : BasicMessage
    {
        public string gameID;
        public Character[] characterSelection;

        public GameAssignment(string gameID, Character[] characterSelection)
        {
            messageType = MessageType.GAME_ASSIGNMENT;
            
            this.gameID = gameID;
            this.characterSelection = characterSelection;
        }

        public GameAssignment(string gameID, Character[] characterSelection, string optionals)
        {
            messageType = MessageType.GAME_ASSIGNMENT;

            this.gameID = gameID;
            this.characterSelection = characterSelection;
            this.optionals = optionals;
        }

        public GameAssignment()
        {
        }
    }
}
