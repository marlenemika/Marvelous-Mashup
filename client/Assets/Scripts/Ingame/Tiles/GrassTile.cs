using Network.Requests;
using Objects;
using UnityEngine;

namespace Ingame.Tiles
{
    public class GrassTile : GridObject
    {
        [SerializeField] private GameObject _highlight;
        public GameState gameState;
        private int[] _positionInGrid = new int[]{0,0};

        public void Init(int x, int y)
        {
            _positionInGrid = new int[]{x,y};
        }

        /*
        private void OnMouseEnter()
        {
            _highlight.SetActive(true);
        }

        private void OnMouseExit()
        {
            _highlight.SetActive(false);
        }

        /*private void OnMouseDown()
        {
            lock (gameState)
            {
                if (gameState.currentPlayerActionType == null) return;

                // Just create Requests when its your turn
                if (gameState.activeCharacter.PID == 1 && gameState.assignment.Equals("PlayerOne")
                    || gameState.activeCharacter.PID == 2 && gameState.assignment.Equals("PlayerTwo"))
                {
                    Debug.Log(gameState.currentPlayerActionType + ", " + _positionInGrid);

                    var gameController = GameObject.Find("UI").GetComponent<GameController>();

                    if (gameState.currentPlayerActionType.Equals(RequestType.MoveRequest))
                    {
                        gameController.Move(_positionInGrid);
                    }
                    else if (gameState.currentPlayerActionType.Equals(RequestType.MeleeAttackRequest))
                    {
                        gameController.MeleeAttack(_positionInGrid);
                    }
                    else if (gameState.currentPlayerActionType.Equals(RequestType.RangedAttackRequest))
                    {
                        gameController.RangeAttack(_positionInGrid);
                    }
                    else if (gameState.currentPlayerActionType.Equals(RequestType.UseInfinityStoneRequest))
                    {
                        gameController.UseInfinityStone(_positionInGrid);
                    }
                }
            }
        }*/
    }
}
