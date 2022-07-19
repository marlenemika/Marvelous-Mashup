using System.Collections.Generic;
using AiClient.Network.Messages;

namespace AiClient.Network.Requests
{
    public class Requests : BasicMessage
    {
        public List<BasicRequest> messages;

        public Requests(List<BasicRequest> messages)
        {
            this.messages = messages;
        }

        public Requests()
        {
            
        }
    }
}