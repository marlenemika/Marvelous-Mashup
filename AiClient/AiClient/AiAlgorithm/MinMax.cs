using System;
using System.Collections.Generic;
using System.Linq;
using AiClient.Network.Requests;
using AiClient.Utility;
using NLog;

namespace AiClient

{
    public class MinMax : AiAlgorithm
    {
        private static readonly double MAX = 100000;
        private static readonly double MIN = -100000;
        private static readonly int maxDepth = 1;
        public int countdeapth1;

        //calculates the value of the gameState for the localPlayer
        public override double EvaluationFunktion(GameState gameState)
        {
            //calculate averageHealth in the game
            double averageHealth = 0;
            var superheroCount = 0;
            foreach (var gameObject in gameState.entities)
                if (gameObject.GetType() == typeof(SuperheroObject))
                {
                    var superhero = (SuperheroObject) gameObject;
                    averageHealth += superhero.healthPoints;
                    superheroCount++;
                }

            averageHealth = averageHealth / superheroCount;
            double gameStateValue = 0;
            foreach (var gameObject in gameState.entities)
                if (gameObject.GetType() == typeof(SuperheroObject))
                {
                    var superhero = (SuperheroObject) gameObject;
                    if (superhero.PID.Equals(gameState.playerLocal))
                    {
                        gameStateValue += 20 * superhero.healthPoints *
                            Math.Max(superhero.damageClose, superhero.damageRange) / (averageHealth * averageHealth);
                        gameStateValue += 5 * superhero.inventory.Count() *
                                          (int) Math.Pow(1.5, superhero.inventory.Count());
                        if (superhero.inventory.Count == 6) gameStateValue += 10000;
                    }
                    else
                    {
                        gameStateValue -= 20 * superhero.healthPoints *
                            Math.Max(superhero.damageClose, superhero.damageRange) / (averageHealth * averageHealth);
                        gameStateValue -= 5 * superhero.inventory.Count() *
                                          (int) Math.Pow(1.5, superhero.inventory.Count());
                        if (superhero.inventory.Count == 6) gameStateValue -= 10000;
                    }
                }

            return gameStateValue;
        }

        public override List<BasicRequest> ChooseActions(GameState gameState)
        {
            MyLogger.GetInstance().Debug("Entered choose action");
            //MyLogger.GetInstance().Debug("CurrentCharacter is null" + (gameState.currentCharacter==null));
            if (!gameState.currentCharacter.isAlive)
            {
                var ret = new List<BasicRequest>();
                //ret.Add(new EndRoundRequest());
                return ret;
            }
            //MyLogger.GetInstance().Debug("Character is alive");
            return MinMaxFunction(gameState, 0, true, MIN, MAX).MessageMovesInTurn;
        }

        //implementation of the minmax algorithm with alpha beta prunning
        public MoveValuePair MinMaxFunction(GameState gameState, int currentDepth, bool maximize, double alpha,
            double beta)
        {
            if (currentDepth == 1)
            {
                countdeapth1 += 1;
                //MyLogger.GetInstance().Debug(countdeapth1.ToString());
            }
            //MyLogger.GetInstance().Debug("Entered MinMaxFunction");
            if (currentDepth >= maxDepth || gameState.IsFinished())
            {
                var ret1 = new MoveValuePair();
                ret1.Value = EvaluationFunktion(gameState);
                ret1.MoveInTurn = new List<GameAction> {new EndTurn()};
                ret1.MessageMovesInTurn = new List<BasicRequest> {new EndRoundRequest()};
                return ret1;
            }
            //MyLogger.GetInstance().Debug("No quick finish");
            var moves = PossibleMoves(gameState);
            //MyLogger.GetInstance().Debug("Possible Moves computed");
            double best;
            var bestMove = new List<GameAction> {new EndTurn()};
            var bestMoveMessage = new List<BasicRequest> {new EndRoundRequest()};
            moves.Reverse();
            if (maximize)
            {
                //MyLogger.GetInstance().Debug("Entered Max");
                best = MIN;
                foreach (var move in moves)
                {
                    var endValue = MinMaxFunction(move.ResultingState, currentDepth + 1,
                        move.ResultingState.currentCharacter.PID == move.ResultingState.playerLocal, alpha, beta).Value;
                    if (endValue > best)
                    {
                        best = endValue;
                        bestMove = move.MovesInTurn;
                        bestMoveMessage = move.MessagesMovesInTurn;
                        alpha = Math.Max(alpha, best);
                        if (beta <= alpha) break;
                    }
                }
            }
            else
            {
                //MyLogger.GetInstance().Debug("Entered Min");
                best = MAX;
                foreach (var move in moves)
                {
                    var endValue = MinMaxFunction(move.ResultingState, currentDepth + 1,
                        move.ResultingState.currentCharacter.PID == move.ResultingState.playerLocal, alpha, beta).Value;
                    if (endValue < best)
                    {
                        best = endValue;
                        bestMove = move.MovesInTurn;
                        bestMoveMessage = move.MessagesMovesInTurn;
                        alpha = Math.Min(beta, best);
                        if (beta <= alpha) break;
                    }
                }
            }

            var ret = new MoveValuePair();
            ret.Value = best;
            ret.MoveInTurn = bestMove;
            ret.MessageMovesInTurn = bestMoveMessage;
            return ret;
        }
    }

    public class MoveValuePair
    {
        public List<GameAction> MoveInTurn;
        public List<BasicRequest> MessageMovesInTurn;
        public double Value;
    }
}