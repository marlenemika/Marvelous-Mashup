using System;
using System.Linq;
using Network.Requests;
using Objects;
using UnityEngine;

namespace Ingame
{
    public class ActionPlate : MonoBehaviour
    {
        // Whether action plate is used for melee, ranged or infinity stone
        public string _actionId;
        // position from active character on grid
        public int[] _originPosition;
        // position of action plate on grid
        public int[] _position;

        public ActionPlate(string actionId, int[] originPosition, int[] position)
        {
            _actionId = actionId;
            _originPosition = originPosition;
            _position = position;
        }
        
        /// <summary>
        /// When the actionPlate gets pressed call the corresponding request method from gamecontroller
        /// </summary>
        private void OnMouseDown()
        {
            Debug.Log(_originPosition + " nutzt " + _actionId + " auf [" + _position[0] + ", " + _position[1] + "]");

            var gameController = GameObject.Find("Ingame").GetComponent<GameController>();
            
            // deactivates infinity stone inventory after executing an action
            gameController.infinityStoneInventory.SetActive(false);

            if (_actionId.Equals(RequestType.MoveRequest))
            {
                gameController.Move(_position);
            }
            else if (_actionId.Equals(RequestType.MeleeAttackRequest))
            {
                gameController.MeleeAttack(_position);
            } 
            else if (_actionId.Equals(RequestType.RangedAttackRequest))
            {
                gameController.RangeAttack(_position);
            }
            else if (_actionId.Equals(RequestType.UseInfinityStoneRequest))
            {
                gameController.UseInfinityStone(_position);
            }
            else if (_actionId.Equals(RequestType.ExchangeInfinityStoneRequest))
            {
                gameController.ExchangeInfinityStone(_position);
            }
        }
    }
}