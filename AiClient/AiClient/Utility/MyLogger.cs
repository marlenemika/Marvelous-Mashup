using NLog;

namespace AiClient.Utility
{
    public class MyLogger : ILogger
    {
        //singlton patttern
        private static MyLogger instance;   //single instance of this class
        private static Logger logger;       //static variable to hold a single instance of the nLog logger
        
        //crivate constructor
        private MyLogger()
        {
            
        }

        public static MyLogger GetInstance()
        {
            if (instance == null)
            {
                instance = new MyLogger();
            }

            return instance;
        }
        private static Logger GetLogger(string theLogger)
        {
            if (logger == null)
            {
                logger = LogManager.GetLogger(theLogger);
            }
            return logger;
        }
        
        public void Debug(string message, string arg = null)
        {
            if (arg == null)
            {
                GetLogger("myLogger").Debug(message);
            }
            else
            {
                GetLogger("myLogger").Debug(message,arg);
            }
        }

        public void Info(string message, string arg = null)
        {
            if (arg == null)
            {
                GetLogger("myLogger").Info(message);
            }
            else
            {
                GetLogger("myLogger").Info(message,arg);
            }
        }

        public void Warning(string message, string arg = null)
        {
            if (arg == null)
            {
                GetLogger("myLogger").Warn(message);
            }
            else
            {
                GetLogger("myLogger").Warn(message,arg);
            }
        }

        public void Error(string message, string arg = null)
        {
            if (arg == null)
            {
                GetLogger("myLogger").Error(message);
            }
            else
            {
                GetLogger("myLogger").Error(message,arg);
            }
        }

        public void Debug(object message, string arg = null)
        {
            Debug(message.ToString(),arg);
        }

        public void Info(object message, string arg = null)
        {
            Info(message.ToString(),arg);
        }

        public void Warning(object message, string arg = null)
        {
            Warning(message.ToString(),arg);
        }

        public void Error(object message, string arg = null)
        {
            Error(message.ToString(),arg);
        }
    }
}