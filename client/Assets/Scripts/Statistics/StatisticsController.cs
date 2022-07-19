using UnityEngine;
using UnityEngine.UI;

namespace Statistics
{
    public class StatisticsController : MonoBehaviour
    {
        public StatisticsObject statisticsObject;

        public Text playerOne;
        public Text playerTwo;

        public Text lblTakenDamage1;
        public Text lblTakenDamage2;

        public Text lblActionsPerformed1;
        public Text lblActionsPerformed2;

        public Text lblMeleeAttacks1;
        public Text lblMeleeAttacks2;

        public Text lblRangeAttacks1;
        public Text lblRangeAttacks2;

        public Text lblInfinityStonesUsed1;
        public Text lblInfinityStonesUsed2;

        public Text lblWinner;


        // Start is called before the first frame update
        void Start()
        {
            UpdateUI();
        }

        private void UpdateUI()
        {
            playerOne.text = statisticsObject.playerOne;
            playerTwo.text = statisticsObject.playerTwo;
            
            lblTakenDamage1.text = statisticsObject.takenDamage1.ToString();
            lblTakenDamage2.text = statisticsObject.takenDamage2.ToString();
            
            lblActionsPerformed1.text = statisticsObject.actionsPerformed1.ToString();
            lblActionsPerformed2.text = statisticsObject.actionsPerformed2.ToString();

            lblMeleeAttacks1.text = statisticsObject.meleeAttacks1.ToString();
            lblMeleeAttacks2.text = statisticsObject.meleeAttacks2.ToString();

            lblRangeAttacks1.text = statisticsObject.rangeAttacks1.ToString();
            lblRangeAttacks2.text = statisticsObject.rangeAttacks2.ToString();
            
            lblInfinityStonesUsed1.text = statisticsObject.infinityStonesUsed1.ToString();
            lblInfinityStonesUsed2.text = statisticsObject.infinityStonesUsed2.ToString();

            lblWinner.text = statisticsObject.playerWon switch
            {
                1 => $"{statisticsObject.playerOne} won the game!",
                2 => $"{statisticsObject.playerTwo} won the game!",
                _ => ""
            };
        }
    }
}
