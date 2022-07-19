using Network.Objects;

namespace Network.Requests
{
    public class ExchangeInfinityStoneRequest:BasicRequest
    {
        public RequestEntity originEntity;
        public RequestEntity targetEntity;
        public int[] originField;
        public int[] targetField;
        public RequestEntity stoneType;

        public ExchangeInfinityStoneRequest(RequestEntity originEntity, RequestEntity targetEntity, int[] originField, int[] targetField, RequestEntity stoneType)
        {
            requestType = RequestType.ExchangeInfinityStoneRequest;
            
            this.originEntity = originEntity;
            this.targetEntity = targetEntity;
            this.originField = originField;
            this.targetField = targetField;
            this.stoneType = stoneType;
        }
    }
}