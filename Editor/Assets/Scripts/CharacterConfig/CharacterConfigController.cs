using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class CharacterConfigController : MonoBehaviour
{
    public ConfigRepository configRepository;
    public CharacterConfig characterConfig;
    
    // currently visible character
    public Character currCharacter;

    // character object editor components
    public Image imgSkin;
    public TMP_Text lblName;
    public InputField intAp;
    public InputField intMp;
    public InputField intHp;
    public InputField intMeleeDamage;
    public InputField intRangeCombatDamage;
    public InputField intRangeCombatReach;

    // prefab blueprint for character card
    public GameObject characterCardPrefab;
    
    // link to character layout view
    public GridLayoutGroup characterCardLayout;
    
    // set to true to update all ui components at next frame
    private bool updateRequired = false;

    public void onImportClicked()
    {
        var success = configRepository.LoadConfigurationFile();

        if (!success)
        {
            DialogManager.PopUpDialog("Import canceled",
                "The import wasn't successful, either because no file was selected or the selected file's format was invalid.");
            return;
        }
        
        updateRequired = true;
    }
    
    public void onExportClicked()
    {
        var success = configRepository.ExportFile(ConfigType.CharacterConfig);

        if (!success)
        {
            DialogManager.PopUpDialog("Export canceled",
                "The export wasn't successful, try it again and select another directory");
        }
    }
    
    public void onHelpClicked()
    {
        DialogManager.PopUpDialog("", "", DialogManager.DialogType.CharacterTutorialDialog);
    }

    public void HPChanged()
    {
        currCharacter.HP = int.Parse(intHp.text);
        
        // if HP value is set to zero, set to minimum (1)
        if (currCharacter.HP <= 0)
        {
            currCharacter.HP = 1;
        }
        
        // if HP value is set to a number larger than 250, set to maximum (250)
        else if (currCharacter.HP > 250)
        {
            currCharacter.HP = 250;
        }
        
        updateRequired = true;
    }

    public void MPChanged()
    {
        currCharacter.MP = int.Parse(intMp.text);
        
        // if MP value is set to zero, set to minimum (1)
        if (currCharacter.MP <= 0)
        {
            currCharacter.MP = 1;
        }
        
        // if MP value is set to a number larger than 15, set to maximum (15)
        else if (currCharacter.MP > 100)
        {
            currCharacter.MP = 100;
        }
        
        updateRequired = true;
    }

    public void APChanged()
    {
        currCharacter.AP = int.Parse(intAp.text);
        
        // if AP value is set to zero, set to minimum (1)
        if (currCharacter.AP <= 0)
        {
            currCharacter.AP = 1;
        }
        
        // if AP value is set to a number larger than 10, set to maximum (10)
        else if (currCharacter.AP > 25)
        {
            currCharacter.AP = 25;
        }
        
        updateRequired = true;
    }

    public void meleeDamageChanged()
    {
        currCharacter.meleeDamage = int.Parse(intMeleeDamage.text);
        
        // if melee damage value is set to zero, set to minimum (1)
        if (currCharacter.meleeDamage <= 0)
        {
            currCharacter.meleeDamage = 1;
            
        }
        
        // if melee damage value is set to a number larger than 50, set to maximum (50)
        else if (currCharacter.meleeDamage > 100)
        {
            currCharacter.meleeDamage = 100;
        }
        
        updateRequired = true;
    }

    public void rangeCombatDamageChanged()
    { 
        currCharacter.rangeCombatDamage = int.Parse(intRangeCombatDamage.text);
        
        // if range combat damage value is set to zero, set to minimum (1)
        if (currCharacter.rangeCombatDamage <= 0)
        {
            currCharacter.rangeCombatDamage = 1;
        }
        
        // if range combat damage value is set to a number larger than 30, set to maximum (30)
        else if (currCharacter.rangeCombatDamage > 100)
        {
            currCharacter.rangeCombatDamage = 100;
        }
        
        updateRequired = true;
    }

    public void rangeCombatReachChanged()
    {
        currCharacter.rangeCombatReach = int.Parse(intRangeCombatReach.text);
        
        // if range combat reach value is set to zero, set to minimum (1)
        if (currCharacter.rangeCombatReach <= 0)
        {
            currCharacter.rangeCombatReach = 1;
        }
        
        // if range combat reach value is set to a number larger than 5, set to maximum (5)
        else if (currCharacter.rangeCombatReach > 100)
        {
            currCharacter.rangeCombatReach = 100;
        }
        
        updateRequired = true;
    }

    private void Start()
    {
        currCharacter = characterConfig.characters[0];

        UpdateUI();
    }

    private void Update()
    {
        if (updateRequired) UpdateUI();
    }

    /// <summary>
    /// Updates all UI components connected to match configuration values to show correct data state.
    /// </summary>
    private void UpdateUI()
    {
        updateRequired = false;

        imgSkin.sprite = GetCharacterImage(currCharacter.characterID);
        lblName.text = currCharacter.name;
        intAp.text = currCharacter.AP.ToString();
        intMp.text = currCharacter.MP.ToString();
        intHp.text = currCharacter.HP.ToString();
        intMeleeDamage.text = currCharacter.meleeDamage.ToString();
        intRangeCombatDamage.text = currCharacter.rangeCombatDamage.ToString();
        intRangeCombatReach.text = currCharacter.rangeCombatReach.ToString();
        
        PopulateGridView();
    }

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
        
        // for each character in config
        foreach (var character in characterConfig.characters)
        {
            // create new game object from prefab with previously loaded grid layout context
            characterCard = Instantiate(characterCardPrefab, characterCardLayout.transform);

            // get components to variables
            var textComponents = characterCard.GetComponentsInChildren(typeof(Text));
            var imageComponents = characterCard.GetComponentsInChildren(typeof(Image));

            // declare ui components
            Image imgSkin = null;
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
            
            // get button component and add click listener, updating current object
            var button = characterCard.GetComponent<Button>();
            button.onClick.AddListener(() =>
            {
                // update current character and update UI to make changes visible
                currCharacter = character;
                updateRequired = true;
            });
        }
    }

    private static Sprite GetCharacterImage(int characterId)
    {
        return characterId switch
        {
            1  => Resources.Load<Sprite>("Sprites/Characters/rocket_raccoon"),
            2  => Resources.Load<Sprite>("Sprites/Characters/quicksilver"),
            3  => Resources.Load<Sprite>("Sprites/Characters/hulk"),
            4  => Resources.Load<Sprite>("Sprites/Characters/black_widow"),
            5  => Resources.Load<Sprite>("Sprites/Characters/hawkeye"),
            6  => Resources.Load<Sprite>("Sprites/Characters/captain_america"),
            7  => Resources.Load<Sprite>("Sprites/Characters/spiderman"),
            8  => Resources.Load<Sprite>("Sprites/Characters/dr_strange"),
            9  => Resources.Load<Sprite>("Sprites/Characters/iron_man"),
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
}