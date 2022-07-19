namespace Network.Requests
{
    public class DisconnectRequest:BasicRequest
    {

        public DisconnectRequest()
        {
            requestType = RequestType.DisconnectRequest;
        }
    }
}