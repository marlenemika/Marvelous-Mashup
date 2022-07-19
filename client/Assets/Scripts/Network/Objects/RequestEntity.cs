namespace Network.Objects
{
    public class RequestEntity
    {
        public string entityID;
        public int ID;

        public RequestEntity(string entityID, int ID)
        {
            this.entityID = entityID;
            this.ID = ID;
        }
    }
}
