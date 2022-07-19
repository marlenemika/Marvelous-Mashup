namespace AiClient.Network.Requests
{
    public class PauseStartRequest:BasicRequest
    {
        
        public PauseStartRequest() 
        { 
            requestType = RequestType.PauseStartRequest;
        }
    }
}