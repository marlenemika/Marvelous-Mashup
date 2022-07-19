using Ingame;
using Network.Objects;
using Network.Requests;
using Objects;
using UnityEngine;
using UnityEngine.UI;

public class InfinityStoneInventoryManager : MonoBehaviour
{
    public GameState gameState;
    public GameObject spaceStoneSlot;
    public GameObject mindStoneSlot;
    public GameObject realityStoneSlot;
    public GameObject powerStoneSlot;
    public GameObject timeStoneSlot;
    public GameObject soulStoneSlot;
    public Text currentActionTextField;
    
    public static int selectedInfinityStone;
    public static string selectedActionType; 

    /// <summary>
    /// Updates the infinity stone inventory
    /// </summary>
    public void OnGameStateChanged()
    {
        spaceStoneSlot.SetActive(false);
        mindStoneSlot.SetActive(false);
        realityStoneSlot.SetActive(false);
        powerStoneSlot.SetActive(false);
        timeStoneSlot.SetActive(false);
        soulStoneSlot.SetActive(false);

        var stones = gameState.activeCharacter.stones;
        var stoneCooldowns = gameState.stoneCooldowns;

        if (stones == null) return;
        
        foreach (var stone in stones)
        {
            switch (stone)
            {
                case 0:
                    if (stoneCooldowns[0] == 2)
                    {
                        spaceStoneSlot.SetActive(true);
                    }
                    break;
                case 1:
                    if (stoneCooldowns[1] == 2)
                    {
                        mindStoneSlot.SetActive(true);
                    }
                    break;
                case 2:
                    if (stoneCooldowns[2] == 2)
                    {
                        realityStoneSlot.SetActive(true);
                    }
                    break;
                case 3:
                    if (stoneCooldowns[3] == 2)
                    {
                        powerStoneSlot.SetActive(true);
                    }
                    break;
                case 4:
                    if (stoneCooldowns[4] == 2)
                    {
                        timeStoneSlot.SetActive(true);
                    }
                    break;
                case 5:
                    if (stoneCooldowns[5] == 2)
                    {
                        soulStoneSlot.SetActive(true);
                    }
                    break;
            }
        }
    }

    /// <summary>
    /// Marks as ready to use the space infinity stone
    /// </summary>
    public void OnSpaceStoneClicked()
    {
        selectedInfinityStone = 0;
        currentActionTextField.text = "Current Action: Space Stone";
        callGenerateInfinityStoneActionPlates(selectedInfinityStone);
    }
    
    /// <summary>
    /// Marks as ready to use the mind infinity stone
    /// </summary>
    public void OnMindStoneClicked()
    {
        selectedInfinityStone = 1;
        currentActionTextField.text = "Current Action: Mind Stone";
        callGenerateInfinityStoneActionPlates(selectedInfinityStone);
    }
    
    /// <summary>
    /// Marks as ready to use the reafity infinity stone
    /// </summary>
    public void OnRealityStoneClicked()
    {
        selectedInfinityStone = 2;
        currentActionTextField.text = "Current Action: Reality Stone";
        callGenerateInfinityStoneActionPlates(selectedInfinityStone);
    }
    
    /// <summary>
    /// Marks as ready to use the power infinity stone
    /// </summary>
    public void OnPowerStoneClicked()
    {
        selectedInfinityStone = 3;
        currentActionTextField.text = "Current Action: Power Stone";
        callGenerateInfinityStoneActionPlates(selectedInfinityStone);
    }
    
    /// <summary>
    /// Marks as ready to use the time infinity stone
    /// </summary>
    public void OnTimeStoneClicked()
    {
        selectedInfinityStone = 4;
        currentActionTextField.text = "Current Action: Time Stone";
        callGenerateInfinityStoneActionPlates(selectedInfinityStone);
    }
    
    /// <summary>
    /// Marks as ready to use the soul infinity stone
    /// </summary>
    public void OnSoulStoneClicked()
    {
        selectedInfinityStone = 5;
        currentActionTextField.text = "Current Action: Soul Stone";
        callGenerateInfinityStoneActionPlates(selectedInfinityStone);
    }

    /// <summary>
    /// Calls the ActionPlateManager to generate necessary action plates either for use or exchange infinity stone
    /// </summary>
    /// <param name="infinityStoneType"></param>
    public void callGenerateInfinityStoneActionPlates(int infinityStoneType)
    {
        var actionPlateManager = GameObject.Find("ActionPlateManager").GetComponent<ActionPlateManager>();
        actionPlateManager.DestroyActionPlates();
        if (selectedActionType.Equals(RequestType.UseInfinityStoneRequest))
        {
            actionPlateManager.GenerateActionPlatesForInfinityStone(gameState.activeCharacter, true, infinityStoneType);   
        }
        else if (selectedActionType.Equals(RequestType.ExchangeInfinityStoneRequest))
        {
            actionPlateManager.GenerateActionPlatesForInfinityStone(gameState.activeCharacter, false, infinityStoneType);
        }
    }
}
