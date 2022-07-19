using System;

namespace AiClient.Network.Events.Notification
{
    public class CustomEvent : BasicEvent
    {
        public string teamIdentifier;
        public Object customContent;
        
        public CustomEvent(string teamIdentifier, object customContent)
        {
            eventType = EventType.CustomEvent;
            this.teamIdentifier = teamIdentifier;
            this.customContent = customContent;
        }

        public CustomEvent()
        {
        }
    }
}