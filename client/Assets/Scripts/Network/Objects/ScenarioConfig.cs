namespace Network.Objects
{
    public class ScenarioConfig
    {
        public string author;
        public string name;
        public string[,] scenario;

        public ScenarioConfig(string author, string name, string[,] scenario)
        {
            this.author = author;
            this.name = name;
            this.scenario = scenario;
        }
    }
}
