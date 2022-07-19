using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Network;
using Network.Events;
using Network.Events.Gamestate;
using Network.Messages;
using Network.Requests;
using Newtonsoft.Json;
using Objects;
using Settings;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

namespace MainMenu
{
    public class MainMenuController : MonoBehaviour, INetworkListener
    {
        public SettingsObject settingsObject;
        public GameState gameState;
        
        public Text lblInputResponse;
        public InputField txtHostname;
        public InputField txtPort;
        public Button btnSubmit;

        private string _userRole;
        
        public string hostname = "";
        public int port = 0;
        
        public static TaskScheduler unityTaskScheduler;
        public static int unityThread;
        public static SynchronizationContext unitySynchronizationContext;
        static public Queue<Action> runInUpdate= new Queue<Action>();

        public static bool isOnUnityThread => unityThread == Thread.CurrentThread.ManagedThreadId;
        
        public async void OnConnectClicked()
        {
            btnSubmit.gameObject.SetActive(false);
            lblInputResponse.gameObject.SetActive(true);
            lblInputResponse.text = "Connecting...";
            
            var success = await NetworkAdapter.Instance.
                ConnectToServer(hostname, port, settingsObject.username, _userRole);

            if (success) return;
            
            btnSubmit.gameObject.SetActive(false);
            lblInputResponse.gameObject.SetActive(true);
            lblInputResponse.text = "Error while connecting...";
        }

        private void Start()
        {
            NetworkAdapter.Instance.UpdateOwner(this);
            CheckLabelState(true);
        }

        public void OnHostnameEdited()
        {
            hostname = txtHostname.text;
            CheckLabelState(false);
        }
        
        public void OnPortEdited()
        {
            port = int.Parse(txtPort.text);
            CheckLabelState(false);
        }

        private void CheckLabelState(bool initial)
        {
            if (hostname == "" && port == 0)
            {
                if (!initial) lblInputResponse.gameObject.SetActive(true);
                btnSubmit.gameObject.SetActive(false);
                lblInputResponse.text = "Enter hostname and port";
            } 
            else if (hostname == "")
            {
                lblInputResponse.gameObject.SetActive(true);
                btnSubmit.gameObject.SetActive(false);
                lblInputResponse.text = "Enter a hostname";
            }
            else if (port < 1  || port > 65535)
            {
                lblInputResponse.gameObject.SetActive(true);
                btnSubmit.gameObject.SetActive(false);
                lblInputResponse.text = "Enter a valid port ( 1 - 65535 )";
            }
            else
            {
                lblInputResponse.gameObject.SetActive(false);
                btnSubmit.gameObject.SetActive(true);
            }
        }

        public void SetUserPlayer()
        {
            _userRole = Role.PLAYER;
        }
        
        public void SetUserSpectator()
        {
            _userRole = Role.SPECTATOR;
        }

        public async void DisconnectFromServer()
        {
            await NetworkAdapter.Instance.Send(new DisconnectRequest());
            CheckLabelState(false);
        }
        
        public async void OnMessageReceived(BasicMessage message, string json)
        {
            switch (message.messageType)
            {
                // on hello client -> set label on "waiting for second player"
                case "HELLO_CLIENT":
                    RunOnUnityThread(SetLabelWaiting);
                    break;
                case "GAME_STRUCTURE":
                    var gameStructure = JsonConvert.DeserializeObject<GameStructure>(json);
                    lock (gameState)
                    {
                        gameState.UpdateGameState(gameStructure);
                        RunOnUnityThread(NavigateToInGameScene);
                    }
                    break;
                // on game assignment -> navigate to character selection
                case "GAME_ASSIGNMENT":
                    var gameAssignment = JsonConvert.DeserializeObject<GameAssignment>(json);
                    if (gameAssignment == null) return;
                    lock (gameState)
                    {
                        gameState.playerCharacterAssignment = gameAssignment.characterSelection;
                        RunOnUnityThread(NavigateToCharacterSelection);
                    }
                    break;
                // on general assignment -> set label on "players are selecting superheros"
                case "GENERAL_ASSIGNMENT":
                    if (NetworkAdapter.Instance.GameRunning)
                        await NetworkAdapter.Instance.Send(new Req());
                    RunOnUnityThread(SetLabelInSelection);
                    break;
            }
        }

        private void SetLabelWaiting()
        {
            // called on hello client
            btnSubmit.gameObject.SetActive(false);
            lblInputResponse.gameObject.SetActive(true);
            lblInputResponse.text = "Waiting for second player...";
        }

        private void NavigateToCharacterSelection()
        {
            // called on game assignment
            SceneManager.LoadScene(2);
        }
        
        private void NavigateToInGameScene()
        {
            // called on game assignment
            SceneManager.LoadScene(3);
        }
        
        private void SetLabelInSelection()
        {
            // called on general assignment
            btnSubmit.gameObject.SetActive(false);
            lblInputResponse.gameObject.SetActive(true);
            lblInputResponse.text = "Players are selecting superheros...";
        }

        // ###################### Multi-Thread compatibility ########################
        
        private void Update()
        {
            while(runInUpdate.Count > 0)
            {
                Action action = null;
                lock(runInUpdate)
                {
                    if(runInUpdate.Count > 0)
                        action = runInUpdate.Dequeue();
                }
                action?.Invoke();
            }
        }
        
        public void Awake()
        {
            unitySynchronizationContext = SynchronizationContext.Current;
            unityThread = Thread.CurrentThread.ManagedThreadId;
            unityTaskScheduler = TaskScheduler.FromCurrentSynchronizationContext();
        }
        
        private static void RunOnUnityThread(Action action)
        {
            // is this right?
            if (unityThread ==Thread.CurrentThread.ManagedThreadId)
            {
                action();
            }
            else
            {
                lock(runInUpdate)
                {
                    runInUpdate.Enqueue(action);
                }
            }
        }

        // ###################### Multi-Thread compatibility ########################
        
        public void OnMessageReceived(BasicEvent message, string json)
        {
            switch (message.eventType)
            {
                case "GamestateEvent":
                    var gamestateEvent = JsonConvert.DeserializeObject<GamestateEvent>(json);
                    lock (gameState)
                    {
                        gameState.UpdateGameState(gamestateEvent);
                        RunOnUnityThread(NavigateToInGameScene);
                    }
                    break;
            }
        }
    }
}