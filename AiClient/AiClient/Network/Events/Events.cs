using System.Collections.Generic;
using AiClient.Network.Messages;

namespace AiClient.Network.Events
{
    public class Events : BasicMessage
    {
        public List<BasicEvent> messages;
    }
}