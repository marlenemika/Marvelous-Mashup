using System;
using System.Collections.Generic;
using System.Linq;
using AiClient.Network.Events.Gamestate;
using AiClient.Network.Objects;

namespace AiClient
{
    public class GameState
    {
        public int ApLeft;
        public GameObject[,] board;
        public int boardLength;
        public int boardWidth;
        public List<SuperheroObject> characterOrder;
        public SuperheroObject currentCharacter;
        public List<GameObject> entities;
        public bool isOvertime;
        public int MpLeft;
        public int playerLocal;
        public int playerOpponent;

        public GameState(int[] dimensions)
        {
            boardLength = dimensions[0];
            boardWidth = dimensions[1];
            board = new GameObject[boardLength, boardWidth];
        }
/*
        public GameState(int[] dimensions, Player playerLocal, Player playerOpponent, List<SuperheroObject> characterOrder, SuperheroObject currentCharacter, bool isOvertime, List<GameObject> entitys)
        {
            this.playerLocal = playerLocal;
            this.playerOpponent = playerOpponent;
            this.characterOrder = characterOrder;
            this.currentCharacter = currentCharacter;
            this.isOvertime = isOvertime;
            this.entitys = entitys;
            boardLength = dimensions[0];
            boardWidth = dimensions[1];
            board = new GameObject[boardLength, boardWidth];
            MpLeft = currentCharacter.movementPoints;
            ApLeft = currentCharacter.actionPoints;
            board[currentCharacter.position.x, currentCharacter.position.y] = currentCharacter;
            foreach (var obj in entitys)
            {
                if (obj.GetType().Equals(typeof(SuperheroObject)))
                {
                    var objCast = (SuperheroObject)obj;
                    board[objCast.position.x, objCast.position.y] = objCast;
                }else if (obj.GetType().Equals(typeof(RockObject)))
                {
                    var objCast = (RockObject)obj;
                    board[objCast.position.x, objCast.position.y] = objCast;
                }else if (obj.GetType().Equals(typeof(InfinityStoneObject)))
                {
                    var objCast = (InfinityStoneObject)obj;
                    board[objCast.position.x, objCast.position.y] = objCast;
                }
            }
        }*/

        public GameState(int[] dimensions, int playerLocal, int playerOpponent,
            List<SuperheroObject> characterOrder, SuperheroObject currentCharacter, bool isOvertime,
            List<GameObject> entities, int mpLeft, int apLeft)
        {
            this.playerLocal = playerLocal;
            this.playerOpponent = playerOpponent;
            this.characterOrder = characterOrder;
            this.currentCharacter = currentCharacter;
            this.isOvertime = isOvertime;
            boardLength = dimensions[0];
            boardWidth = dimensions[1];
            board = new GameObject[boardLength, boardWidth];
            MpLeft = mpLeft;
            ApLeft = apLeft;
            this.entities = entities;
            foreach (var obj in entities)
                board[obj.position.x, obj.position.y] = obj;
        }
        
        public GameState(GamestateEvent gamestateEvent, int playerLocal, int playerOpponent, List<Character> characters)
        {
            try
            {
                this.playerLocal = playerLocal;
                this.playerOpponent = playerOpponent;
                boardLength = gamestateEvent.mapSize[0];
                boardWidth = gamestateEvent.mapSize[1];
                board = new GameObject[boardLength, boardWidth];
                entities = new List<GameObject>();
                int activeCharacterPID;
                if (gamestateEvent.activeCharacter.entityID.Equals("P1"))
                {
                    activeCharacterPID = 1;
                }
                else
                {
                    activeCharacterPID = 2;
                }
                ApLeft = gamestateEvent.entities.ToList().Find(x =>
                    x.ID.Equals(gamestateEvent.activeCharacter.ID) &&
                    x.entityType.Equals(EventEntityType.Character) &&
                    x.PID==activeCharacterPID).AP;
                MpLeft = gamestateEvent.entities.ToList().Find(x =>
                    x.ID.Equals(gamestateEvent.activeCharacter.ID) &&
                    x.entityType.Equals(EventEntityType.Character) &&
                    x.PID==activeCharacterPID).MP;
                foreach (var entity in gamestateEvent.entities)
                {
                    //MyLogger.GetInstance().Debug("Entities: EID:" + entity.entityType + " ,ID:" + entity.ID);
                    switch (entity.entityType)
                    {
                        case EventEntityType.Character:
                            var superhero = new SuperheroObject(entity, characters);
                            //MyLogger.GetInstance().Debug("PID:" + superhero.PID);
                            entities.Add(superhero);
                            if (superhero.ID == gamestateEvent.activeCharacter.ID)
                            {
                                currentCharacter = superhero;
                            }
                            break;
                        case EventEntityType.Rock:
                            entities.Add(new RockObject(entity));
                            break;
                        case EventEntityType.InfinityStone:
                            entities.Add(new InfinityStoneObject(entity, gamestateEvent.stoneCooldowns));
                            break;
                        case EventEntityType.Portal:
                            entities.Add(new Portal(entity));
                            break;
                        case EventEntityType.NPC:
                            entities.Add(new NPC(entity));
                            break;
                    }
                }
                foreach (var obj in entities)
                    board[obj.position.x, obj.position.y] = obj;
                characterOrder = new List<SuperheroObject>();
                int i = -1;
                i = gamestateEvent.turnOrder.ToList().FindIndex(x => x.ID.Equals(currentCharacter.ID));
                i++;
                //MyLogger.GetInstance().Debug("i:" + i);
                for(; i<gamestateEvent.turnOrder.Length; i++)
                {
                    //MyLogger.GetInstance().Debug("EID:" + gamestateEvent.turnOrder[i].entityID + " ,ID:" + gamestateEvent.turnOrder[i].ID);
                    if (gamestateEvent.turnOrder[i].entityID.Equals("P1"))
                    {
                        //MyLogger.GetInstance().Debug(entities[0].entityType==null);
                        //MyLogger.GetInstance().Debug(entities.Find(x => x.entityType.Equals("Character") && x.ID==gamestateEvent.turnOrder[i].ID && ((SuperheroObject)x).PID.Equals(1)));
                        characterOrder.Add((SuperheroObject) entities.Find(x => x.entityType.Equals("Character") && x.ID==gamestateEvent.turnOrder[i].ID && ((SuperheroObject)x).PID.Equals(1)));
                    } else if (gamestateEvent.turnOrder[i].entityID.Equals("P2"))
                    {
                        //MyLogger.GetInstance().Debug(entities[0].entityType==null);
                        //MyLogger.GetInstance().Debug(entities.Find(x => x.entityType.Equals("Character") && x.ID==gamestateEvent.turnOrder[i].ID && ((SuperheroObject)x).PID.Equals(1)));
                        characterOrder.Add((SuperheroObject) entities.Find(x => x.entityType.Equals("Character") && x.ID==gamestateEvent.turnOrder[i].ID && ((SuperheroObject)x).PID.Equals(2)));
                    } else if (gamestateEvent.turnOrder[i].entityID.Equals("NPC"))
                    {
                        //todo: NPC beachten
                    }
                }
                //MyLogger.GetInstance().Debug("characterOrder has length:" + characterOrder.Count);

            }
            catch (Exception e)
            {
                //MyLogger.GetInstance().Debug(e);
                throw;
            }
        }

        public bool IsFinished()
        {
            foreach (var gameObject in entities)
                if (gameObject.GetType() == typeof(SuperheroObject))
                {
                    var superhero = (SuperheroObject) gameObject;
                    if (superhero.inventory.Count == 6) return true;
                }

            return false;
        }

        public GameState DeapCopy()
        {
            //MyLogger.GetInstance().Debug("GameState.DeapCopy entered");
            var copyEntitys = new List<GameObject>();
            //MyLogger.GetInstance().Debug(2.1);
            foreach (var obj in entities) copyEntitys.Add(obj.DeapCopy());
            var copyCharacterOrder = new List<SuperheroObject>();
            //MyLogger.GetInstance().Debug(2.2);
            //MyLogger.GetInstance().Debug(characterOrder.Count);
            foreach (var character in characterOrder)
            {
                //MyLogger.GetInstance().Debug("Entered foreach");
                //MyLogger.GetInstance().Debug(character == null);
                copyCharacterOrder.Add((SuperheroObject) (copyEntitys.Find(x => character.Equals(x))));
            }

            //MyLogger.GetInstance().Debug(2.3);
            var copyCurrentCharacter = (SuperheroObject) copyEntitys.Find(x => currentCharacter.Equals(x));
            //MyLogger.GetInstance().Debug(2.4);
            var ret = new GameState(new[] {boardLength, boardWidth}, playerLocal, playerOpponent, copyCharacterOrder,
                copyCurrentCharacter, isOvertime, copyEntitys, MpLeft, ApLeft);
            //MyLogger.GetInstance().Debug("GameState.DeapCopy finished");
            return ret;
        }

        public bool IsBetter(GameState resultingState, int forPlayer)
        {
            for (var i = 0; i < boardLength; i++)
            for (var j = 0; j < boardWidth; j++)
            {
                if (board[i, j] == resultingState.board[i, j]) continue;
                if (board[i, j] == null) return false;
                if (resultingState.board[i, j] == null) return false;
                if (board[i, j].GetType().Equals(typeof(SuperheroObject)))
                {
                    var objCast = (SuperheroObject) board[i, j];
                    var resultObj = (SuperheroObject) resultingState.board[i, j];

                    if (resultObj.name.Equals(objCast.name))
                    {
                        if (!objCast.PID.Equals(forPlayer))
                        {
                            if (resultObj.healthPoints < objCast.healthPoints) return false;
                        }
                        else
                        {
                            if (resultObj.healthPoints > objCast.healthPoints) return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                else if (board[i, j].GetType().Equals(typeof(RockObject)))
                {
                    var objCast = (RockObject) board[i, j];
                    var resultObj = (RockObject) resultingState.board[i, j];
                    if (objCast.healthPoints > resultObj.healthPoints) return false;
                }
                else if (board[i, j].GetType().Equals(typeof(InfinityStoneObject)))
                {
                }
            }

            return true;
        }
    }

    /*
    public class Player
    {
        private string username;
        public bool isHuman;
        public bool isLocal;
        public List<SuperheroObject> superheros;

        public Player(string username, bool isHuman, bool isLocal, List<SuperheroObject> superheros)
        {
            this.username = username;
            this.isHuman = isHuman;
            this.isLocal = isLocal;
            this.superheros = superheros;
        }
    }*/
}