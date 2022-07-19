#if UNITY_STANDALONE_WIN
using System;
using UnityEngine;

/// <summary>
/// Representing a clean IO interface to the rest of the code layer.
/// </summary>
[CreateAssetMenu(fileName = "ConfigRepository", menuName = "ScriptableObjects/ConfigRepository")]
public class ConfigRepository : ScriptableObject
{
    // connected to all configuration objects for im- and exporting
    public CharacterConfig characterConfig;
    public ScenarioConfig scenarioConfig;
    public MatchConfig matchConfig;

    /// <summary>
    /// Import a file using the ConfigLoader class and if the file is valid configuration file, overwrite the local
    /// configuration object with the imported values.
    /// </summary>
    /// <returns>A bool indicating whether the object was overwritten or not</returns>
    public bool LoadConfigurationFile()
    {
        // get file by user selecting it in explorer
        var file = 
            ConfigLoader.Instance.LoadConfigurationFile();

        // if imported file was invalid, return false
        if (file == null) return false;
        
        switch (file.Item1)
        {
            // case -> file is character config
            case ConfigType.CharacterConfig:
                JsonUtility.FromJsonOverwrite(file.Item2, characterConfig);
                return true;

            // case -> file is scenario config
            case ConfigType.ScenarioConfig:
            {
                DeszerializeScenarioConfig(file.Item2);
                return true;
            }

            // case -> file is match config
            case ConfigType.MatchConfig:
                JsonUtility.FromJsonOverwrite(file.Item2, matchConfig);
                return true;
            
            default: return false;
        }
    }

    public bool ExportFile(ConfigType configType)
    {
        // parse json string depending on configType to be exported
        string configFile = configType switch
        {
            ConfigType.CharacterConfig => JsonUtility.ToJson(characterConfig),
            ConfigType.ScenarioConfig => SzerializeScenarioConfig(),
            ConfigType.MatchConfig => JsonUtility.ToJson(matchConfig),
            _ => null
        };

        return
            configFile != null && // conversion successful
            ConfigValidator.Instance.ValidateFile(configType, configFile) && // validation successful
            ConfigExporter.Instance.ExportConfigurationFile(configType, configFile); // export successful
    }

    private string SzerializeScenarioConfig()
    {
        var scenarioString = "{\"scenario\":[";
        var scenario = scenarioConfig.scenario;

        var columnCount = scenario.GetLength(0);
        var rowCount = scenario.GetLength(1);

        Debug.Log("[" + rowCount  + ", " + columnCount  + "]");
        
        for (var y = 0; y < rowCount; y++)
        {
            scenarioString += "[";
            for (var x = 0; x < columnCount; x++)
            {
                switch (scenario[x, y])
                {
                    case ScenarioConfigController.GRASS:
                        scenarioString += "\"GRASS\"";
                        break;
                    case ScenarioConfigController.ROCK:
                        scenarioString += "\"ROCK\"";
                        break;
                    case ScenarioConfigController.PORTAL:
                        scenarioString += "\"PORTAL\"";
                        break;
                }
                if (x < columnCount - 1)
                {
                    scenarioString += ",";
                }
            }
            scenarioString += "]";
            if (y < rowCount - 1)
            {
                scenarioString += ",";
            }
        }

        scenarioString += "],\"author\": \"" + scenarioConfig.author + "\",\"name\": \"" + scenarioConfig.name + "\"}";

        return scenarioString;
    }


    private void DeszerializeScenarioConfig(string jsonString) 
    {
        // extract string that represents the scenario array
        var startOfArray = jsonString.IndexOf('[') + 1;
        var endOfArray = jsonString.LastIndexOf(']') - 1;
        var arrayString = jsonString.Substring(startOfArray, endOfArray);
        
        // calculate row amount
        var rows = arrayString.Split('[').Length - 1;

        // calculate col amount
        var startOfFirstRow = arrayString.IndexOf('[');
        var endOfFirstRow = arrayString.IndexOf(']');
        var firstRowString = arrayString.Substring(startOfFirstRow, endOfFirstRow);
        var cols = firstRowString.Split(',').Length;
        
        // extract scenario from json
        string[,] scenario = new string[cols, rows];
        for (int currentRow = 0; currentRow < rows; currentRow++) 
        {
            for (int currentCol = 0; currentCol < cols; currentCol++) 
            {
                // add next field to array
                var nextGrassIndex = arrayString.IndexOf("GRASS", StringComparison.Ordinal);
                var nextRockIndex = arrayString.IndexOf("ROCK", StringComparison.Ordinal);

                if (nextGrassIndex < nextRockIndex || nextRockIndex == -1) 
                {
                    scenario[currentCol, currentRow] = "GRASS";
                    arrayString = arrayString.Substring(nextGrassIndex + 1);
                }
                else if(nextGrassIndex > nextRockIndex || nextGrassIndex == -1)
                {
                    scenario[currentCol, currentRow] = "ROCK";
                    arrayString = arrayString.Substring(nextRockIndex + 1);
                }
            }
        }

        JsonUtility.FromJsonOverwrite(jsonString, scenarioConfig);

        scenarioConfig.scenario = scenario;
    }
}
#endif