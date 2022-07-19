using System;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema;
using UnityEngine;

public sealed class ConfigValidator
{
    private ConfigValidator() 
    {}
    
   //Validates a given string against a json schema
    public bool ValidateFile(ConfigType configType, string json)
    {
        try
        {
            JObject config = JObject.Parse(json);
            TextAsset schemaJson = null;
            
            switch (configType)
            {
                case ConfigType.CharacterConfig:
                    schemaJson = Resources.Load("JsonSchemas/character_config_schema") as TextAsset;
                    break;
                case ConfigType.ScenarioConfig:
                    schemaJson = Resources.Load("JsonSchemas/scenario_config_schema") as TextAsset;
                    break;
                case ConfigType.MatchConfig:
                    schemaJson = Resources.Load("JsonSchemas/match_config_schema") as TextAsset;
                    break;
            }

            JSchema schema = JSchema.Parse(schemaJson.text);

            bool valid = config.IsValid(schema);
            
            if (!valid) Debug.LogWarning("The file is not valid");
            
            return valid;
        }
        catch (Exception e)
        {
            Debug.LogWarning(e.ToString());
            return false;
        }
        
    }
    
    public static ConfigValidator Instance => Lazy.Value;

    private static readonly Lazy<ConfigValidator> Lazy = new Lazy<ConfigValidator>(() => new ConfigValidator());
}
