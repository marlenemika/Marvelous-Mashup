namespace Network.Objects
{
    public class EventEntity
    {
        public EventEntityType entityType;
        public int PID;
        public int ID;
        public string name;
        public int HP;
        public int MP;
        public int AP;
        public int[] stones;
        public int[] position;

        public EventEntity(EventEntityType entityType)
        {
            this.entityType = entityType;
        }

        public RequestEntity ToRequestEntity()
        {
            return new RequestEntity(
                PID switch
                {
                    1 => RequestEntityType.P1,
                    2 => RequestEntityType.P2,
                    _ => RequestEntityType.NPC
                }, ID);
        }
    }
}
