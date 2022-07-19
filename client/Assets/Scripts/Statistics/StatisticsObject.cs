using UnityEngine;

namespace Statistics
{
    [CreateAssetMenu(fileName = "StatisticsObject", menuName = "ScriptableObjects/StatisticsObject", order = 1)]
    public class StatisticsObject : ScriptableObject
    {
        public int playerWon;
        
        public string playerOne = "Player One";
        public string playerTwo = "Player Two";

        public int takenDamage1;
        public int takenDamage2;

        public int actionsPerformed1;
        public int actionsPerformed2;

        public int meleeAttacks1;
        public int meleeAttacks2;

        public int rangeAttacks1;
        public int rangeAttacks2;

        public int infinityStonesUsed1;
        public int infinityStonesUsed2;
    }
}