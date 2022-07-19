using System;
using AiClient.Utility;
using NLog;

namespace AiClient
{
    public abstract class GameAction
    {
        public abstract GameState ResultingState(GameState gameState);
        public abstract string GetServerMessage();
        public abstract GameAction DeapCopy();
        public abstract bool Equals(GameAction action);
    }

    public class Move : GameAction
    {
        public Position position;

        public Move(Position position)
        {
            this.position = position;
        }

        public override GameState ResultingState(GameState gameState)
        {
            MyLogger.GetInstance().Debug("Entered Resulting State for Move Action");
            if (gameState.board[position.x, position.y] == null)
            {
                //move character to new space
                gameState.currentCharacter.position = position;
                gameState.board[position.x, position.y] = gameState.currentCharacter;
                //reduce MpLeft by one
                gameState.MpLeft = gameState.MpLeft - 1;
                return gameState;
            }
            MyLogger.GetInstance().Debug("1If");
            //characters can't move through rocks
            if (gameState.board[position.x, position.y].GetType() == typeof(RockObject) ||
                gameState.board[position.x, position.y].GetType() == typeof(Portal))
            {
                MyLogger.GetInstance().Debug("Moved into Rock or Portal on x:" + position.x+ "y:" + position.y);
                throw new IllegalActionException();
            }

            //characters change place with character moved through
            MyLogger.GetInstance().Debug("2If");
            if (gameState.board[position.x, position.y].GetType() == typeof(SuperheroObject))
            {
                var characterInWay = (SuperheroObject) gameState.board[position.x, position.y];
                characterInWay.position = gameState.currentCharacter.position;
                gameState.board[gameState.currentCharacter.position.x, gameState.currentCharacter.position.y] =
                    characterInWay;
            }
            MyLogger.GetInstance().Debug("3If");
            if (gameState.board[position.x, position.y].GetType() == typeof(NPC))
            {
                var characterInWay = (NPC) gameState.board[position.x, position.y];
                characterInWay.position = gameState.currentCharacter.position;
                gameState.board[gameState.currentCharacter.position.x, gameState.currentCharacter.position.y] =
                    characterInWay;
            }
            //characters pick up infinity stones when they move into the space of the infinity stone
            MyLogger.GetInstance().Debug("4If");
            if (gameState.board[position.x, position.y].GetType() == typeof(InfinityStoneObject))
            {
                gameState.currentCharacter.inventory.Add(((InfinityStoneObject) gameState.board[position.x, position.y]).ID);
                gameState.entities.Remove(gameState.board[position.x, position.y]);

            }

            MyLogger.GetInstance().Debug("5If");
            //move character to new space
            gameState.currentCharacter.position = position;
            gameState.board[position.x, position.y] = gameState.currentCharacter;
            //reduce MpLeft by one
            gameState.MpLeft = gameState.MpLeft - 1;
            return gameState;
        }

        public override string GetServerMessage()
        {
            var ret = "";
            return ret;
        }

        public override GameAction DeapCopy()
        {
            return new Move(position);
        }

        public override bool Equals(GameAction action)
        {
            if (action.GetType() != typeof(Move)) return false;
            var move = (Move) action;
            return position.Equals(move.position);
        }
    }


    public class Attack : GameAction
    {
        public SuperheroObject attacked;
        public int distance;

        public Attack(SuperheroObject attacked, int distance)
        {
            this.attacked = attacked;
            this.distance = distance;
        }

        public override GameState ResultingState(GameState gameState)
        {
            MyLogger.GetInstance().Debug("Entered Attack Resulting State");
            var damage = distance > 1 ? gameState.currentCharacter.damageRange : gameState.currentCharacter.damageClose;
            MyLogger.GetInstance().Debug("Checkpoint1");
            attacked = (SuperheroObject) gameState.entities.Find(x =>
                x.GetType() == typeof(SuperheroObject) && ((SuperheroObject) x).name.Equals(attacked.name));
            MyLogger.GetInstance().Debug("Checkpoint2");
            if (attacked.healthPoints > damage)
            {
                MyLogger.GetInstance().Debug("Not killed");
                attacked.healthPoints -= damage;
                //MyLogger.GetInstance().Debug(attacked.name+" has " + attacked.healthPoints.ToString()+ "Hp left.");
            }
            else
            {
                MyLogger.GetInstance().Debug("Killed");
                //MyLogger.GetInstance().Debug(attacked.name+" was killed!");
                attacked.healthPoints = 0;
                attacked.isAlive = false;
                foreach (var stone in attacked.inventory)
                {
                    gameState = DropToNextFreeField(attacked.position, gameState, stone);
                }
            }
            MyLogger.GetInstance().Debug("Checkpoint3");
            //reduce ApLeft by 1
            gameState.ApLeft -= 1;
            return gameState;
        }

        private GameState DropToNextFreeField(Position attackedPosition, GameState gameState, int stone)
        {
            var searchWidth = 0;
            while (true)
            {
                for (var i = 0; i < gameState.boardLength; i++)
                for (var j = 0; j < gameState.boardWidth; j++)
                    if (Math.Max(Math.Abs(i - attackedPosition.x), Math.Abs(j - attackedPosition.y)) == searchWidth)
                        //Problem: Does not see the cooldowns here

                        if (gameState.board[i, j] == null)
                        {
                            var newStone = new InfinityStoneObject(false, 100, 100, new Position(i, j), stone);
                            gameState.entities.Add(newStone);
                            gameState.board[i, j] = newStone;
                            return gameState;
                        }

                searchWidth++;
            }
        }

        public override string GetServerMessage()
        {
            var ret = "";
            return ret;
        }

        public override GameAction DeapCopy()
        {
            return new Attack(attacked, distance);
        }

        public override bool Equals(GameAction action)
        {
            if (action.GetType() != typeof(Attack)) return false;
            var attack = (Attack) action;
            return attacked.Equals(attack.attacked) && distance > 1 == attack.distance > 1;
        }
    }

    public class NoAction : GameAction
    {
        public override GameState ResultingState(GameState gameState)
        {
            return gameState;
        }

        public override string GetServerMessage()
        {
            return null;
        }

        public override GameAction DeapCopy()
        {
            return new NoAction();
        }

        public override bool Equals(GameAction action)
        {
            return true;
        }
    }

    public class EndTurn : GameAction
    {
        public override GameState ResultingState(GameState gameState)
        {
            //at the character to the end of the line and change the currentCharacter
            gameState.characterOrder.Add(gameState.currentCharacter);
            gameState.currentCharacter = gameState.characterOrder[0];
            gameState.characterOrder.RemoveAt(0);

            gameState.ApLeft = gameState.currentCharacter.actionPoints;
            gameState.MpLeft = gameState.currentCharacter.movementPoints;

            return gameState;
        }

        public override string GetServerMessage()
        {
            var ret = "";
            return ret;
        }

        public override GameAction DeapCopy()
        {
            return new EndTurn();
        }

        public override bool Equals(GameAction action)
        {
            return true;
        }
    }

    public class PassInfinityStone : GameAction
    {
        public Position Target;

        public PassInfinityStone(Position target)
        {
            Target = target;
        }

        public override GameState ResultingState(GameState gameState)
        {
            MyLogger.GetInstance().Debug("Entered PassinfinityStone Resulting State");
            if (gameState.currentCharacter.inventory.Count == 0) throw new IllegalActionException();
            var targetSuperhero = (SuperheroObject) gameState.entities.Find(x =>
                x.GetType() == typeof(SuperheroObject) && ((SuperheroObject) x).position.Equals(Target));
            targetSuperhero.inventory.Add(gameState.currentCharacter.inventory[0]);
            gameState.currentCharacter.inventory.RemoveAt(0);

            gameState.ApLeft = gameState.ApLeft - 1;
            return gameState;
        }

        public override string GetServerMessage()
        {
            var ret = "";
            return ret;
        }

        public override GameAction DeapCopy()
        {
            return new PassInfinityStone(Target);
        }

        public override bool Equals(GameAction action)
        {
            MyLogger.GetInstance().Debug("Entered PassInfinityStone.Equals");
            if (!action.GetType().Equals(typeof(PassInfinityStone))) return false;
            var passInfinityStone = (PassInfinityStone) action;
            return Target.Equals(passInfinityStone.Target);
        }
    }

    public class IllegalActionException : Exception
    {
    }
}