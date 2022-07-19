using System;
using System.Collections.Generic;
using AiClient.Network.Messages;
using AiClient.Network.Requests;

namespace AiClient
{
    public class CompleteMove
    {
        public List<GameAction> MovesInTurn;
        public GameState ResultingState;
        public List<BasicRequest> MessagesMovesInTurn;
        public CompleteMove(List<GameAction> movesInTurn, List<BasicRequest> messagesMovesInTurn,GameState resultingState)
        {
            MovesInTurn = movesInTurn;
            MessagesMovesInTurn = messagesMovesInTurn;
            ResultingState = resultingState;
        }

        public CompleteMove DeapCopy()
        {
            //MyLogger.GetInstance().Debug("CompleteMove.DeapCopy Entered");
            var movesInTurn = new List<GameAction>();
            foreach (var gameAction in MovesInTurn) movesInTurn.Add(gameAction.DeapCopy());
            //MyLogger.GetInstance().Debug("CompleteMove.DeapCopy Finished");
            var messagesMovesInTurn = new List<BasicRequest>();
            foreach (var request in MessagesMovesInTurn) messagesMovesInTurn.Add(request);
            //MyLogger.GetInstance().Debug("CompleteMove.DeapCopy Finished");
            return new CompleteMove(movesInTurn, messagesMovesInTurn, ResultingState.DeapCopy());
        }
    }
}