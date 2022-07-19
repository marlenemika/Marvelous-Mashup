namespace AiClient.Network.Messages
{
    public class MessageType
    {
        public static string HELLO_CLIENT = "HELLO_CLIENT";
        public static string HELLO_SERVER = "HELLO_SERVER";
        public static string RECONNECT = "RECONNECT";
        public static string PLAYER_READY = "PLAYER_READY";
        public static string GAME_ASSIGNMENT = "GAME_ASSIGNMENT";
        public static string GENERAL_ASSIGNMENT = "GENERAL_ASSIGNMENT";
        public static string CHARACTER_SELECTION = "CHARACTER_SELECTION";
        public static string CONFIRM_SELECTION = "CONFIRM_SELECTION";
        public static string GAME_STRUCTURE = "GAME_STRUCTURE";
        public static string GOODBYE_CLIENT = "GOODBYE_CLIENT";
        public static string ERROR = "ERROR";
}
}
