using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Threading;
using AiClient.Network.Events.Entity;
using AiClient.Network.Events.Game;
using AiClient.Network.Events.Gamestate;
using AiClient.Network.Events.Notification;
using AiClient.Network.Messages;
using AiClient.Network.Objects;
using AiClient.Network.Requests;
using Newtonsoft.Json;
using WebSocketSharp;

namespace AiClient
{
    public class Application
    {
        private static WebSocket _webSocket;
        private static string _gameID;
        private static GameStructure _gameStructure;

        private static int ignoreGamestateCount = 0;
        private static string host = null;
        private static int port = -1;
        private static string name = null;
        private static string deviceID = null;
        
        public static List<BasicRequest> listBasicRequests;

        private static bool _ingame = false;

        public static void Main(string[] args)
        {
            try
            {
                host = args[0];
                port = int.Parse(args[1]);
                name = args[2];
                deviceID = args[3];
            }
            catch (Exception e)
            {
                Console.WriteLine("Wrong parameter structure! | command <hostname> <port>");
                throw;
            }
            
            using (_webSocket = new WebSocket ("ws://" + host + ":" + port + "/channel"))
            {
                _webSocket.OnMessage += WebSocketOnMessage;
                _webSocket.Connect ();
                
                var json = JsonConvert.SerializeObject(new HelloServer(name, deviceID));
                
                _webSocket.Send(json);
                Console.WriteLine("AI-Client -> Server | " + json);

                Console.ReadKey(true);
            }
        }

        //handles incoming messages
        private static void WebSocketOnMessage(object sender, MessageEventArgs e)
        {
            Thread.Sleep(100);
            Console.WriteLine("Server -> AI-Client | " + e.Data);

            if (!_ingame)
            {

                var basicMessage = JsonConvert.DeserializeObject<BasicMessage>(e.Data);

                if (basicMessage == null) return;

                switch (basicMessage.messageType)
                {
                    case "HELLO_CLIENT":
                        OnHelloClient(JsonConvert.DeserializeObject<HelloClient>(e.Data));
                        break;
                    case "GOODBYE_CLIENT":
                        OnGoodbyeClient(JsonConvert.DeserializeObject<GoodbyeClient>(e.Data));
                        break;
                    case "GAME_ASSIGNMENT":
                        OnGameAssignment(JsonConvert.DeserializeObject<GameAssignment>(e.Data));
                        break;
                    case "GENERAL_ASSIGNMENT":
                        OnGeneralAssignment(JsonConvert.DeserializeObject<GeneralAssignment>(e.Data));
                        break;
                    case "GAME_STRUCTURE":
                        OnGameStructure(JsonConvert.DeserializeObject<GameStructure>(e.Data));
                        break;
                }
            }
            else
            {
                try
                {
                    var gameStateEvent = JsonConvert.DeserializeObject<GamestateEvent>(e.Data);

                    if (gameStateEvent == null) return;

                    OnGameStateEvent(gameStateEvent);
                }
                catch (Exception exception)
                {
                    // ignored
                }

                try
                {
                    var turnEvent = JsonConvert.DeserializeObject<TurnEvent>(e.Data);

                    if (turnEvent == null) return;

                    listBasicRequests = null;
                }
                catch (Exception exception)
                {
                    // ignored
                }
                try
                {
                    var pauseStopEvent = JsonConvert.DeserializeObject<PauseStopEvent>(e.Data);

                    if (pauseStopEvent != null && pauseStopEvent.eventType.Equals("pauseStopEvent"))
                    {
                        ignoreGamestateCount++;
                    }

                }
                catch (Exception exception)
                {
                    // ignored
                }
                try
                {
                    var pauseStartEvent = JsonConvert.DeserializeObject<PauseStartEvent>(e.Data);

                    if (pauseStartEvent != null && pauseStartEvent.eventType.Equals("PauseStartEvent"))
                    {
                        ignoreGamestateCount++;
                        Console.WriteLine("ignoreGamestateCount:" + ignoreGamestateCount);
                    }
                    

                }
                catch (Exception exception)
                {
                    // ignored
                }
                try
                {
                    var spawnEntityEvent = JsonConvert.DeserializeObject<SpawnEntityEvent>(e.Data);

                    if (spawnEntityEvent != null && spawnEntityEvent.eventType.Equals("SpawnEntityEvent"))
                    {
                        ignoreGamestateCount++;
                        Console.WriteLine("ignoreGamestateCount:" + ignoreGamestateCount);
                    }
                    

                }
                catch (Exception exception)
                {
                    // ignored
                }
                try
                {
                    var destroyEntityEvent = JsonConvert.DeserializeObject<SpawnEntityEvent>(e.Data);

                    if (destroyEntityEvent != null && destroyEntityEvent.eventType.Equals("destroyEntityEvent"))
                    {
                        ignoreGamestateCount++;
                        Console.WriteLine("ignoreGamestateCount:" + ignoreGamestateCount);
                    }
                    

                }
                catch (Exception exception)
                {
                    // ignored
                }
            }
        }

        //called when GamestateEvent comes in
        private static void OnGameStateEvent(GamestateEvent gameStateEvent)
        {
            if (ignoreGamestateCount > 0)
            {
                ignoreGamestateCount--;
                Console.WriteLine("ignoreGamestateCount:" + ignoreGamestateCount);
                return;
            }
            if (!gameStateEvent.activeCharacter.entityID.Equals("P1") && name.Equals(_gameStructure.playerOneName)) return;
            if (!gameStateEvent.activeCharacter.entityID.Equals("P2") && name.Equals(_gameStructure.playerTwoName)) return;
            Console.WriteLine("reached");
            Console.WriteLine(listBasicRequests==null);
            if (listBasicRequests == null || listBasicRequests.Count == 0)
            {
                Console.WriteLine("1.1");
                MinMax minMax = new MinMax();
                List<Character> characters = new List<Character>();
                characters.AddRange(_gameStructure.playerOneCharacters);
                characters.AddRange(_gameStructure.playerTwoCharacters);
                Console.WriteLine("1.2");
                if (name.Equals(_gameStructure.playerOneName))
                {
                    Console.WriteLine("1.3.1.1");
                    var gameState = new GameState(gameStateEvent, 1, 2, characters);
                    Console.WriteLine("1.3.1.2");
                    listBasicRequests = minMax.ChooseActions(gameState);
                    Console.WriteLine("1.3.1.3");
                }   
                else
                {
                    Console.WriteLine("1.3.2.1");
                    var gameState = new GameState(gameStateEvent, 2, 1, characters);
                    Console.WriteLine("1.3.2.2");
                    listBasicRequests = minMax.ChooseActions(gameState);
                    Console.WriteLine("1.3.2.3");
                }
                Console.WriteLine(listBasicRequests[0]);
                SendMessage(_webSocket, listBasicRequests[0]);
                listBasicRequests.RemoveAt(0);
            }
            else
            {
                SendMessage(_webSocket, listBasicRequests[0]);
                listBasicRequests.RemoveAt(0);
            }
        }

        //called when HelloClient comes in
        private static void OnHelloClient(HelloClient deserializeObject)
        {
            if (deserializeObject.runningGame)
            {
                Reconnect reconnectMessage = new Reconnect(true);
                SendMessage(_webSocket, reconnectMessage);
            }
            else
            {
                PlayerReady playerReadyMessage = new PlayerReady(true, Role.KI);
                SendMessage(_webSocket, playerReadyMessage);
            }
        }

        //called when GeneralAssignment comes in
        private static void OnGeneralAssignment(GeneralAssignment deserializedObject)
        {
            _gameID = deserializedObject.gameID;
        }

        //called when GameStructure comes in
        private static void OnGameStructure(GameStructure deserializedObject)
        {
            _gameStructure = deserializedObject;
            _ingame = true;
        }
        
        //called when GoodbyeClient comes in
        private static void OnGoodbyeClient(GoodbyeClient deserializeObject)
        {
            _webSocket.Close();
            Console.WriteLine("The server has closed the connection: " + deserializeObject.message);
            Console.WriteLine("Shutting down...");

            Environment.Exit(0);
        }
        
        //called when GameAssignment comes in
        private static void OnGameAssignment(GameAssignment deserializeObject)
        {
            _gameID = deserializeObject.gameID;
            var characterSelection = new bool[12];

            for (var i = 0; i < 12; i++)
            {
                if (i < 6) characterSelection[i] = true;
                else characterSelection[i] = false;
            }
            
            SendMessage(_webSocket, new CharacterSelection(characterSelection));
        }

        //called to send Message
        private static void SendMessage(WebSocket webSocket, string json)
        {
            webSocket.Send(json);
            Console.WriteLine("AI-Client -> Server | " + json);
        }
        
        private static void SendMessage(WebSocket webSocket, BasicMessage message)
        {
            var json = JsonConvert.SerializeObject(message);
            webSocket.Send(json);
            Console.WriteLine("AI-Client -> Server | " + json);
        }
        
        //called to send Message
        private static void SendMessage(WebSocket webSocket, BasicRequest message)
        {
            var json = JsonConvert.SerializeObject(message);
            webSocket.Send(json);
            Console.WriteLine("AI-Client -> Server | " + json);
        }
    }
}