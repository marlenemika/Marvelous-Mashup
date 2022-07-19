using Network.Objects;

namespace Network.Messages
{
    public class GameStructure : BasicMessage
    {
        public string assignment;
        public Character[] playerOneCharacters;
        public string playerOneName;
        public Character[] playerTwoCharacters;
        public string playerTwoName;
        public ScenarioConfig scenarioconfig;
        public MatchConfig matchconfig;

        public GameStructure(string assignment, string playerOneName, string playerTwoName,
            Character[] playerOneCharacters, Character[] playerTwoCharacters, MatchConfig matchconfig,
            ScenarioConfig scenarioconfig)
        {
            messageType = MessageType.GAME_STRUCTURE;
            
            this.assignment = assignment;
            this.playerOneName = playerOneName;
            this.playerTwoName = playerTwoName;
            this.playerOneCharacters = playerOneCharacters;
            this.playerTwoCharacters = playerTwoCharacters;
            this.matchconfig = matchconfig;
            this.scenarioconfig = scenarioconfig;
        }
        
        public GameStructure(string assignment, string playerOneName, string playerTwoName,
            Character[] playerOneCharacters, Character[] playerTwoCharacters, MatchConfig matchconfig,
            ScenarioConfig scenarioconfig, string optionals)
        {
            messageType = MessageType.GAME_STRUCTURE;
            
            this.assignment = assignment;
            this.playerOneName = playerOneName;
            this.playerTwoName = playerTwoName;
            this.playerOneCharacters = playerOneCharacters;
            this.playerTwoCharacters = playerTwoCharacters;
            this.matchconfig = matchconfig;
            this.scenarioconfig = scenarioconfig;
            this.optionals = optionals;
        }

        public GameStructure()
        {
        }
    }
}
