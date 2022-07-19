using System;
using System.Collections.Generic;
using System.Linq;
using Network.Objects;
using Network.Requests;
using Objects;
using UnityEngine;

namespace Ingame
{
    public class ActionPlateManager : MonoBehaviour
    {
        [SerializeField] private ActionPlate actionPlate;
        public GameState gameState;
        private List<ActionPlate> _actionPlates = new List<ActionPlate>();
        
        

        /// <summary>
        ///     Creates action plates for movement for a character according to its MP.
        /// </summary>
        /// <param name="character"></param>
        public void GenerateActionPlatesForMove(EventEntity character)
        {
            Debug.Log("GenerateActionPlatesForMove");

            // 0 = x, 1 = y
            int[] up = {character.position[0], character.position[1]+1};
            int[] down = {character.position[0], character.position[1]-1};
            int[] left = {character.position[0]-1, character.position[1]};
            int[] right = {character.position[0]+1, character.position[1]};
            int[] upleft = {character.position[0]-1, character.position[1]+1};
            int[] upright = {character.position[0]+1, character.position[1]+1};
            int[] downleft = {character.position[0]-1, character.position[1]-1};
            int[] downright = {character.position[0]+1, character.position[1]-1};
            int[][] next = {up, down, left, right, upleft, upright, downleft, downright};

            if(character.MP>0)
                foreach(var t in next)
                    if(IsValidMove(t))
                        _actionPlates.Add(instantiateActionPlate(RequestType.MoveRequest, character.position, t));
        }

        /// <summary>
        ///     Creates action plates for attacks for a character according to its AP.
        /// </summary>
        /// <param name="character"></param>
        public void GenerateActionPlatesForAttack(EventEntity character)
        {
            if(character.AP>0)
            {
                // melee
                MeleeAttack(character);

                // ranged
                RangedAttack(character, true);
            }
        }

        /// <summary>
        /// Generates Action Plates for Infinity Stone (IS)
        /// </summary>
        /// <param name="character">selected character</param>
        /// <param name="use">true if IS is used, false if exchanged</param>
        /// <param name="infinityStoneType">type of IS (only if use is true)</param>
        public void GenerateActionPlatesForInfinityStone(EventEntity character, bool use, int infinityStoneType)
        {
            if(use)
            {
                GenerateActionPlatesForInfinityStoneUse(character, infinityStoneType);
            }
            else
            {
                GenerateActionPlatesForInfinityStoneExchange(character);
            }
        }

        /// <summary>
        ///     Generates Action Plates for Infinity Stone Use according to the Infinity Stone
        /// </summary>
        /// <param name="character"></param>
        /// <param name="infinityStoneType"></param>
        public void GenerateActionPlatesForInfinityStoneUse(EventEntity character, int infinityStoneType)
        {
            
            int[] up = {character.position[0], character.position[1]+1};
            int[] down = {character.position[0], character.position[1]-1};
            int[] left = {character.position[0]-1, character.position[1]};
            int[] right = {character.position[0]+1, character.position[1]};
            int[] upleft = {character.position[0]-1, character.position[1]+1};
            int[] upright = {character.position[0]+1, character.position[1]+1};
            int[] downleft = {character.position[0]-1, character.position[1]-1};
            int[] downright = {character.position[0]+1, character.position[1]-1};
            int[][] next = {up, down, left, right, upleft, upright, downleft, downright};

            switch(infinityStoneType)
            {
                // blue
                case 0:
                    for(var i = 0; i<gameState.mapSize[0]; i++)
                    {
                        for(var j = 0; j<gameState.mapSize[1]; j++)
                        {
                            int[] ent = {i,j};
                            int[] stuff = {i, j, 1};
                            
                            var checkEnt = gameState.FindEntityWithPosition(ent);
                            var checkStuff = gameState.FindEntityWithPosition(stuff);
                            if(checkEnt==null || checkStuff==null)
                            {
                                _actionPlates.Add(instantiateActionPlate(RequestType.UseInfinityStoneRequest, character.position,
                                    ent));
                            }
                        }
                    }
                    break;
                // yellow
                case 1:
                    RangedAttack(character, false);
                    break;
                // red
                case 2:
                    foreach(var t in next)
                        if(!IsOutOfField(t)&&!IsNPC(t)&&!IsTeammate(t))
                            _actionPlates.Add(instantiateActionPlate(RequestType.UseInfinityStoneRequest, character.position,
                                t));
                    break;
                // purple
                case 3:
                    foreach(var t in next)
                        if(IsHittable(t))
                            _actionPlates.Add(instantiateActionPlate(RequestType.UseInfinityStoneRequest, character.position,
                                t));
                    break;
                // green
                case 4:
                    _actionPlates.Add(instantiateActionPlate(RequestType.UseInfinityStoneRequest, character.position,
                        character.position));
                    break;
                // orange
                case 5:
                    foreach(var t in next)
                        if(IsTeammate(t))
                            _actionPlates.Add(instantiateActionPlate(RequestType.UseInfinityStoneRequest, character.position,
                                t));
                    break;
                
            }
        }

        /// <summary>
        ///     Generates action plates for Infinity Stone Exchange. Teammate must be next to selected character.
        /// </summary>
        /// <param name="character">selected character</param>
        public void GenerateActionPlatesForInfinityStoneExchange(EventEntity character)
        {
            int[] up = {character.position[0], character.position[1]+1};
            int[] down = {character.position[0], character.position[1]-1};
            int[] left = {character.position[0]-1, character.position[1]};
            int[] right = {character.position[0]+1, character.position[1]};
            int[] upleft = {character.position[0]-1, character.position[1]+1};
            int[] upright = {character.position[0]+1, character.position[1]+1};
            int[] downleft = {character.position[0]-1, character.position[1]-1};
            int[] downright = {character.position[0]+1, character.position[1]-1};
            int[][] next = {up, down, left, right, upleft, upright, downleft, downright};

            foreach(var t in next)
                if(IsTeammate(t))
                    _actionPlates.Add(instantiateActionPlate(RequestType.ExchangeInfinityStoneRequest, character.position,
                        t));
        }
        
        // helper function for GenerateActionPlatesForAttack 
        private void MeleeAttack(EventEntity character)
        {
            int[] up = {character.position[0], character.position[1]+1};
            int[] down = {character.position[0], character.position[1]-1};
            int[] left = {character.position[0]-1, character.position[1]};
            int[] right = {character.position[0]+1, character.position[1]};
            int[] upleft = {character.position[0]-1, character.position[1]+1};
            int[] upright = {character.position[0]+1, character.position[1]+1};
            int[] downleft = {character.position[0]-1, character.position[1]-1};
            int[] downright = {character.position[0]+1, character.position[1]-1};
            int[][] next = {up, down, left, right, upleft, upright, downleft, downright};

            foreach(var t in next)
                if(IsHittable(t))
                    _actionPlates.Add(instantiateActionPlate(RequestType.MeleeAttackRequest, character.position,
                        t));
        }
        
        // helper function for GenerateActionPlatesForAttack
        private void RangedAttack(EventEntity character, bool type)
        {
            // count distincts it from a melee attack
            const int distance = 1;
            RangedAttack(character, DirectionsEnum.UP, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.DOWN, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.LEFT, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.RIGHT, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.UPLEFT, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.UPRIGHT, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.DOWNLEFT, character.position, distance, type);
            RangedAttack(character, DirectionsEnum.DOWNRIGHT, character.position, distance, type);
        }
        
        // recursive function for RangedAttack
        private void RangedAttack(EventEntity character, DirectionsEnum dir, int[] position, int distance, bool type)
        {
            // Break when theres already a melee attack in direction or a team mate blocks the way
            if (IsHittable(position) && distance == 2 || IsTeammate(position) && distance > 1) return;
            
            if(IsHittable(position)&&distance>=3)
            {
                if(type)
                {
                    _actionPlates.Add(instantiateActionPlate(RequestType.RangedAttackRequest, character.position,
                        position));
                }
                else
                {
                    _actionPlates.Add(instantiateActionPlate(RequestType.UseInfinityStoneRequest, character.position,
                        position));
                }

            }
            else
            {
                // Break when position is out of field or a rock blocks the way
                if(IsOutOfField(position) || IsRock(position)) return;
                
                // a ranged attack is defined as an attack only if distance is equal or greater than 2
                distance++;
                switch(dir)
                {
                    case DirectionsEnum.UP:
                        int[] up = {position[0], position[1]+1};
                        RangedAttack(character, DirectionsEnum.UP, up, distance, type);
                        break;
                    case DirectionsEnum.DOWN:
                        int[] down = {position[0], position[1]-1};
                        RangedAttack(character, DirectionsEnum.DOWN, down, distance, type);
                        break;
                    case DirectionsEnum.LEFT:
                        int[] left = {position[0]-1, position[1]};
                        RangedAttack(character, DirectionsEnum.LEFT, left, distance,type);
                        break;
                    case DirectionsEnum.RIGHT:
                        int[] right = {position[0]+1, position[1]};
                        RangedAttack(character, DirectionsEnum.RIGHT, right, distance, type);
                        break;
                    case DirectionsEnum.UPLEFT:
                        int[] upleft = {position[0]-1, position[1]+1};
                        RangedAttack(character, DirectionsEnum.UPLEFT, upleft, distance, type);
                        break;
                    case DirectionsEnum.UPRIGHT:
                        int[] upright = {position[0]+1, position[1]+1};
                        RangedAttack(character, DirectionsEnum.UPRIGHT, upright, distance, type);
                        break;
                    case DirectionsEnum.DOWNLEFT:
                        int[] downleft = {position[0]-1, position[1]-1};
                        RangedAttack(character, DirectionsEnum.DOWNLEFT, downleft, distance, type);
                        break;
                    case DirectionsEnum.DOWNRIGHT:
                        int[] downright = {position[0]+1, position[1]-1};
                        RangedAttack(character, DirectionsEnum.DOWNRIGHT, downright, distance, type);
                        break;
                    default:
                        throw new ArgumentOutOfRangeException(nameof(dir), dir, null);
                }
            }
        }

        /// <summary>
        /// Checks if you can move to the given position. You can move if it the position is free and in the field.
        /// </summary>
        /// <param name="position">position you want to move to</param>
        /// <returns>true</returns>
        private bool IsValidMove(int[] position)
        {
            return !(IsRock(position)||IsOutOfField(position));
        }

        /// <summary>
        /// Checks if given position is in the field.
        /// </summary>
        /// <param name="position"></param>
        /// <returns>true</returns>
        private bool IsOutOfField(int[] position)
        {
            return position[0]<0||position[1]<0||position[0]>gameState.mapSize[0]-1||position[1]>gameState.mapSize[1]-1;
        }

        /// <summary>
        /// Checks if there is a rock at the given position.
        /// </summary>
        /// <param name="position"></param>
        /// <returns>true</returns>
        private bool IsRock(int[] position)
        {
            var entity = gameState.FindEntityWithPosition(position);
            if(entity==null) return false;
            return entity.entityType==EventEntityType.Rock;
        }

        /// <summary>
        /// Checks if entity at given position is hittable i.e. is
        /// in the field and is an NPC or a rock.
        /// </summary>
        /// <param name="position"></param>
        /// <returns>true</returns>
        private bool IsHittable(int[] position)
        {
           return (!IsOutOfField(position))&&(IsNPC(position))&&(!IsKO(position));
        }

        public bool IsKO(int[] position)
        {
            return gameState.FindEntityWithPosition(position).HP <= 0;
        }

        /// <summary>
        /// Checks if entity at given position is an NPC.
        /// </summary>
        /// <param name="position"></param>
        /// <returns>true</returns>
        private bool IsNPC(int[] position)
        {
            var character = gameState.FindEntityWithPosition(position);

            if(!(character is {entityType: EventEntityType.Character})) return false;
            Debug.Log(position[0]+", "+position[1]+" is a character");
            return !(character.PID==1&&gameState.assignment.Equals("PlayerOne")
                     ||character.PID==2&&gameState.assignment.Equals("PlayerTwo"));
        }

        /// <summary>
        /// Checks if entity at given position is a teammate.
        /// </summary>
        /// <param name="position"></param>
        /// <returns>true</returns>
        private bool IsTeammate(int[] position)
        {
            var character = gameState.FindEntityWithPosition(position);

            if(!(character is {entityType: EventEntityType.Character})) return false;
            Debug.Log(position[0]+", "+position[1]+" is a character");
            return (character.PID==1&&gameState.assignment.Equals("PlayerOne")
                     ||character.PID==2&&gameState.assignment.Equals("PlayerTwo"));
        }

        private ActionPlate instantiateActionPlate(string actionId, int[] originPosition, int[] position)
        {
            Debug.Log("InstantiateActionPlate");
            var actionPlateGameObject = Instantiate(
                actionPlate,
                new Vector3(position[0], position[1], 0),
                Quaternion.identity);
            actionPlateGameObject._actionId = actionId;
            actionPlateGameObject._originPosition = originPosition;
            actionPlateGameObject._position = position;
            actionPlateGameObject.name = "actionPlate"+actionId;
            actionPlateGameObject.GetComponent<SpriteRenderer>().sortingLayerName = "UI";
            return actionPlateGameObject;
        }

        public void DestroyActionPlates()
        {
            foreach(var plate in _actionPlates) Destroy(plate.gameObject);
            _actionPlates = new List<ActionPlate>();
        }
    }
}