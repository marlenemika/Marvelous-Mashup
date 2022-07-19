using System;
using UnityEngine;

[Serializable]
[CreateAssetMenu(fileName = "ScenarioConfigObject", menuName = "ConfigFiles/ScenarioConfigObject", order = 3)]
public class ScenarioConfig : ScriptableObject, IConfigFile
{
    public string author;
    public string name;
    public string[,] scenario;

    public ScenarioConfig()
    {
        scenario = new string[10, 10];
        for (int x = 0; x < 10; x++)
        {
            for (int y = 0; y < 10; y++)
            {
                scenario[x, y] = "GRASS";
            }
        }
    }
}