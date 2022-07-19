using System.Numerics;

namespace Ingame.Tiles
{
    public class PortalTile : GridObject
    {
        private Vector2 _positionInGrid = new Vector2(0, 0);

        public void Init(int x, int y)
        {
            _positionInGrid = new Vector2(x, y);
        }
    }
}