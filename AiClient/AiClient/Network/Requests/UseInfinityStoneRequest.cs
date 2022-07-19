using AiClient.Network.Objects;

namespace AiClient.Network.Requests
{
    public class UseInfinityStoneRequest:BasicRequest
    {
        public RequestEntity originEntity; 
        public int[] originField; 
        public int[] targetField;
        public RequestEntity stoneType;

        public UseInfinityStoneRequest(RequestEntity originEntity, int[] originField, int[] targetField, RequestEntity stoneType)
        {
            requestType = RequestType.UseInfinityStoneRequest;
            
            this.originEntity = originEntity;
            this.originField = originField;
            this.targetField = targetField;
            this.stoneType = stoneType;
        }
    }
}