namespace AiClient.Network.Messages
{
    public class ConfirmSelection : BasicMessage
    {
        public bool selectionComplete;

        public ConfirmSelection(bool selectionComplete)
        {
            messageType = MessageType.CONFIRM_SELECTION;

            this.selectionComplete = selectionComplete;
        }

        public ConfirmSelection()
        {
        }
    }
}