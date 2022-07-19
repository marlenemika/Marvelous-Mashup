namespace Network.Messages
{
    public class CharacterSelection : BasicMessage
    {
        public bool[] characters;

        public CharacterSelection(bool[] characters)
        {
            messageType = MessageType.CHARACTER_SELECTION;

            this.characters = characters;
        }

        public CharacterSelection()
        {
        }
    }
}
