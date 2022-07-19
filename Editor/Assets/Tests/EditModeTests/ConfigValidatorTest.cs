using System;
using System.IO;
using NUnit.Framework;

public class ConfigValidatorTest
{
    // Test if tests work 
    [Test]
    public void ConfigValidatorTestSimplePasses()
    {
        Assert.True(true);
    }
    // test  if MatchConfigs are validated correct
    [Test]
    public void ConfigValidatorTestMatchConfig()
    {
        bool testTrue = ConfigValidator.Instance.ValidateFile(ConfigType.MatchConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/partieexample.game.json"));
        Assert.True(testTrue);
        bool testFalse = ConfigValidator.Instance.ValidateFile(ConfigType.MatchConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/partieexamplefalse.game.json"));
        Assert.False(testFalse);
    }
    
    // test  if scenarioConfigs are validated correct
    [Test]
    public void ConfigValidatorTestSzenarioConfig()
    {
        bool testTrue = ConfigValidator.Instance.ValidateFile(ConfigType.ScenarioConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/scenarioexample.scenario.json"));
        Assert.True(testTrue);
        bool testFalse = ConfigValidator.Instance.ValidateFile(ConfigType.ScenarioConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/scenarioexamplefalse.scenario.json"));
        Assert.False(testFalse);
    }
    
    // test  if CharacterConfigs are validated correct
    [Test]
    public void ConfigValidatorTestCharacterConfig()
    {
        bool testTrue = ConfigValidator.Instance.ValidateFile(ConfigType.CharacterConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/characterConfig.character.json"));
        Assert.True(testTrue);
        bool testFalse = ConfigValidator.Instance.ValidateFile(ConfigType.CharacterConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/characterConfigFalse.character.json"));
        Assert.False(testFalse);
    }

    //test what happens if no json file was submited
    [Test]
    public void ConfigValidatorTestNoJson()
    {
        bool testFalse = ConfigValidator.Instance.ValidateFile(ConfigType.ScenarioConfig,
            LoadJson("Assets/Tests/EditModeTests/Configs/noJson.json"));
        Assert.False(testFalse);
    }
    public String LoadJson(String file)
    {
        using (StreamReader r = new StreamReader(file))
        {
            string json = r.ReadToEnd();
            return json;
        }
    }
}
