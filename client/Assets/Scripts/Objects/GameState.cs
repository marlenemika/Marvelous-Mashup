using System;
using System.Linq;
using Network.Events.Gamestate;
using Network.Messages;
using Network.Objects;
using Statistics;
using Network.Requests;
using UnityEngine;

namespace Objects
{
    [CreateAssetMenu(fileName = "GameState", menuName = "ScriptableObjects/GameState")]
    public class GameState : ScriptableObject
    {
        public StatisticsObject statisticsObject;

        // GameState
        public EventEntity[] entities;
        public int[] mapSize;
        public RequestEntity[] turnOrder;
        public EventEntity activeCharacter;
        public int[] stoneCooldowns;
        public bool winCondition;

        // GameStructure
        public string assignment;
        public string playerOneName;
        public Character[] playerOneCharacters;
        public string playerTwoName;
        public Character[] playerTwoCharacters;
        public MatchConfig matchConfig;
        public ScenarioConfig scenarioConfig;

        // CharacterSelection
        public Character[] playerCharacterAssignment;

        public void UpdateGameState(GamestateEvent gsEvent)
        {
            entities = gsEvent.entities;
            mapSize = gsEvent.mapSize;
            turnOrder = gsEvent.turnOrder;

            foreach (var e in entities)
            {
                if (e.entityType == EventEntityType.Character && e.ID == gsEvent.activeCharacter.ID)
                    activeCharacter = e;
            }
            
            stoneCooldowns = gsEvent.stoneCooldowns;
            winCondition = gsEvent.winCondition;
        }

        public void UpdateGameState(GameStructure gsEvent)
        {
            var scenario = gsEvent.scenarioconfig.scenario;
            mapSize = new[] { scenario.GetLength(0), scenario.GetLength(1) };

            statisticsObject.playerOne = gsEvent.playerOneName;
            statisticsObject.playerTwo = gsEvent.playerTwoName;

            assignment = gsEvent.assignment;
            playerOneName = gsEvent.playerOneName;
            playerOneCharacters = gsEvent.playerOneCharacters;
            playerTwoName = gsEvent.playerTwoName;
            playerTwoCharacters = gsEvent.playerTwoCharacters;
            matchConfig = gsEvent.matchconfig;
            scenarioConfig = gsEvent.scenarioconfig;
        }

        public bool IsMyTurn()
        {
            return activeCharacter.PID == 1 && assignment.Equals("PlayerOne")
                || activeCharacter.PID == 2 && assignment.Equals("PlayerTwo");
        }

        public EventEntity FindEntityAt(int[] position)
        {
            return entities.FirstOrDefault(entity =>
                entity.position[0] == position[0] && entity.position[1] == position[1]); // Equals(position));
        }

        public Character FindCharacterWithId(int id)
        {
            return playerOneCharacters.Concat(playerTwoCharacters)
                .FirstOrDefault(character => character.characterID == id);
        }

        public EventEntity FindCharacterEntityWithId(int id)
        {
            return entities
                .Where(entity => entity.entityType == EventEntityType.Character)
                .FirstOrDefault(entity => entity.ID == id);
        }

        public EventEntity FindEntityWithPosition(int[] position)
        {
            return entities.FirstOrDefault(entity => entity.position.SequenceEqual(position));
        }

        public void UpdateActiveCharacter(RequestEntity requestEntity)
        {
            foreach (var entity in entities)
            {
                if (entity.entityType == EventEntityType.Character && entity.ID == requestEntity.ID)
                    activeCharacter = entity;
            }
        }

        public void DealDamage(EventEntity entity, int amount)
        {
            foreach (var e in entities)
            {
                if (e.entityType == EventEntityType.Character && e.ID == entity.ID)
                {
                    e.HP -= amount;
                    return;    
                }
            }
        }

        public void Heal(EventEntity entity, int amount)
        {
            foreach (var e in entities)
            {
                if (e.entityType == EventEntityType.Character && e.ID == entity.ID)
                {
                    e.HP += amount;
                    return;    
                }
            }
        }

        public void CountMeleeAttacks(int id)
        {
            switch (FindCharacterEntityWithId(id).PID)
            {
                case 1:
                    statisticsObject.meleeAttacks1 += 1;
                    break;
                case 2:
                    statisticsObject.meleeAttacks2 += 1;
                    break;
            };
        }
        
        public void CountRangeAttacks(int id)
        {
            switch (FindCharacterEntityWithId(id).PID)
            {
                case 1:
                    statisticsObject.rangeAttacks1 += 1;
                    break;
                case 2:
                    statisticsObject.rangeAttacks2 += 1;
                    break;
            };
        }

        public void CountActions(int id)
        {
            switch (FindCharacterEntityWithId(id).PID)
            {
                case 1:
                    statisticsObject.actionsPerformed1 += 1;
                    break;
                case 2:
                    statisticsObject.actionsPerformed2 += 1;
                    break;
            };
        }

        public void CountInfinityStoneUsed(int id)
        {
            switch (FindCharacterEntityWithId(id).PID)
            {
                case 1:
                    statisticsObject.infinityStonesUsed1 += 1;
                    break;
                case 2:
                    statisticsObject.infinityStonesUsed2 += 1;
                    break;
            };
        }

        public void CountDamageTaken(int id, int amount)
        {
            switch (FindCharacterEntityWithId(id).PID)
            {
                case 1:
                    statisticsObject.takenDamage1 += amount;
                    break;
                case 2:
                    statisticsObject.takenDamage2 += amount;
                    break;
            };
        }

        public void SetWinner(int playerWon)
        {
            statisticsObject.playerWon = playerWon;
        }
    }
}
