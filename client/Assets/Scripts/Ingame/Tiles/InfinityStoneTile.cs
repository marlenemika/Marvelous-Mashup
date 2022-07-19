using UnityEngine;
using Vector2 = System.Numerics.Vector2;

namespace Ingame.Tiles
{
    public class InfinityStoneTile : GridObject
    {
        [SerializeField] private Sprite spaceStone;
        [SerializeField] private Sprite mindStone;
        [SerializeField] private Sprite realityStone;
        [SerializeField] private Sprite powerStone;
        [SerializeField] private Sprite timeStone;
        [SerializeField] private Sprite soulStone;

        private Vector2 _positionInGrid = new Vector2(0, 0);
        private int _type;

        public void Init(int x, int y, int type)
        {
            _positionInGrid = new Vector2(x, y);
            _type = type;

            GetComponent<SpriteRenderer>().sprite = _type switch
            {
                0 => spaceStone,
                1 => mindStone,
                2 => realityStone,
                3 => powerStone,
                4 => timeStone,
                5 => soulStone,
                _ => spaceStone
            };
        }
    }
}