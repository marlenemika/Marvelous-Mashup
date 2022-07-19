using System;
using UnityEngine;

[Serializable]
[CreateAssetMenu(fileName = "MatchConfigObject", menuName = "ConfigFiles/MatchConfigObject", order = 3)]
public class MatchConfig : ScriptableObject, IConfigFile
{
    public int maxRounds = 5;
    public int maxRoundTime = 45;
    public int maxGameTime = 0;
    public int maxAnimationTime = 0;
    public int spaceStoneCD = 2;
    public int mindStoneCD = 2;
    public int realityStoneCD = 2;
    public int powerStoneCD = 2;
    public int timeStoneCD = 2;
    public int soulStoneCD = 2;
    public int mindStoneDMG = 35;
    public int maxPauseTime = 60;
    public int maxResponseTime = 5;
}
