using UnityEngine;
using UnityEngine.SceneManagement;

public class NavigationScript : MonoBehaviour
{
    public void NavigateToMainMenu()
    {
        SceneManager.LoadScene(0);
    }
    
    public void NavigateToCharacterConfigEditor()
    {
        SceneManager.LoadScene(1);
    }
    
    public void NavigateToScenarioConfigEditor()
    {
        SceneManager.LoadScene(2);
    }
    
    public void NavigateToMatchConfigEditor()
    {
        SceneManager.LoadScene(3);
    }

    public void NavigateExit()
    {
        Application.Quit(0);
    }

    #if !UNITY_EDITOR
    
    public void Start()
    {

        MinimumWindowSize.Set(1150, 700);
    }

    public void OnApplicationQuit()
    {
        MinimumWindowSize.Reset();
    }
    #endif
}