namespace AiClient.Utility
{
    public interface ILogger
    {
        void Debug(string message, string arg = null);
        void Info(string message, string arg = null);
        void Warning(string message, string arg = null);
        void Error(string message, string arg = null);
        void Debug(object message, string arg = null);
        void Info(object message, string arg = null);
        void Warning(object message, string arg = null);
        void Error(object message, string arg = null);
    }
}