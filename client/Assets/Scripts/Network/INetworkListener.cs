using Network.Events;
using Network.Messages;

namespace Network
{
        public interface INetworkListener
        {
                public void OnMessageReceived(BasicMessage message, string json);
                
                public void OnMessageReceived(BasicEvent message, string json);
        }
}
