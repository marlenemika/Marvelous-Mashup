using AiClient.Network.Objects;

namespace AiClient.Network.Requests
{
    public class MeleeAttackRequest:BasicRequest
    {
        public RequestEntity originEntity;
        public RequestEntity targetEntity;
        public int[] originField; 
        public int[] targetField; 
        public int value;
        
        public MeleeAttackRequest(RequestEntity originEntity, RequestEntity targetEntity, int[] originField, int[] targetField, int value)
        { 
            requestType = RequestType.MeleeAttackRequest;
                    
            this.originEntity = originEntity;
            this.targetEntity = targetEntity;
            this.originField = originField;
            this.targetField = targetField;
            this.value = value;
        }
    }
}