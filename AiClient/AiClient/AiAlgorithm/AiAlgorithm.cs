using System;
using System.Collections.Generic;
using System.Globalization;
using System.Runtime.CompilerServices;
using AiClient.Network.Messages;
using AiClient.Network.Objects;
using AiClient.Network.Requests;
using AiClient.Utility;
using NLog;


namespace AiClient
{
    public abstract class AiAlgorithm
    {
        public int anzNextMove = 0;
        public abstract double EvaluationFunktion(GameState gameState);
        public abstract List<BasicRequest> ChooseActions(GameState gameState);

        public List<CompleteMove> PossibleMoves(GameState gameState)
        {
            if (!gameState.currentCharacter.isAlive)
            {
                var listGameAction = new List<GameAction>();
                var listGameActionMessages = new List<BasicRequest>();
                var completeGameAction = new CompleteMove(listGameAction, listGameActionMessages, gameState.DeapCopy());
                completeGameAction = Act(completeGameAction, new EndTurn());
                return new List<CompleteMove> {completeGameAction};
            }

            var actingPlayer = gameState.currentCharacter.PID;
            var ret = new List<CompleteMove>();
            var listActionsStartMove = new List<GameAction>();
            var listGameActionMessagesStartMove = new List<BasicRequest>();
            var completeStartMove = new CompleteMove(listActionsStartMove, listGameActionMessagesStartMove, gameState);
            var completeStartMoveList = new List<CompleteMove> {completeStartMove};
            //compute moves recursively
            //MyLogger.GetInstance().Debug("Before recursive");
            ret = NextMove(completeStartMoveList);
            //MyLogger.GetInstance().Debug("After recursive");
            //ret = FilterBest(ret, actingPlayer);
            return ret;
        }

        //eliminates redundant or strictly worse options
        private List<CompleteMove> FilterBest(List<CompleteMove> list, int actingPlayer)
        {
            for (var i = 0; i < list.Count; i++)
                if (list.Exists(x =>
                    !x.Equals(list[i]) && x.ResultingState.IsBetter(list[i].ResultingState, actingPlayer)))
                {
                    list.RemoveAt(i);
                    i -= 1;
                }

            return list;
        }

        //recursively computes all possible moves
        public List<CompleteMove> NextMove(List<CompleteMove> possibleStarts)
        {
            anzNextMove++;
            MyLogger.GetInstance().Debug(anzNextMove);
            MyLogger.GetInstance().Debug("Entered NextMove");
            
            var ret = new List<CompleteMove>();
            foreach (var completeMove in possibleStarts)
            {
                MyLogger.GetInstance().Debug("Entered Foreach");
                if (completeMove.MovesInTurn.Exists(x => x.GetType().Equals(typeof(EndTurn)))) continue;
                MyLogger.GetInstance().Debug("No endTurn in Move");
                //take last movement and add result to ret
                ret.AddRange(EndMove(completeMove));
                //if there are more actions to be taken give me all possible outcomes and add them to ret
                MyLogger.GetInstance().Debug("Before if");
                if (completeMove.ResultingState.ApLeft != 0)
                {
                    MyLogger.GetInstance().Debug("ApLeft!=0");
                    var resultingCompleteMoves = MovementAndAction(completeMove);
                    MyLogger.GetInstance().Debug("MovementAndActionAdded");
                    ret.AddRange(NextMove(resultingCompleteMoves));
                    MyLogger.GetInstance().Debug("Recursive call finished");
                }
            }

            return ret;
        }


        //returns a list with one more move made
        private List<CompleteMove> MovementAndAction(CompleteMove completeMove)
        {
            if(completeMove == null) return new List<CompleteMove>();
            MyLogger.GetInstance().Debug("MovementandAction Entered");
            var ret = new List<CompleteMove>();
            var actionsOnFields = ActionsOnFields(completeMove.ResultingState);
            MyLogger.GetInstance().Debug("Actions calculated");
            var wayTo = new CompleteMove[completeMove.ResultingState.boardLength,
                completeMove.ResultingState.boardWidth];
            wayTo[completeMove.ResultingState.currentCharacter.position.x,
                completeMove.ResultingState.currentCharacter.position.y] = completeMove.DeapCopy();

            var actionsTaken = new List<GameAction>();
            //stay on current field to act:
            if (actionsOnFields[completeMove.ResultingState.currentCharacter.position.x,
                completeMove.ResultingState.currentCharacter.position.y] != null)
            {
                MyLogger.GetInstance().Debug("Entered Stay to act");
                foreach (var action in actionsOnFields[completeMove.ResultingState.currentCharacter.position.x,
                    completeMove.ResultingState.currentCharacter.position.y])
                {
                    if (action == null)
                    {
                        continue;
                    }

                    var copy = completeMove.DeapCopy();
                    actionsTaken.Add(action);
                    copy = Act(copy, action);
                    ret.Add(copy);
                }
            }
            MyLogger.GetInstance().Debug("MoveToAct");
            for (var moveLeft = completeMove.ResultingState.MpLeft; moveLeft > 0; moveLeft--)
            {
                
                var newActionsTaken = new List<GameAction>();
                for (var i = 0; i < completeMove.ResultingState.boardLength; i++)
                for (var j = 0; j < completeMove.ResultingState.boardWidth; j++)
                {
                    MyLogger.GetInstance().Debug("i:" + i + "j:"+ j);
                    if (wayTo[i, j] != null) continue;
                    if (!(completeMove.ResultingState.board[i, j] == null))
                        if (completeMove.ResultingState.board[i, j].GetType().Equals(typeof(RockObject)) || completeMove.ResultingState.board[i, j].GetType().Equals(typeof(Portal)) || completeMove.ResultingState.board[i, j].GetType().Equals(typeof(NPC)))
                            continue;
                    MyLogger.GetInstance().Debug("No rock or portal on x:" + i + "y:" + j);
                    var copyTest = completeMove.DeapCopy();
                    if (copyTest.ResultingState.board[i, j] != null)
                    {
                        MyLogger.GetInstance().Debug(copyTest.ResultingState.board[i, j].entityType);
                        if (copyTest.ResultingState.board[i, j].entityType.Equals("Portal") ||
                            copyTest.ResultingState.board[i, j].entityType.Equals("Rock") ||
                            copyTest.ResultingState.board[i, j].entityType.Equals("NPC"))
                        {
                            MyLogger.GetInstance().Debug("FEHLER");
                        }
                    }
                    copyTest = completeMove.DeapCopy();

                    MyLogger.GetInstance().Debug("checkpoint0");
                    MyLogger.GetInstance().Debug(completeMove.ResultingState == null);
                    MyLogger.GetInstance().Debug(completeMove.ResultingState.boardLength);
                    MyLogger.GetInstance().Debug(completeMove.ResultingState.boardWidth);
                    if (i + 1 < completeMove.ResultingState.boardLength)
                    {
                        MyLogger.GetInstance().Debug("checkpoint01");
                        if (wayTo[i + 1, j] != null)
                        {
                            MyLogger.GetInstance().Debug((wayTo[i + 1, j].ResultingState == null).ToString());
                            MyLogger.GetInstance().Debug(wayTo[i + 1, j].ResultingState.MpLeft.ToString());
                            if (wayTo[i + 1, j].ResultingState.MpLeft >= moveLeft)
                                wayTo[i, j] = MoveTo(wayTo[i + 1, j].DeapCopy(), new Position(i, j));
                        }
                        MyLogger.GetInstance().Debug("checkpoint02");
                        if (j + 1 < completeMove.ResultingState.boardLength)
                        {
                            if (wayTo[i + 1, j + 1] != null)
                            {
                                MyLogger.GetInstance().Debug((wayTo[i + 1, j+1].ResultingState == null).ToString());
                                MyLogger.GetInstance().Debug((wayTo[i + 1, j+1].ResultingState.MpLeft).ToString());
                                if (wayTo[i + 1, j + 1].ResultingState.MpLeft >= moveLeft)
                                    wayTo[i, j] = MoveTo(wayTo[i + 1, j + 1].DeapCopy(), new Position(i, j));
                            }
                        }

                        MyLogger.GetInstance().Debug("checkpoint03");
                        if (j - 1 >= 0)
                            if (wayTo[i + 1, j - 1] != null)
                            {
                                MyLogger.GetInstance().Debug((wayTo[i + 1, j-1].ResultingState == null).ToString());
                                MyLogger.GetInstance().Debug((wayTo[i + 1, j-1].ResultingState.MpLeft).ToString());
                                if (wayTo[i + 1, j - 1].ResultingState.MpLeft >= moveLeft)
                                    wayTo[i, j] = MoveTo(wayTo[i + 1, j - 1].DeapCopy(), new Position(i, j));
                            }
                    }
                    MyLogger.GetInstance().Debug("checkpoint1");
                    if (i - 1 >= 0)
                    {
                        if (wayTo[i - 1, j] != null)
                        {
                            MyLogger.GetInstance().Debug(wayTo[i - 1, j].ResultingState == null);
                            MyLogger.GetInstance().Debug(wayTo[i - 1, j].ResultingState.MpLeft);
                            if (wayTo[i - 1, j].ResultingState.MpLeft >= moveLeft)
                                wayTo[i, j] = MoveTo(wayTo[i - 1, j].DeapCopy(), new Position(i, j));
                        }

                        if (j + 1 < completeMove.ResultingState.boardLength)
                        {
                            if (wayTo[i - 1, j + 1] != null)
                            {
                                MyLogger.GetInstance().Debug(wayTo[i - 1, j+1].ResultingState == null);
                                MyLogger.GetInstance().Debug(wayTo[i - 1, j+1].ResultingState.MpLeft);
                                if (wayTo[i - 1, j + 1].ResultingState.MpLeft >= moveLeft)
                                    wayTo[i, j] = MoveTo(wayTo[i - 1, j + 1].DeapCopy(), new Position(i, j));
                            }
                        }

                        if (j - 1 >= 0)
                            if (wayTo[i - 1, j - 1] != null)
                            {
                                MyLogger.GetInstance().Debug(wayTo[i - 1, j-1].ResultingState == null);
                                MyLogger.GetInstance().Debug(wayTo[i - 1, j-1].ResultingState.MpLeft);
                                if (wayTo[i - 1, j - 1].ResultingState.MpLeft >= moveLeft)
                                    wayTo[i, j] = MoveTo(wayTo[i - 1, j - 1].DeapCopy(), new Position(i, j));
                            }
                    }
                    MyLogger.GetInstance().Debug("checkpoint2");
                    if (j + 1 < completeMove.ResultingState.boardLength)
                        if (wayTo[i, j + 1] != null)
                        {
                            MyLogger.GetInstance().Debug(wayTo[i, j + 1].ResultingState == null);
                            MyLogger.GetInstance().Debug(wayTo[i, j + 1].ResultingState.MpLeft);
                            if (wayTo[i, j + 1].ResultingState.MpLeft >= moveLeft)
                                wayTo[i, j] = MoveTo(wayTo[i, j + 1].DeapCopy(), new Position(i, j));
                        }

                    MyLogger.GetInstance().Debug("checkpoint3");
                    if (j - 1 >= 0)
                        if (wayTo[i, j - 1] != null)
                        {
                            MyLogger.GetInstance().Debug(wayTo[i, j - 1].ResultingState == null);
                            MyLogger.GetInstance().Debug(wayTo[i, j - 1].ResultingState.MpLeft);
                            if (wayTo[i, j - 1].ResultingState.MpLeft >= moveLeft)
                                wayTo[i, j] = MoveTo(wayTo[i, j - 1].DeapCopy(), new Position(i, j));
                        }

                    MyLogger.GetInstance().Debug("checkpoint4");
                    if (wayTo[i, j] != null)
                        if (actionsOnFields[i, j] != null)
                            foreach (var action in actionsOnFields[i, j])
                                if (!actionsTaken.Exists(x => x.Equals(action)))
                                {
                                    MyLogger.GetInstance().Debug("Add action:" + action.GetType());
                                    if (action.GetType().Equals(typeof(PassInfinityStone)))
                                    {
                                        var castActionDebug = (PassInfinityStone)action;
                                        MyLogger.GetInstance().Debug("Target" + castActionDebug.Target.x+","+ castActionDebug.Target.y);
                                        MyLogger.GetInstance().Debug("actionsTaken:");
                                        foreach (var a in actionsTaken)
                                        {
                                            MyLogger.GetInstance().Debug(a);
                                            if (a.GetType().Equals(typeof(PassInfinityStone)))
                                            {
                                                var act = (PassInfinityStone) a;
                                                MyLogger.GetInstance().Debug("Target:"+act.Target.x+","+act.Target.y);
                                            }
                                        }
                                    }

                                    if (!newActionsTaken.Exists(x => x.Equals(action))) newActionsTaken.Add(action);

                                    var copy = wayTo[i, j].DeapCopy();
                                    MyLogger.GetInstance().Debug("before act");
                                    copy = Act(copy, action);
                                    MyLogger.GetInstance().Debug("after act");
                                    ret.Add(copy);
                                }
                }

                actionsTaken.AddRange(newActionsTaken);
            }
            MyLogger.GetInstance().Debug("Before Mp set 0");
            foreach (var retMove in ret)
            {
                retMove.ResultingState.MpLeft = 0;
            }
            MyLogger.GetInstance().Debug("After Mp set 0");
            return ret;
        }

        //performs and adds the action to completeMove
        private CompleteMove Act(CompleteMove completeMove, GameAction action)
        {
            MyLogger.GetInstance().Debug("Act Entered");
            action = action.DeapCopy();
            completeMove.MovesInTurn.Add(action);
            
            if (action.GetType() == typeof(Move))
            {
                MyLogger.GetInstance().Debug("Act on MoveAction");
                MoveRequest actionMessage;
                var move = (Move) action;
                string requestEntityType;
                MyLogger.GetInstance().Debug(completeMove.ResultingState.currentCharacter == null);
                if (completeMove.ResultingState.currentCharacter.PID == 1)
                {
                    requestEntityType = RequestEntityType.P1;
                }
                else
                {
                    requestEntityType = RequestEntityType.P2;
                }
                actionMessage = new MoveRequest(new RequestEntity(requestEntityType, completeMove.ResultingState.currentCharacter.ID),new []{completeMove.ResultingState.currentCharacter.position.x,completeMove.ResultingState.currentCharacter.position.y}, new []{move.position.x,move.position.y});
                completeMove.MessagesMovesInTurn.Add(actionMessage);
            }else if (action.GetType() == typeof(Attack))
            {
                var attack = (Attack) action;
                if (attack.distance <= 1)
                {
                    string originEntityType;
                    if (completeMove.ResultingState.currentCharacter.PID == 1)
                    {
                        originEntityType = RequestEntityType.P1;
                    }
                    else
                    {
                        originEntityType = RequestEntityType.P2;
                    }
                    string targetEntityType;
                    
                    if (attack.attacked.PID == 1)
                    {
                        targetEntityType = RequestEntityType.P1;
                    }
                    else
                    {
                        targetEntityType = RequestEntityType.P2;
                    }
                    var actionMessage = new MeleeAttackRequest(
                        new RequestEntity(originEntityType, completeMove.ResultingState.currentCharacter.ID),
                        new RequestEntity(targetEntityType, attack.attacked.ID),new []{completeMove.ResultingState.currentCharacter.position.x,completeMove.ResultingState.currentCharacter.position.y}, new []{attack.attacked.position.x, attack.attacked.position.y}, completeMove.ResultingState.currentCharacter.damageClose);
                    completeMove.MessagesMovesInTurn.Add(actionMessage);
                }
                else
                {
                    string originEntityType;
                    if (completeMove.ResultingState.currentCharacter.PID == 1)
                    {
                        originEntityType = RequestEntityType.P1;
                    }
                    else
                    {
                        originEntityType = RequestEntityType.P2;
                    }
                    string targetEntityType;
                    
                    if (attack.attacked.PID == 1)
                    {
                        targetEntityType = RequestEntityType.P1;
                    }
                    else
                    {
                        targetEntityType = RequestEntityType.P2;
                    }
                    var actionMessage = new RangedAttackRequest(
                        new RequestEntity(originEntityType, completeMove.ResultingState.currentCharacter.ID),
                        new RequestEntity(targetEntityType, attack.attacked.ID),new []{completeMove.ResultingState.currentCharacter.position.x,completeMove.ResultingState.currentCharacter.position.y}, new []{attack.attacked.position.x, attack.attacked.position.y}, completeMove.ResultingState.currentCharacter.damageRange);
                    completeMove.MessagesMovesInTurn.Add(actionMessage);
                }
            }else if (action.GetType() == typeof(PassInfinityStone))
            {
                MyLogger.GetInstance().Debug("Act on PassInfinityStone");
                var passInfintityStone = (PassInfinityStone) action;
                string originEntityType;
                if (completeMove.ResultingState.currentCharacter.PID == 1)
                {
                    originEntityType = RequestEntityType.P1;
                }
                else
                {
                    originEntityType = RequestEntityType.P2;
                }
                string targetEntityType;
                MyLogger.GetInstance().Debug("before get Target");
                SuperheroObject target = (SuperheroObject)completeMove.ResultingState.entities.Find(x => x.GetType()==typeof(SuperheroObject) &&
                    passInfintityStone.Target.Equals(((SuperheroObject) x).position));
                if (target == null)
                {
                    Console.WriteLine("Error NUll");
                }
                MyLogger.GetInstance().Debug("after get Target");
                MyLogger.GetInstance().Debug("Target: "+target);
                if (target.PID == 1)
                {
                    targetEntityType = RequestEntityType.P1;
                }
                else
                {
                    targetEntityType = RequestEntityType.P2;
                }
                MyLogger.GetInstance().Debug("Get InfinityStone to pass");
                var passedStone = completeMove.ResultingState.currentCharacter.inventory[0];
                MyLogger.GetInstance().Debug("Create ActionMessage");
                var actionMessage = new ExchangeInfinityStoneRequest(
                    new RequestEntity(originEntityType, completeMove.ResultingState.currentCharacter.ID),
                    new RequestEntity(targetEntityType, target.ID),new []{completeMove.ResultingState.currentCharacter.position.x,completeMove.ResultingState.currentCharacter.position.y}, new []{target.position.x, target.position.y}, new RequestEntity(RequestEntityType.InfinityStones, passedStone));
                MyLogger.GetInstance().Debug("ActionMessage created");
                completeMove.MessagesMovesInTurn.Add(actionMessage);
            }else if (action.GetType() == typeof(EndTurn))
            {
                completeMove.MessagesMovesInTurn.Add(new EndRoundRequest());
            }
            MyLogger.GetInstance().Debug("Ende Act on Action");
            MyLogger.GetInstance().Debug(action == null);
            MyLogger.GetInstance().Debug(action.GetType());
            MyLogger.GetInstance().Debug(completeMove.ResultingState == null);
            
            completeMove.ResultingState = action.ResultingState(completeMove.ResultingState);
            MyLogger.GetInstance().Debug("Ganz Ende Act on Move Action");
            return completeMove;
        }


        //moves current character in complete move to position
        private CompleteMove MoveTo(CompleteMove completeMove, Position position)
        {
            var nextStep = new Move(position);
            var copy = completeMove.DeapCopy();
            MyLogger.GetInstance().Debug("MoveTo before Act");
            copy = Act(copy, nextStep);
            MyLogger.GetInstance().Debug("MoveTo after Act");
            return copy;
        }

        //possible ends of the turn
        private List<CompleteMove> EndMove(CompleteMove completeMove)
        {
            //MyLogger.GetInstance().Debug("EndMove entered");
            var ret = new List<CompleteMove>();
            var copy = completeMove.DeapCopy();
            //MyLogger.GetInstance().Debug("DeapCopy made");
            var endTurn = new EndTurn();
            copy = Act(copy, endTurn);
            ret.Add(copy);
            //todo: implement other ends
            //MyLogger.GetInstance().Debug("EndMove finished");
            return ret;
        }

        public List<GameAction>[,] ActionsOnFields(GameState gameState)
        {
            var boardActionLists = new List<GameAction>[gameState.boardLength, gameState.boardWidth];
            if (gameState.entities == null) return boardActionLists;
            foreach (var obj in gameState.entities)
                if (obj.GetType().Equals(typeof(RockObject)))
                {
                    MyLogger.GetInstance().Debug("AddRockAction");
                    var cur = (RockObject) obj;
                }
                else if (obj.GetType().Equals(typeof(InfinityStoneObject)))
                {
                    MyLogger.GetInstance().Debug("AddInfinityStoneAction");
                    var cur = (InfinityStoneObject) obj;
                    MyLogger.GetInstance().Debug("x:"+cur.position.x+"y:"+cur.position.y);
                    MyLogger.GetInstance().Debug("x:"+gameState.boardLength+"y:"+gameState.boardWidth);
                    MyLogger.GetInstance().Debug(boardActionLists[cur.position.x, cur.position.y]==null);
                    if (boardActionLists[cur.position.x, cur.position.y] == null)
                    {
                        boardActionLists[cur.position.x, cur.position.y] = new List<GameAction>();
                    }
                    boardActionLists[cur.position.x, cur.position.y].Add(new NoAction());
                    MyLogger.GetInstance().Debug("InfinityStoneAction added");
                }
                else if (obj.GetType().Equals(typeof(SuperheroObject)))
                {
                    MyLogger.GetInstance().Debug("AddSuperheroAction");
                    var cur = (SuperheroObject) obj;
                    //MyLogger.GetInstance().Debug("Superhero: " + cur.name);
                    if (!cur.isAlive) continue;

                    if (cur.PID == gameState.currentCharacter.PID &&
                        (gameState.currentCharacter.inventory.Count > 0) && cur.ID != gameState.currentCharacter.ID)
                    {       
                        //todo: for each surronding field
                        //MyLogger.GetInstance().Debug("On friend");
                        
                        List<Position> neighborPositions = GetFreePositionsAround(cur.position.x, cur.position.y, gameState);
                        foreach (var position in neighborPositions)
                        {
                            if (boardActionLists[cur.position.x, cur.position.y] == null)
                            {
                                boardActionLists[cur.position.x, cur.position.y] = new List<GameAction>();
                            }
                            MyLogger.GetInstance().Debug("On friend");
                            AddInterestingTile(gameState, boardActionLists, new PassInfinityStone(cur.position), position.x, position.y);
                        }
                    }
                    else if (cur.PID != gameState.currentCharacter.PID)
                    {
                        //MyLogger.GetInstance().Debug("On enemy");
                        foreach (var position in CalculateAttackFields(gameState, cur))
                        {
                            if (boardActionLists[position.x, position.y] == null)
                            {
                                boardActionLists[position.x, position.y] = new List<GameAction>();
                            }
                            //MyLogger.GetInstance().Debug("AttackFieldCalculated");
                            AddInterestingTile(gameState, boardActionLists,
                                new Attack(cur,
                                    Math.Max(Math.Abs(cur.position.x - position.x),
                                        Math.Abs(cur.position.y - position.y))), position.x, position.y);
                        }
                    }
                }
            MyLogger.GetInstance().Debug("ActionsOnFields Finished");
            return boardActionLists;
        }

        private List<Position> GetFreePositionsAround(int positionX, int positionY, GameState gameState)
        {
            var ret = new List<Position>();
            Type type;
            if (positionX + 1 < gameState.boardLength)
            {
                if (gameState.board[positionX + 1, positionY] == null)
                {
                    ret.Add(new Position(positionX+1, positionY));
                }
                else
                {
                    type = gameState.board[positionX + 1, positionY].GetType();
                    if (!type.Equals(typeof(RockObject)) &&
                        !type.Equals(typeof(Portal)) &&
                        !type.Equals(typeof(NPC)))
                    {
                        ret.Add(new Position(positionX+1, positionY));
                    }
                }
                
                if (positionY + 1 < gameState.boardWidth)
                {
                    if (gameState.board[positionX + 1, positionY+1] == null)
                    {
                        ret.Add(new Position(positionX+1, positionY+1));
                    }
                    else
                    {
                        type = gameState.board[positionX + 1, positionY+1].GetType();
                        if (!type.Equals(typeof(RockObject)) &&
                            !type.Equals(typeof(Portal)) &&
                            !type.Equals(typeof(NPC)))
                        {
                            ret.Add(new Position(positionX+1, positionY+1));
                        }
                    }
                }
                if (positionY - 1 >= 0)
                {
                    if (gameState.board[positionX + 1, positionY-1] == null)
                    {
                        ret.Add(new Position(positionX+1, positionY-1));
                    }
                    else
                    {
                        type = gameState.board[positionX + 1, positionY - 1].GetType();
                        if (!type.Equals(typeof(RockObject)) &&
                            !type.Equals(typeof(Portal)) &&
                            !type.Equals(typeof(NPC)))
                        {
                            ret.Add(new Position(positionX + 1, positionY - 1));
                        }
                    }
                }
            }
            if (positionX - 1 >= 0)
            {
                if (gameState.board[positionX - 1, positionY] == null)
                {
                    ret.Add(new Position(positionX-1, positionY));
                }
                else
                {
                    type = gameState.board[positionX - 1, positionY].GetType();
                    if (!type.Equals(typeof(RockObject)) &&
                        !type.Equals(typeof(Portal)) &&
                        !type.Equals(typeof(NPC)))
                    {
                        ret.Add(new Position(positionX - 1, positionY));
                    }
                }

                if (positionY + 1 < gameState.boardWidth)
                {
                    if (gameState.board[positionX - 1, positionY + 1] == null)
                    {
                        ret.Add(new Position(positionX - 1, positionY + 1));
                    }
                    else
                    {
                        type = gameState.board[positionX - 1, positionY + 1].GetType();
                        if (!type.Equals(typeof(RockObject)) &&
                            !type.Equals(typeof(Portal)) &&
                            !type.Equals(typeof(NPC)))
                        {
                            ret.Add(new Position(positionX - 1, positionY + 1));
                        }
                    }
                }
                if (positionY - 1 >= 0)
                {
                    if (gameState.board[positionX - 1, positionY-1] == null)
                    {
                        ret.Add(new Position(positionX-1, positionY-1));
                    }
                    else
                    {
                        type = gameState.board[positionX - 1, positionY - 1].GetType();
                        if (!type.Equals(typeof(RockObject)) &&
                            !type.Equals(typeof(Portal)) &&
                            !type.Equals(typeof(NPC)))
                        {
                            ret.Add(new Position(positionX - 1, positionY - 1));
                        }
                    }
                }
            }
            if (positionY - 1 >= 0)
            {
                if (gameState.board[positionX, positionY-1] == null)
                {
                    ret.Add(new Position(positionX, positionY-1));
                }
                else
                {
                    type = gameState.board[positionX, positionY - 1].GetType();
                    if (!type.Equals(typeof(RockObject)) &&
                        !type.Equals(typeof(Portal)) &&
                        !type.Equals(typeof(NPC)))
                    {
                        ret.Add(new Position(positionX, positionY - 1));
                    }
                }
            }
            if (positionY + 1 < gameState.boardWidth)
            {
                if (gameState.board[positionX, positionY+1] == null)
                {
                    ret.Add(new Position(positionX, positionY+1));
                }
                else
                {
                    type = gameState.board[positionX, positionY + 1].GetType();
                    if (!type.Equals(typeof(RockObject)) &&
                        !type.Equals(typeof(Portal)) &&
                        !type.Equals(typeof(NPC)))
                    {
                        ret.Add(new Position(positionX, positionY + 1));
                    }
                }
            }

            return ret;
        }

        private List<Position> CalculateAttackFields(GameState gameState, SuperheroObject target)
        {
            //MyLogger.GetInstance().Debug("CalculateAttackFields Entered");
            var positionList = new List<Position>();
            for (var i = Math.Max(target.position.x-gameState.currentCharacter.attackRange, -target.position.x);
                i <= Math.Min(gameState.currentCharacter.attackRange, gameState.boardLength - target.position.x - 1);
                i++)
            for (var j = Math.Max(-gameState.currentCharacter.attackRange, -target.position.y);
                j <= Math.Min(gameState.currentCharacter.attackRange, gameState.boardLength - target.position.y - 1);
                j++)
            {
                var pos = new Position(target.position.x + i, target.position.y + j);
                if (NoBlockade(pos, target.position, gameState)) positionList.Add(pos);
            }
            //MyLogger.GetInstance().Debug("CalculateAttackFields Finished");
            return positionList;
        }

        //returns True if you can shoot from pos to targetPosition
        private bool NoBlockade(Position pos, Position targetPosition, GameState gameState)
        {
            //MyLogger.GetInstance().Debug("NoBlockadeEntered");
            var xLow = Math.Min(pos.x, targetPosition.x);
            var xHigh = Math.Max(pos.x, targetPosition.x);
            var yLow = Math.Min(pos.y, targetPosition.y);
            var yHigh = Math.Max(pos.y, targetPosition.y);
            //MyLogger.GetInstance().Debug("MathFunktions");
            for (var x = xLow; x <= xHigh; x++)
            for (var y = yLow; y <= yHigh; y++)
            {
                //MyLogger.GetInstance().Debug("x:"+x+"y:"+y);
                //MyLogger.GetInstance().Debug("xHigh:"+xHigh+"yHigh:"+yHigh);
                //MyLogger.GetInstance().Debug(gameState.board.Length);
                //MyLogger.GetInstance().Debug(gameState.board[x, y]==null);
                if (gameState.board[x, y] == null) continue;
                //MyLogger.GetInstance().Debug("3.1");
                if (x == pos.x && y == pos.y) continue;
                //MyLogger.GetInstance().Debug("3.2");
                if (x == targetPosition.x && y == targetPosition.y) continue;
                //MyLogger.GetInstance().Debug("Critical FIeld reached");
                if (gameState.board[x, y].GetType() == typeof(RockObject) ||
                    gameState.board[x, y].GetType() == typeof(SuperheroObject) ||
                    gameState.board[x, y].GetType() == typeof(Portal) ||
                    gameState.board[x, y].GetType() == typeof(NPC))
                {
                    var dx = pos.x - targetPosition.x;
                    var dy = pos.y - targetPosition.y;
                    var dxMid = x - targetPosition.x;
                    var dyMid = y - targetPosition.y;
                    var rightUpperComparison =
                        dx * (2 * dyMid + 1) <=
                        (2 * dxMid + 1) * dy;
                    var leftUpperComparison =
                        dx * (2 * dyMid + 1) <=
                        (2 * dxMid - 1) * dy;
                    var leftLowerComparison =
                        dx * (2 * dyMid - 1) <=
                        (2 * dxMid - 1) * dy;
                    var rightLowerComparison =
                        dx * (2 * dyMid - 1) <=
                        (2 * dxMid + 1) * dy;
                    if (!(rightUpperComparison == leftUpperComparison && leftUpperComparison == leftLowerComparison &&
                          leftLowerComparison == rightLowerComparison)) return false;
                }
                //MyLogger.GetInstance().Debug("Critical Field solved");
            }

            return true;
        }

        //Add tile if it can be entered
        private List<GameAction>[,] AddInterestingTile(GameState gameState, List<GameAction>[,] boardActionLists,
            GameAction gameAction, int positionX, int positionY)
        {
            if (gameState.board[positionX, positionY] != null)
            {
                if (gameState.board[positionX, positionY].GetType() == typeof(RockObject))
                    return boardActionLists;
                if (gameState.board[positionX, positionY].GetType() == typeof(Portal))
                    return boardActionLists;
                if (gameState.board[positionX, positionY].GetType() == typeof(NPC))
                    return boardActionLists;
            }

            if (boardActionLists[positionX, positionY] == null)
                boardActionLists[positionX, positionY] = new List<GameAction>();
            if (gameAction.GetType().Equals(typeof(Move)))
            {
                var castAction = (Move) gameAction;
                boardActionLists[positionX, positionY].Add(castAction);
            }
            else if (gameAction.GetType().Equals(typeof(Attack)))
            {
                var castAction = (Attack) gameAction;
                boardActionLists[positionX, positionY].Add(castAction);
            }
            else if (gameAction.GetType().Equals(typeof(NoAction)))
            {
                var castAction = (NoAction) gameAction;
                boardActionLists[positionX, positionY].Add(castAction);
            }
            else if (gameAction.GetType().Equals(typeof(PassInfinityStone)))
            {
                var castAction = (PassInfinityStone) gameAction;
                boardActionLists[positionX, positionY].Add(castAction);
            }

            return boardActionLists;
        }
    }
}