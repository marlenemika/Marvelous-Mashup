using UnityEngine;
using UnityEngine.SceneManagement;
public class NavigationScript : MonoBehaviour
{
    public void NavigateToMainMenu()
    {
        SceneManager.LoadScene(0);
    }

    public void NavigateExit()
    {
        Debug.Log("Application closed.");
        Application.Quit(0);
    }

    public void NavigateToSettings()
    {
        SceneManager.LoadScene(1);
    }
    
}