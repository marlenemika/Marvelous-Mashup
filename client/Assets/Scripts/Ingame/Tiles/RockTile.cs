using UnityEngine;
using Vector2 = System.Numerics.Vector2;

namespace Ingame.Tiles
{
    public class RockTile : GridObject
    {
        [SerializeField] private GameObject _highlight;
        private Vector2 _positionInGrid = new Vector2(0, 0);

        public void Init(int x, int y)
        {
            _positionInGrid = new Vector2(x, y);
        }
        
        private void OnMouseEnter()
        {
            _highlight.SetActive(true);
        }

        private void OnMouseExit()
        {
            _highlight.SetActive(false);
        }
    }
}