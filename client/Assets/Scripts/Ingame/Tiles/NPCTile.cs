namespace Ingame.Tiles
{
    public class NPCTile : GridObject
    {
        private int[] _positionInGrid = new int[]{0,0};

        public void Init(int x, int y)
        {
            _positionInGrid = new int[]{x,y};
        }
    }
}