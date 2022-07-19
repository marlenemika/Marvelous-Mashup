using AiClient.Network.Objects;

namespace AiClient.Network.Requests
{
    public class MoveRequest:BasicRequest
    {
        public RequestEntity originEntity;
        public int[] originField;
        public int[] targetField;

        public MoveRequest(RequestEntity originEntity, int[] originField, int[] targetField)
        {
            requestType = RequestType.MoveRequest;
            
            this.originEntity = originEntity;
            this.originField = originField;
            this.targetField = targetField;
        }
    }
}