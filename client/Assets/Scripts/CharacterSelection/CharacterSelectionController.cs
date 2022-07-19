using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Network;
using Network.Events;
using Network.Events.Gamestate;
using Network.Messages;
using Newtonsoft.Json;
using Character = Network.Objects.Character;
using Objects;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class CharacterSelectionController : MonoBehaviour, INetworkListener
{
    // load predefined characters that can be selected 
    public GameState gameState;

    public List<Character> selectedCharacters;

    // bool array that will be send to the server as soon as confirmed by user
    public bool[] characters;
    private bool _selectionIsConfirmed;

    // character prefabs shown in grid
    public GridLayoutGroup characterCardLayout;
    public GameObject characterCardPrefab;

    // confirm button, at the beginning invisible
    // becomes visible when selected 6 characters
    public Button btnConfirm;
    public Text lblSelectionSuccess;

    // set to true to update all ui components at next frame
    private bool updateRequired = false;

    public static TaskScheduler unityTaskScheduler;
    public static int unityThread;
    public static SynchronizationContext unitySynchronizationContext;
    static public Queue<Action> runInUpdate= new Queue<Action>();

    public static bool isOnUnityThread => unityThread == Thread.CurrentThread.ManagedThreadId;
    
    /// <summary>
    /// Populating the grid view of the scene with the characters inside the config file.
    /// The items use the "characterChardPrefab" as a blue print, values are individually assigned to views.
    /// </summary>
    private void PopulateGridView()
    {
        // clear grid layout group
        for (int i = 0; i < characterCardLayout.transform.childCount; i++)
            Destroy(characterCardLayout.transform.GetChild(i).gameObject);

        // create game object variable out side of forEach
        GameObject characterCard;

        lock (gameState)
            // for each character in config
            foreach (var character in gameState.playerCharacterAssignment)
            {
                // create new game object from prefab with previously loaded grid layout context
                characterCard = Instantiate(characterCardPrefab, characterCardLayout.transform);

                // get components to variables
                var textComponents = characterCard.GetComponentsInChildren(typeof(Text));
                var imageComponents = characterCard.GetComponentsInChildren(typeof(Image));

                // declare ui components
                Image imgSkin = null;
                Image imgSelected = null;
                Text lblName = null;
                Text txtAP = null;
                Text txtHP = null;
                Text txtMP = null;
                Text txtDMG = null;
                Text txtRNGDMG = null;
                Text txtRNGCR = null;

                // assign components to variables by matching their names
                foreach (var component in textComponents)
                {
                    switch (component.name)
                    {
                        case "lblName":
                        {
                            lblName = component as Text;
                            break;
                        }
                        case "txtAP":
                        {
                            txtAP = component as Text;
                            break;
                        }
                        case "txtHP":
                        {
                            txtHP = component as Text;
                            break;
                        }
                        case "txtMP":
                        {
                            txtMP = component as Text;
                            break;
                        }
                        case "txtDMG":
                        {
                            txtDMG = component as Text;
                            break;
                        }
                        case "txtRNGDMG":
                        {
                            txtRNGDMG = component as Text;
                            break;
                        }
                        case "txtRNGCR":
                        {
                            txtRNGCR = component as Text;
                            break;
                        }
                    }
                }

                foreach (var component in imageComponents)
                {
                    switch (component.name)
                    {
                        case "imgSkin":
                        {
                            imgSkin = component as Image;
                            break;
                        }
                        case "imgSelected":
                        {
                            imgSelected = component as Image;
                            break;
                        }
                    }
                }

                // assign values to components after null-check
                if (imgSkin != null) imgSkin.sprite = GetCharacterImage(character.characterID);
                else Debug.LogWarning(character.name + " - Skin not found");

                // assign values to components after null-check
                if (lblName != null) lblName.text = character.name;
                else Debug.LogWarning(character.name + " - lblName TextView not found");

                // assign values to components after null-check
                if (txtAP != null) txtAP.text = character.AP.ToString();
                else Debug.LogWarning(character.name + " - txtAP TextView not found");

                // assign values to components after null-check
                if (txtHP != null) txtHP.text = character.HP.ToString();
                else Debug.LogWarning(character.name + " - txtHP TextView not found");

                // assign values to components after null-check
                if (txtMP != null) txtMP.text = character.MP.ToString();
                else Debug.LogWarning(character.name + " - txtMP TextView not found");

                // assign values to components after null-check
                if (txtDMG != null) txtDMG.text = character.meleeDamage.ToString();
                else Debug.LogWarning(character.name + " - txtDMG TextView not found");

                // assign values to components after null-check
                if (txtRNGDMG != null) txtRNGDMG.text = character.rangeCombatDamage.ToString();
                else Debug.LogWarning(character.name + " - txtRNGDMG TextView not found");

                // assign values to components after null-check
                if (txtRNGCR != null) txtRNGCR.text = character.rangeCombatReach.ToString();
                else Debug.LogWarning(character.name + " - txtRNGCR TextView not found");

                if (imgSelected != null && selectedCharacters.Contains(character))
                {
                    imgSelected.color = Color.white;
                    imgSelected.sprite = Resources.Load<Sprite>("Sprites/card_selected");
                }

                // get button component and add click listener, updating current object
                var button = characterCard.GetComponent<Button>();
                button.onClick.AddListener(() =>
                {
                    // update selectedCharacters & characters and update UI to make changes visible

                    if (_selectionIsConfirmed) return;
                    
                    // character is not yet in list and list is not full yet
                    if (selectedCharacters.Count < 6 && !selectedCharacters.Contains(character))
                    {
                        var i = getIndex(character);
                        characters[i] = true;
                        print(characters[i]);
                        selectedCharacters.Add(character);
                        print("added character: " + character.name + 
                              "\ncurrent amount of chosen characters: " + selectedCharacters.Count);
                        updateRequired = true;
                    }

                    // character is in list, will be removed
                    else if (selectedCharacters.Contains(character))
                    {
                        var i = getIndex(character);
                        characters[i] = false;
                        print(characters[i]);
                        selectedCharacters.Remove(character);
                        print("removed character: " + character.name + 
                              "\ncurrent amount of chosen characters: " + selectedCharacters.Count);
                        updateRequired = true;
                    }

                    // make it possible to confirm selection only when 6 characters are selected
                    if (selectedCharacters.Count == 6)
                    {
                        btnConfirm.gameObject.SetActive(true);
                    }

                    else btnConfirm.gameObject.SetActive(false);
                });
            }
    }

    /// <summary>
    /// finds the index of the character array so the value of the representating bool array can be changed
    /// </summary>
    /// <param name="character">selected character whose bool value should be changed</param>
    /// <returns>if found: i, otherwise: -1</returns>
    private int getIndex(Character character)
    {
        int idx = -1;
        
        lock (gameState)
            for (int i = 0; i < gameState.playerCharacterAssignment.Length; i++)
            {
                if (gameState.playerCharacterAssignment[i].characterID.Equals(character.characterID))
                {
                    idx = i;
                    break;
                }
            }

        return idx;
    }

    /// <summary>
    /// Updates all UI components connected to match configuration values to show correct data state.
    /// </summary>
    private void UpdateUI()
    {
        updateRequired = false;

        PopulateGridView();
    }

    public static Sprite GetNpcImage(int characterId)
    {
        return characterId switch
        {
            0 => Resources.Load<Sprite>("Sprites/Characters/goose"),
            1 => Resources.Load<Sprite>("Sprites/Characters/stanlee"),
            2 => Resources.Load<Sprite>("Sprites/Characters/thanos"),
            _ => Resources.Load<Sprite>("Sprites/Characters/anon")
        };
    }
    
    public static Sprite GetCharacterImage(int characterId)
    {
        return characterId switch
        {
            1 => Resources.Load<Sprite>("Sprites/Characters/rocket_raccoon"),
            2 => Resources.Load<Sprite>("Sprites/Characters/quicksilver"),
            3 => Resources.Load<Sprite>("Sprites/Characters/hulk"),
            4 => Resources.Load<Sprite>("Sprites/Characters/black_widow"),
            5 => Resources.Load<Sprite>("Sprites/Characters/hawkeye"),
            6 => Resources.Load<Sprite>("Sprites/Characters/captain_america"),
            7 => Resources.Load<Sprite>("Sprites/Characters/spiderman"),
            8 => Resources.Load<Sprite>("Sprites/Characters/dr_strange"),
            9 => Resources.Load<Sprite>("Sprites/Characters/iron_man"),
            10 => Resources.Load<Sprite>("Sprites/Characters/black_panther"),
            11 => Resources.Load<Sprite>("Sprites/Characters/thor"),
            12 => Resources.Load<Sprite>("Sprites/Characters/captain_marvel"),
            13 => Resources.Load<Sprite>("Sprites/Characters/groot"),
            14 => Resources.Load<Sprite>("Sprites/Characters/starlord"),
            15 => Resources.Load<Sprite>("Sprites/Characters/gamora"),
            16 => Resources.Load<Sprite>("Sprites/Characters/ant_man"),
            17 => Resources.Load<Sprite>("Sprites/Characters/vision"),
            18 => Resources.Load<Sprite>("Sprites/Characters/deadpool"),
            19 => Resources.Load<Sprite>("Sprites/Characters/loki"),
            20 => Resources.Load<Sprite>("Sprites/Characters/silver_surfer"),
            21 => Resources.Load<Sprite>("Sprites/Characters/mantis"),
            22 => Resources.Load<Sprite>("Sprites/Characters/ghost_rider"),
            23 => Resources.Load<Sprite>("Sprites/Characters/jessica_jones"),
            24 => Resources.Load<Sprite>("Sprites/Characters/scarlet_witch"),
            _ => Resources.Load<Sprite>("Sprites/Characters/anon"),
        };
    }

    // Start is called before the first frame update
    void Start()
    {
        NetworkAdapter.Instance.UpdateOwner(this);
        
        selectedCharacters = new List<Character>();
        characters = new bool[12]; 
        UpdateUI();
    }

    // Send bool array with selected characters to server
    public async void SendMessage()
    {
        await NetworkAdapter.Instance.Send(new CharacterSelection(characters));
    }

    public void OnMessageReceived(BasicMessage message, string json)
    {
        switch (message.messageType)
        {
            case "CONFIRM_SELECTION":
                _selectionIsConfirmed = true;
                RunOnUnityThread(ShowSuccessLabel);
                break;
            case "GAME_STRUCTURE":
                var gameStructure = JsonConvert.DeserializeObject<GameStructure>(json);
                lock (gameState)
                {
                    gameState.UpdateGameState(gameStructure);
                    RunOnUnityThread(NavigateToInGameScene);
                }
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
                    gameState.UpdateGameState(gameStateEvent);
                break;
        }
    }

    private void ShowSuccessLabel()
    {
        btnConfirm.gameObject.SetActive(false);
        lblSelectionSuccess.gameObject.SetActive(true);
    }

    private void NavigateToInGameScene()
    {
        SceneManager.LoadScene(3);
    }

    // ###################### Multi-Thread compatibility ########################
        
    private void Update()
    {
        if (updateRequired) UpdateUI();

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
}
