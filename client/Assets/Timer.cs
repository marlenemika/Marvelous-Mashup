using Objects;
using UnityEngine;
using UnityEngine.UI;

public class Timer : MonoBehaviour
{
    public GameState gameState;
    public float timeValue;
    public Text timerText;
    public bool timerActivated = false;

    private void Start()
    {
        timerActivated = true;
        timeValue = gameState.matchConfig.maxRoundTime;
    }

    // Update is called once per frame
    void Update()
    {
        if (timeValue > 0 && timerActivated == true)
        {
            timeValue -= Time.deltaTime;
        }

        DisplayTime(timeValue);
    }

    void DisplayTime(float timeToDisplay)
    {
        if (timeToDisplay < 0)
        {
            timeToDisplay = 0;
        }

        float minutes = Mathf.FloorToInt(timeToDisplay / 60);
        float seconds = Mathf.FloorToInt(timeToDisplay % 60);

        timerText.text = string.Format("{0:00}:{1:00}", minutes, seconds);
    }

    public void resetTimer()
    {
        timeValue = gameState.matchConfig.maxRoundTime;;
    }

    public void pauseTimer()
    {
        timerActivated = false;
    }

    public void resumeTimer()
    {
        timerActivated = true;
    }
}
