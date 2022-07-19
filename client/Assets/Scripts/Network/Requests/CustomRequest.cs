using System;

namespace Network.Requests
{
    public class CustomRequest:BasicRequest
    {
        public string teamIdentifier;
        public Object customContent;

        public CustomRequest(string teamIdentifier, Object customContent)
        {
            requestType = RequestType.CustomRequest;

            this.teamIdentifier = teamIdentifier;
            this.customContent = customContent;
        }
    }
}