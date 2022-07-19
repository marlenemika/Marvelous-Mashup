using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Ingame.Tiles;
using Network;
using Network.Events;
using Network.Events.Character;
using Network.Events.Entity;
using Network.Events.Game;
using Network.Events.Gamestate;
using Network.Events.Notification;
using Network.Events.Portal;
using Network.Messages;
using Network.Objects;
using Network.Requests;
using Newtonsoft.Json;
using Objects;
using Settings;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

namespace Ingame
{
    public class GameController : ClientView.ClientView, INetworkListener
    {

        [SerializeField] private GridManager gridManager;
        [SerializeField] private ActionPlateManager actionPlateManager;
        [SerializeField] private InfinityStoneInventoryManager _infinityStoneInventoryManager;

        public GameObject interaction;
        
        public HorizontalLayoutGroup hlgTurnOrder;
        public GameObject OwnTurnPrefab;
        public GameObject EnemyTurnPrefab;
        public GameObject NpcTurnPrefab;

        public Text currentPlayerTextField;
        public Text currentCharacterTextField;
        public Text mpTextField;
        public Text apTextField;
        public Text currentActionTextField;

        public GameObject infinityStoneInventory;

        public Timer timer;

        public static TaskScheduler unityTaskScheduler;
        public static int unityThread;
        public static SynchronizationContext unitySynchronizationContext;
        static public Queue<Action> runInUpdate = new Queue<Action>();

        public static bool isOnUnityThread => unityThread == Thread.CurrentThread.ManagedThreadId;

        public int spectatorCount;
        public GameState gameState;

        /******************************************************************************************
         * Pause Menu
         */

        public GameObject pauseMenu,
            popup;

        private bool _locked = false,
            _updateRequired = true;

        public Slider volumeLevelSlider;
        public Text volumeLevelValue;
        public Toggle volumeActiveCheckbox;

        public Text lblMoveButton,
            lblAttackButton,
            lblFinishTurnButton,
            lblPauseMenuButton;

        public bool changeMoveClicked,
            changeAttackClicked,
            changeFinishTurnClicked,
            changePauseMenuClicked;

        public Text moveKeybind,
            attackKeybind,
            finishTurnKeybind,
            pauseMenuKeybind;

        //  character overview
        public GridLayoutGroup ownCharacters,
            foeCharacters;

        public OwnCharacterInfoScript ownCharacterPrefab, foeCharacterPrefab;

        /// <summary>
        /// Populating the grid view of the scene with the own characters inside the config file.
        /// The items use the "ownCharacterInfoPrefab" as a blue print, values individually assigned to views.
        /// </summary>
        private void PopulateOwnCharacters()
        {
            if (NetworkAdapter.Instance.Role.Equals("PLAYER"))
            {
                // clear grid layout group
                for (int i = 0; i < ownCharacters.transform.childCount; i++)
                    Destroy(ownCharacters.transform.GetChild(i).gameObject);

                List<EventEntity> ownEntities;
                lock (gameState)
                    ownEntities = gameState.entities.Where(
                        e => e.entityType == EventEntityType.Character &&
                             e.PID == 1 && gameState.assignment.Equals("PlayerOne") ||
                             e.PID == 2 && gameState.assignment.Equals("PlayerTwo")).ToList();

                ownEntities.Sort((a, b) => 
                    string.Compare(a.name, b.name, StringComparison.Ordinal));

                // for each character in config
                foreach (var character in ownEntities)
                {
                    // create new game object from prefab with previously loaded grid layout context
                    var ownCharacterInfo = Instantiate(ownCharacterPrefab, ownCharacters.transform);
                    ownCharacterInfo.Init(character);
                }
            }
            else
            {
                // clear grid layout group
                for (int i = 0; i < ownCharacters.transform.childCount; i++)
                    Destroy(ownCharacters.transform.GetChild(i).gameObject);

                List<EventEntity> ownEntities;
                lock (gameState)
                    ownEntities = gameState.entities.Where(
                        e => e.entityType == EventEntityType.Character && e.PID == 1).ToList();

                ownEntities.Sort((a, b) => 
                    string.Compare(a.name, b.name, StringComparison.Ordinal));

                // for each character in config
                foreach (var character in ownEntities)
                {
                    // create new game object from prefab with previously loaded grid layout context
                    var ownCharacterInfo = Instantiate(ownCharacterPrefab, ownCharacters.transform);
                    ownCharacterInfo.Init(character);
                }
            }
        }

        public void PopulateFoeCharacters()
        {
            if (NetworkAdapter.Instance.Role.Equals("PLAYER"))
            {
                // clear grid layout group
                for (int i = 0; i < foeCharacters.transform.childCount; i++)
                    Destroy(foeCharacters.transform.GetChild(i).gameObject);

                List<EventEntity> foeEntities;
                lock (gameState)
                    foeEntities = gameState.entities.Where(
                        e => e.entityType == EventEntityType.Character &&
                             !(e.PID == 1 && gameState.assignment.Equals("PlayerOne") ||
                               e.PID == 2 && gameState.assignment.Equals("PlayerTwo"))).ToList();

                foeEntities.Sort((a, b) => 
                    string.Compare(a.name, b.name, StringComparison.Ordinal));

                // for each character in config
                foreach (var character in foeEntities)
                {
                    // create new game object from prefab with previously loaded grid layout context
                    var foeCharacterInfo = Instantiate(foeCharacterPrefab, foeCharacters.transform);
                    foeCharacterInfo.Init(character);
                }
            }
            else
            {
                // clear grid layout group
                for (int i = 0; i < foeCharacters.transform.childCount; i++)
                    Destroy(foeCharacters.transform.GetChild(i).gameObject);

                List<EventEntity> foeEntities;
                lock (gameState)
                    foeEntities = gameState.entities.Where(
                        e => e.entityType == EventEntityType.Character && e.PID == 2).ToList();

                foeEntities.Sort((a, b) => 
                    string.Compare(a.name, b.name, StringComparison.Ordinal));

                // for each character in config
                foreach (var character in foeEntities)
                {
                    // create new game object from prefab with previously loaded grid layout context
                    var foeCharacterInfo = Instantiate(foeCharacterPrefab, foeCharacters.transform);
                    foeCharacterInfo.Init(character);
                }
            }
        }

        public void OnGameStateChanged()
        {
            lock (gameState)
            {
                // Removing all action plates
                var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
                actionPlateManager.DestroyActionPlates();
                // Drawing a new level
                gridManager.OnGameStateChanged();
                // Hide the infinity stone inventory
                infinityStoneInventory.SetActive(false);
                // Update the infinity stone inventory
                _infinityStoneInventoryManager.OnGameStateChanged();
                // Deactivate/Activate interactable GameObjects on screen
                if (!gameState.IsMyTurn() || NetworkAdapter.Instance.Role.Equals("SPECTATOR"))
                {
                    interaction.SetActive(false);
                }
                else
                {
                    interaction.SetActive(true);
                }
                // Update character and player information
                UpdateGameInformation();
                // Rerender turn order
                UpdateTurnOrder();
            }
        }

        public void Start()
        {
            NetworkAdapter.Instance.UpdateOwner(this);
            pauseMenu.SetActive(false);
        }

        public void OnEnable()
        {
            OnGameStateChanged();
        }

        public void UpdateGameInformation()
        {
            currentPlayerTextField.text =
                gameState.activeCharacter.PID == 1 ? gameState.playerOneName : gameState.playerTwoName;
            currentCharacterTextField.text = gameState.activeCharacter.name;
            mpTextField.text = "MP: " + gameState.activeCharacter.MP;
            apTextField.text = "AP: " + gameState.activeCharacter.AP;

            PopulateOwnCharacters();
            PopulateFoeCharacters();
        }

        public void OnMoveButtonClicked()
        {
            // Removing all action plates
            var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
            actionPlateManager.DestroyActionPlates();
            // Check if there are MP left for this action
            if (gameState.activeCharacter.MP > 0)
            {
                // Hide the infinity stone inventory
                infinityStoneInventory.SetActive(false);
                currentActionTextField.text = "Current Action: Move";
                actionPlateManager.GenerateActionPlatesForMove(gameState.activeCharacter);
            }
            else
            {
                currentActionTextField.text = "You don't have enough MP";
            }
        }

        public void OnAttackButtonClicked()
        {
            // Removing all action plates
            var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
            actionPlateManager.DestroyActionPlates();
            // Check if there are AP left for this action
            if (gameState.activeCharacter.AP > 0)
            {
                // Hide the infinity stone inventory
                infinityStoneInventory.SetActive(false);
                currentActionTextField.text = "Current Action: Attack";
                actionPlateManager.GenerateActionPlatesForAttack(gameState.activeCharacter);
            }
            else
            {
                currentActionTextField.text = "You don't have enough AP";
            }
        }

        public void OnUseInfinityStoneButtonClicked()
        {
            // Removing all action plates
            var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
            actionPlateManager.DestroyActionPlates();
            // Check if there are AP left for this action
            if (gameState.activeCharacter.AP > 0)
            {
                // Show inventory for infinity stone selection
                infinityStoneInventory.SetActive(true);
                InfinityStoneInventoryManager.selectedActionType = RequestType.UseInfinityStoneRequest;
                currentActionTextField.text = "Current Action: Use Infinity Stone";
            }
            else
            {
                currentActionTextField.text = "You don't have enough AP";
            }
        }

        public void OnExchangeInfinityStoneButtonClicked()
        {
            // Removing all action plates
            var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
            actionPlateManager.DestroyActionPlates();
            // Check if there are AP left for this action
            if (gameState.activeCharacter.AP > 0)
            {
                // Show inventory for infinity stone selection
                infinityStoneInventory.SetActive(true);
                InfinityStoneInventoryManager.selectedActionType = RequestType.ExchangeInfinityStoneRequest;
                currentActionTextField.text = "Current Action: Exchange Infinity Stone";
            }
            else
            {
                currentActionTextField.text = "You don't have enough AP";
            }
        }

        public async void OnEndTurnButtonClicked()
        {
            // Removing all action plates
            var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
            actionPlateManager.DestroyActionPlates();
            await NetworkAdapter.Instance.Send(new EndRoundRequest());
        }

        public async void OnPauseClicked()
        {
            if (NetworkAdapter.Instance.Role.Equals("PLAYER"))
                await NetworkAdapter.Instance.Send(new PauseStartRequest());
            else
                RunOnUnityThread(ShowPauseMenu);
        }

        public async void ResumeClicked()
        {
            if (NetworkAdapter.Instance.Role.Equals("PLAYER"))
                await NetworkAdapter.Instance.Send(new PauseStopRequest());
            else
                RunOnUnityThread(HidePauseMenu);
        }

        public async void OnLeaveGameClicked()
        {
            if (NetworkAdapter.Instance.Role.Equals("PLAYER"))
                await NetworkAdapter.Instance.Send(new PauseStopRequest());

            await NetworkAdapter.Instance.Send(new DisconnectRequest());
            SceneManager.LoadScene(0);
        }

        public async void Move(int[] targetPosition)
        {
            MoveRequest moveRequest;

            lock (gameState)
            {
                var origin = gameState.activeCharacter;
                moveRequest = new MoveRequest(origin.ToRequestEntity(), origin.position, targetPosition);
            }

            await NetworkAdapter.Instance.Send(moveRequest);
        }

        public async void MeleeAttack(int[] targetPosition)
        {
            MeleeAttackRequest meleeRequest;

            lock (gameState)
            {
                var origin = gameState.activeCharacter;
                var target = gameState.FindEntityAt(targetPosition);

                if (target == null)
                {
                    Debug.LogWarning("Entity not found");
                    return;
                }

                meleeRequest = new MeleeAttackRequest(
                    origin.ToRequestEntity(),
                    target.ToRequestEntity(),
                    origin.position,
                    targetPosition,
                    gameState.FindCharacterWithId(origin.ID).meleeDamage
                );
            }

            await NetworkAdapter.Instance.Send(meleeRequest);
        }

        public async void RangeAttack(int[] targetPosition)
        {
            RangedAttackRequest rangedAttackRequest;

            lock (gameState)
            {
                var origin = gameState.activeCharacter;
                var target = gameState.FindEntityAt(targetPosition);

                if (target == null)
                {
                    Debug.LogWarning("Entity not found");
                    return;
                }

                rangedAttackRequest = new RangedAttackRequest(
                    origin.ToRequestEntity(),
                    target.ToRequestEntity(),
                    origin.position,
                    target.position,
                    gameState.FindCharacterWithId(origin.ID).rangeCombatDamage
                );
            }

            await NetworkAdapter.Instance.Send(rangedAttackRequest);
        }

        public async void UseInfinityStone(int[] targetPosition)
        {
            UseInfinityStoneRequest useInfinityStoneRequest;

            lock (gameState)
            {
                var origin = gameState.activeCharacter;

                var stoneType = new RequestEntity(RequestEntityType.InfinityStones,
                    InfinityStoneInventoryManager.selectedInfinityStone);

                useInfinityStoneRequest = new UseInfinityStoneRequest(
                    origin.ToRequestEntity(), origin.position, targetPosition, stoneType);
            }

            await NetworkAdapter.Instance.Send(useInfinityStoneRequest);
        }

        public async void ExchangeInfinityStone(int[] targetPosition)
        {
            var origin = gameState.activeCharacter;
            var stoneType = new RequestEntity(RequestEntityType.InfinityStones,
                InfinityStoneInventoryManager.selectedInfinityStone);

            var target = gameState.FindEntityAt(targetPosition);

            var exchangeInfinityStoneRequest = new ExchangeInfinityStoneRequest(
                origin.ToRequestEntity(),
                target.ToRequestEntity(),
                origin.position,
                targetPosition, stoneType);

            await NetworkAdapter.Instance.Send(exchangeInfinityStoneRequest);
        }

        public void OnMessageReceived(BasicMessage message, string json)
        {
            switch (message.messageType)
            {
                case "HELLO_CLIENT":
                    break;
                case "GAME_ASSIGNMENT":
                    break;
                case "GENERAL_ASSIGNMENT":
                    break;
                case "CONFIRM_SELECTION":
                    break;
                case "GAME_STRUCTURE":
                    var gameStructure = JsonConvert.DeserializeObject<GameStructure>(json);
                    lock (gameState)
                    {
                        gameState.UpdateGameState(gameStructure);
                    }
                    break;
                case "GOODBYE_CLIENT":
                    var goodbyeClient = JsonConvert.DeserializeObject<GoodbyeClient>(json);
                    if (goodbyeClient == null) break;
                    RunOnUnityThread(NavigateToMainMenuScene);
                    break;
                case "ERROR":
                    break;
            }
        }

        public void OnMessageReceived(BasicEvent message, string json)
        {
            switch (message.eventType)
            {
                case "GamestateEvent":
                    var gameStateEvent = JsonConvert.DeserializeObject<GamestateEvent>(json);
                    if (gameStateEvent == null) break;
                    lock (gameState)
                    {
                        gameState.UpdateGameState(gameStateEvent);
                        RunOnUnityThread(OnGameStateChanged);
                    }
                    break;
                case "Ack":
                    var ack = JsonConvert.DeserializeObject<Ack>(json);
                    if(ack==null) break;
                    //TODO
                    break;
                case "Nack":
                    var nack = JsonConvert.DeserializeObject<Nack>(json);
                    if(nack==null) break;
                    //TODO
                    break;
                case "TakenDamageEvent":
                    var takenDamageEvent = JsonConvert.DeserializeObject<TakenDamageEvent>(json);
                    if (takenDamageEvent == null) break;
                    lock (gameState)
                    lock (gameState.statisticsObject)
                    {
                        gameState.DealDamage(takenDamageEvent.targetEntity, takenDamageEvent.amount);
                        gameState.CountDamageTaken(takenDamageEvent.targetEntity.ID, takenDamageEvent.amount);
                    }

                    RunOnUnityThread(OnGameStateChanged);
                    break;
                case "HealedEvent":
                    var healedEvent = JsonConvert.DeserializeObject<HealedEvent>(json);
                    if (healedEvent == null) break;
                    lock (gameState)
                        gameState.Heal(healedEvent.targetEntity, healedEvent.amount);
                    RunOnUnityThread(OnGameStateChanged);
                    break;
                case "ConsumedAPEvent":
                    var consumedApEvent = JsonConvert.DeserializeObject<ConsumedAPEvent>(json);
                    if(consumedApEvent==null) break;
                    lock(gameState)
                        gameState.CountActions(consumedApEvent.targetEntity.ID);
                    break;
                case "ConsumedMPEvent":
                    var consumedMpEvent = JsonConvert.DeserializeObject<ConsumedMPEvent>(json);
                    if(consumedMpEvent==null) break;
                    // TODO
                    break;
                case "SpawnEntityEvent":
                    var spawnEntityEvent = JsonConvert.DeserializeObject<SpawnEntityEvent>(json);
                    if (spawnEntityEvent == null) break;
                    lock (gameState)
                    {
                        gameState.entities =
                            (EventEntity[])gameState.entities.Concat(new[] { spawnEntityEvent.entity });
                        RunOnUnityThread(OnGameStateChanged);
                    }

                    break;
                case "DestroyedEntityEvent":
                    break;
                case "MeleeAttackEvent":
                    var meleeAttack = JsonConvert.DeserializeObject<MeleeAttackEvent>(json);
                    if (meleeAttack == null) break;
                    lock (gameState)
                    lock (gameState.statisticsObject)
                            gameState.CountMeleeAttacks(meleeAttack.originEntity.ID);
                    break;
                case "RangedAttackEvent":
                    var rangedAttack = JsonConvert.DeserializeObject<RangedAttackEvent>(json);
                    if (rangedAttack == null) break;
                    lock (gameState)
                    lock (gameState.statisticsObject)
                        gameState.CountRangeAttacks(rangedAttack.originEntity.ID);

                    break;
                case "MoveEvent":
                    break;
                case "ExchangeInfinityStoneEvent":
                    var exchangedInfinityStoneEvent = JsonConvert.DeserializeObject<ExchangeInfinityStoneEvent>(json);
                    if(exchangedInfinityStoneEvent==null) break;
                    // TODO
                    break;
                case "UseInfinityStoneEvent":
                    var useInfinityStone = JsonConvert.DeserializeObject<UseInfinityStoneEvent>(json);
                    if (useInfinityStone == null) break;
                    lock (gameState)
                    lock (gameState.statisticsObject)
                        gameState.CountInfinityStoneUsed(useInfinityStone.originEntity.ID);
                    break;
                case "TeleportedEvent":
                    break;
                case "RoundSetupEvent":
                    var roundSetupEvent = JsonConvert.DeserializeObject<RoundSetupEvent>(json);
                    if (roundSetupEvent == null) break;
                    lock (gameState)
                    {
                        gameState.turnOrder = roundSetupEvent.characterOrder;
                        RunOnUnityThread(OnGameStateChanged);
                    }

                    break;
                case "TurnEvent":
                    var turnEvent = JsonConvert.DeserializeObject<TurnEvent>(json);
                    if (turnEvent == null) break;
                    lock (gameState)
                    {
                        gameState.UpdateActiveCharacter(turnEvent.nextCharacter);
                        RunOnUnityThread(OnGameStateChanged);
                        timer.resetTimer();
                    }

                    break;
                case "WinEvent":
                    var winEvent = JsonConvert.DeserializeObject<WinEvent>(json);
                    if (winEvent == null) break;
                    lock (gameState)
                    {
                        lock (gameState.statisticsObject)
                            gameState.SetWinner(winEvent.playerWon);
                        RunOnUnityThread(NavigateToStatisticsScene);
                    }

                    break;
                case "PauseStartEvent":
                    var pauseStartEvent = JsonConvert.DeserializeObject<PauseStartEvent>(json);
                    if (pauseStartEvent == null) break;
                    RunOnUnityThread(ShowPauseMenu);
                    break;
                case "PauseStopEvent":
                    var pauseStopEvent = JsonConvert.DeserializeObject<PauseStopEvent>(json);
                    if (pauseStopEvent == null) break;
                    RunOnUnityThread(HidePauseMenu);
                    break;
                case "TurnTimeoutEvent":
                    break;
                case "TimeoutWarningEvent":
                    break;
                case "TimeoutEvent":
                    var timeoutEvent = JsonConvert.DeserializeObject<TimeoutEvent>(json);
                    if (timeoutEvent == null) break;
                    RunOnUnityThread(NavigateToStatisticsScene);
                    break;
                case "DisconnectEvent":
                    var disconnectEvent = JsonConvert.DeserializeObject<DisconnectEvent>(json);
                    if (disconnectEvent == null) break;
                    RunOnUnityThread(NavigateToStatisticsScene);
                    break;
            }
        }

        private void NavigateToMainMenuScene()
        {
            SceneManager.LoadScene(0);
        }

        private void NavigateToStatisticsScene()
        {
            SceneManager.LoadScene(4);
        }

        private void ShowPauseMenu()
        {
            pauseMenu.SetActive(true);
        }
        
        private void HidePauseMenu()
        {
            pauseMenu.SetActive(false);
        }

        // ###################### Multi-Thread compatibility ########################

        private void Update()
        {
            while (runInUpdate.Count > 0)
            {
                Action action = null;
                lock (runInUpdate)
                {
                    if (runInUpdate.Count > 0)
                        action = runInUpdate.Dequeue();
                }

                action?.Invoke();
            }

            lock (gameState)
                if (gameState.IsMyTurn() && !NetworkAdapter.Instance.Role.Equals("SPECTATOR"))
                {
                    if (Input.GetKeyDown(clientKeybindingsObject.move))
                        OnMoveButtonClicked();

                    else if (Input.GetKeyDown(clientKeybindingsObject.attack))
                        OnAttackButtonClicked();

                    else if (Input.GetKeyDown(clientKeybindingsObject.finishTurn))
                        OnEndTurnButtonClicked();
                }
            
            if (Input.GetKeyDown(clientKeybindingsObject.pauseMenu))
                OnPauseClicked();

            if (_updateRequired) UpdateUI();
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
            if (unityThread == Thread.CurrentThread.ManagedThreadId)
            {
                action();
            }
            else
            {
                lock (runInUpdate)
                {
                    runInUpdate.Enqueue(action);
                }
            }
        }

        // ###################### Multi-Thread compatibility ########################


        /*********************************************************************************
         *  Pause Menu
         */
        public void OnVolumeLevelChanged()
        {
            clientSettingsObject.volumeLevel = volumeLevelSlider.value;
            _updateRequired = true;
            MusicSettings [] objs = UnityEngine.Object.FindObjectsOfType<MusicSettings>();
            objs[0].changeVolume(volumeLevelSlider.value);
        }

        public void OnIsVolumeOnChanged()
        {
            clientSettingsObject.isVolumeOn = volumeActiveCheckbox.isOn;
            _updateRequired = true;
            MusicSettings [] objs = UnityEngine.Object.FindObjectsOfType<MusicSettings>();
            objs[0].SetMusic();
        }

        private void UpdateUI()
        {
            _updateRequired = false;

            volumeLevelSlider.value = clientSettingsObject.volumeLevel;
            volumeLevelValue.text = ToPercent(clientSettingsObject.volumeLevel);
            volumeActiveCheckbox.isOn = clientSettingsObject.isVolumeOn;

            moveKeybind.text = clientKeybindingsObject.move.ToString();
            attackKeybind.text = clientKeybindingsObject.attack.ToString();
            finishTurnKeybind.text = clientKeybindingsObject.finishTurn.ToString();
            pauseMenuKeybind.text = clientKeybindingsObject.pauseMenu.ToString();
        }

        private void UpdateTurnOrder()
        {
            foreach (Transform component in hlgTurnOrder.transform)
                Destroy(component.gameObject);

            lock (gameState)
            {
                if (NetworkAdapter.Instance.Role.Equals("PLAYER"))
                {
                    var localPlayer = gameState.assignment;

                    foreach (var e in gameState.turnOrder)
                    {
                        GameObject turnOrderTile;

                        if (
                            e.entityID == "P1" && localPlayer.Equals("PlayerOne") ||
                            e.entityID == "P2" && localPlayer.Equals("PlayerTwo"))
                            turnOrderTile = Instantiate(OwnTurnPrefab, hlgTurnOrder.transform);
                        else if (
                            e.entityID == "P1" && localPlayer.Equals("PlayerTwo") ||
                            e.entityID == "P2" && localPlayer.Equals("PlayerOne"))
                            turnOrderTile = Instantiate(EnemyTurnPrefab, hlgTurnOrder.transform);
                        else
                            turnOrderTile = Instantiate(NpcTurnPrefab, hlgTurnOrder.transform);

                        foreach (var image in turnOrderTile.GetComponentsInChildren<Image>())
                        {
                            switch (image.name)
                            {
                                case "Image":
                                    image.sprite = e.entityID == "NPC" ? 
                                        CharacterSelectionController.GetNpcImage(e.ID) : 
                                        CharacterSelectionController.GetCharacterImage(e.ID);
                                    if (
                                        e.entityID != "Rocks" &&
                                        e.entityID != "NPC" &&
                                        e.entityID != "Portals" &&
                                        gameState.FindCharacterEntityWithId(e.ID).HP == 0)
                                    {
                                        var color = image.color;
                                        color.a = 0.2f;
                                        image.color = color;
                                    }
                                    break;
                                case "Active" when gameState.activeCharacter.ID != e.ID:
                                    image.gameObject.SetActive(false);
                                    break;
                            }
                        }
                    }
                }
                else
                {
                    foreach (var e in gameState.turnOrder)
                    {
                        var turnOrderTile = e.entityID switch
                        {
                            "P1" => Instantiate(OwnTurnPrefab, hlgTurnOrder.transform),
                            "P2" => Instantiate(EnemyTurnPrefab, hlgTurnOrder.transform),
                            _ => Instantiate(NpcTurnPrefab, hlgTurnOrder.transform)
                        };

                        foreach (var image in turnOrderTile.GetComponentsInChildren<Image>())
                        {
                            switch (image.name)
                            {
                                case "Image":
                                    image.sprite = e.entityID == "NPC" ? 
                                        CharacterSelectionController.GetNpcImage(e.ID) : 
                                        CharacterSelectionController.GetCharacterImage(e.ID);
                                    if (
                                        e.entityID != "Rocks" &&
                                        e.entityID != "NPC" &&
                                        e.entityID != "Portals" &&
                                        gameState.FindCharacterEntityWithId(e.ID).HP == 0)
                                    {
                                        var color = image.color;
                                        color.a = 0.2f;
                                        image.color = color;
                                    }
                                    break;
                                case "Active" when gameState.activeCharacter.ID != e.ID:
                                    image.gameObject.SetActive(false);
                                    break;
                            }
                        }
                    }
                }
            }
        }

        public void OnOKClicked()
        {
            popup.SetActive(false);
            _locked = false;
            SetChangeDefaultState();
        }

        private void OnGUI()
        {
            var e = Event.current;

            if (Input.GetKeyUp(e.keyCode))
            {
                _locked = false;
                return;
            }

            if (!e.isKey || _locked)
                return;

            _locked = true;

            if (InputManager.instance.IsAlreadyBound(e.keyCode) && AnyButtonClicked())
            {
                SetChangeDefaultState();
                popup.SetActive(true);
                return;
            }

            if (changeMoveClicked)
            {
                clientKeybindingsObject.move = e.keyCode;
                SetChangeDefaultState();
            }

            else if (changeAttackClicked)
            {
                clientKeybindingsObject.attack = e.keyCode;
                SetChangeDefaultState();
            }

            else if (changeFinishTurnClicked)
            {
                clientKeybindingsObject.finishTurn = e.keyCode;
                SetChangeDefaultState();
            }

            else if (changePauseMenuClicked)
            {
                clientKeybindingsObject.pauseMenu = e.keyCode;
                SetChangeDefaultState();
            }

            _updateRequired = true;
        }

        private bool AnyButtonClicked()
        {
            return changeMoveClicked || changeAttackClicked
                   || changeFinishTurnClicked || changePauseMenuClicked;
        }

        private void SetChangeDefaultState()
        {
            changeMoveClicked = false;
            changeAttackClicked = false;
            changeFinishTurnClicked = false;
            changePauseMenuClicked = false;

            lblMoveButton.text = SettingsController.changeStr;
            lblAttackButton.text = SettingsController.changeStr;
            lblFinishTurnButton.text = SettingsController.changeStr;
            lblPauseMenuButton.text = SettingsController.changeStr;

            _updateRequired = true;
        }

        public void ChangeMoveClicked()
        {
            SetChangeDefaultState();
            changeMoveClicked = true;
            lblMoveButton.text = SettingsController.pressKeyStr;
        }

        public void ChangeAttackClicked()
        {
            SetChangeDefaultState();
            changeAttackClicked = true;
            lblAttackButton.text = SettingsController.pressKeyStr;
        }

        public void ChangeFinishTurnClicked()
        {
            SetChangeDefaultState();
            changeFinishTurnClicked = true;
            lblFinishTurnButton.text = SettingsController.pressKeyStr;
        }

        public void ChangePauseMenuClicked()
        {
            SetChangeDefaultState();
            changePauseMenuClicked = true;
            lblPauseMenuButton.text = SettingsController.pressKeyStr;
        }

        public static string ToPercent(float value)
        {
            return (int)(value * 100) + "%";
        }
    }
}
