#if UNITY_STANDALONE_WIN
using UnityEngine;
using UnityEngine.UI;

public class MatchConfigController : MonoBehaviour
{
    public MatchConfig matchConfig;
    public ConfigRepository configRepository;

    private bool updateRequired;
    
    public InputField intMaxRounds;
    public InputField intMaxRoundTime;
    public InputField intMaxGameTime;
    public InputField intMaxAnimationTime;
    public InputField intSpaceStoneCD;
    public InputField intMindStoneCD;
    public InputField intRealityStoneCD;
    public InputField intPowerStoneCD;
    public InputField intTimeStoneCD;
    public InputField intSoulStoneCD;
    public InputField intMindStoneDMG;
    public InputField intMaxPauseTime;
    public InputField intMaxResponseTime;

    public void MaxRoundsChanged()
    {
        // rounds until thanos spawns
        // unlimited to 50
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intMaxRounds.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMaxRounds.text = newValue.ToString();
        matchConfig.maxRounds = newValue;
    }
    
    public void MaxRoundTimeChanged()
    {
        // time users have to make their turn
        // 5 second to 5 minutes
        const int minValue = 5;
        const int maxValue = 300;
        
        // parse text field content to int
        var newValue = int.Parse(intMaxRoundTime.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMaxRoundTime.text = newValue.ToString();
        matchConfig.maxRoundTime = newValue;
    }
    
    public void MaxGameTimeChanged()
    {
        // time the game is limited to
        // unlimited to 1 hour
        const int minValue = 0;
        const int maxValue = 3600;
        
        // parse text field content to int
        var newValue = int.Parse(intMaxGameTime.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMaxGameTime.text = newValue.ToString();
        matchConfig.maxGameTime = newValue;
    }
    
    public void MaxAnimationTimeChanged()
    {
        // time the animation needs to complete
        // 0 seconds to 5 minutes
        const int minValue = 0;
        const int maxValue = 300;
        
        // parse text field content to int
        var newValue = int.Parse(intMaxAnimationTime.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMaxAnimationTime.text = newValue.ToString();
        matchConfig.maxAnimationTime = newValue;
    }
    
    public void SpaceStoneCdChanged()
    {
        // cooldown for space stone
        // 0 rounds to 50 rounds
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intSpaceStoneCD.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intSpaceStoneCD.text = newValue.ToString();
        matchConfig.spaceStoneCD = newValue;
    }
    
    public void MindStoneCdChanged()
    {
        // cooldown for mind stone
        // 0 rounds to 50 rounds
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intMindStoneCD.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMindStoneCD.text = newValue.ToString();
        matchConfig.mindStoneCD = newValue;
    }
    
    public void RealityStoneCdChanged()
    {
        // cooldown for reality stone
        // 0 rounds to 50 rounds
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intRealityStoneCD.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intRealityStoneCD.text = newValue.ToString();
        matchConfig.realityStoneCD = newValue;
    }
    
    public void PowerStoneCdChanged()
    {
        // cooldown for power stone
        // 0 rounds to 50 rounds
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intPowerStoneCD.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intPowerStoneCD.text = newValue.ToString();
        matchConfig.powerStoneCD = newValue;
    }
    
    public void TimeStoneCdChanged()
    {
        // cooldown for time stone
        // 0 rounds to 50 rounds
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intTimeStoneCD.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intTimeStoneCD.text = newValue.ToString();
        matchConfig.timeStoneCD = newValue;
    }
    
    public void SoulStoneCdChanged()
    {
        // cooldown for soul stone
        // 0 rounds to 50 rounds
        const int minValue = 0;
        const int maxValue = 50;
        
        // parse text field content to int
        var newValue = int.Parse(intSoulStoneCD.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intSoulStoneCD.text = newValue.ToString();
        matchConfig.soulStoneCD = newValue;
    }
    
    public void MindStoneDmgChanged()
    {
        // damage for mind stone
        // 1 health points to 999 health points
        const int minValue = 1;
        const int maxValue = 999;
        
        // parse text field content to int
        var newValue = int.Parse(intMindStoneDMG.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMindStoneDMG.text = newValue.ToString();
        matchConfig.mindStoneDMG = newValue;
    }
    
    public void MaxPauseTimeChanged()
    {
        // time the game can be paused for
        // no pause to 30 Minutes 
        const int minValue = 0;
        const int maxValue = 1800;
        
        // parse text field content to int
        var newValue = int.Parse(intMaxPauseTime.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMaxPauseTime.text = newValue.ToString();
        matchConfig.maxPauseTime = newValue;
    }
    
    public void MaxResponseTimeChanged()
    {
        // duration the server waits for a client response
        // 1 second to 10 seconds
        const int minValue = 1;
        const int maxValue = 10;
        
        // parse text field content to int
        var newValue = int.Parse(intMaxResponseTime.text);

        // case -> below allowed range
        if (newValue < minValue) newValue = minValue;
        // case -> above allowed range
        else if (newValue > maxValue) newValue = maxValue;

        intMaxResponseTime.text = newValue.ToString();
        matchConfig.maxResponseTime = newValue;
    }

    public void ImportFileClicked()
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
    
    public void ExportFileClicked()
    {
        var success = configRepository.ExportFile(ConfigType.MatchConfig);

        if (!success)
        {
            DialogManager.PopUpDialog("Export canceled",
                "The export wasn't successful, try it again and select another directory");
        }
    }
    
    private void Start()
    {
        UpdateInputs();
    }

    private void Update()
    {
        if (updateRequired) UpdateInputs();
    }

    private void UpdateInputs()
    {
        intMaxRounds.text = matchConfig.maxRounds.ToString();
        intMaxRoundTime.text = matchConfig.maxRoundTime.ToString();
        intMaxGameTime.text = matchConfig.maxGameTime.ToString();
        intMaxAnimationTime.text = matchConfig.maxAnimationTime.ToString();
        intSpaceStoneCD.text = matchConfig.spaceStoneCD.ToString();
        intMindStoneCD.text = matchConfig.mindStoneCD.ToString();
        intRealityStoneCD.text = matchConfig.realityStoneCD.ToString();
        intPowerStoneCD.text = matchConfig.powerStoneCD.ToString();
        intTimeStoneCD.text = matchConfig.timeStoneCD.ToString();
        intSoulStoneCD.text = matchConfig.soulStoneCD.ToString();
        intMindStoneDMG.text = matchConfig.mindStoneDMG.ToString();
        intMaxPauseTime.text = matchConfig.maxPauseTime.ToString();
        intMaxResponseTime.text = matchConfig.maxResponseTime.ToString();

        updateRequired = false;
    }
}
#endif