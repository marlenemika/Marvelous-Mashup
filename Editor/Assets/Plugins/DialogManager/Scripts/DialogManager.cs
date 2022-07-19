using UnityEngine;
using UnityEngine.UI;

public class DialogManager : MonoBehaviour
{
    // Singleton reference
    private static DialogManager _instance;

    // Public getter for the singleton reference
    public static DialogManager instance
    {
        get
        {
            // If there is no instance for DialogManager yet and we're not shutting down at the moment
            if (_instance == null && !isShuttingDown)
            {
                //Try finding and instance in the scene
                _instance = GameObject.FindObjectOfType<DialogManager>();
                //If no instance was found, let's create one
                if (!_instance)
                {
                    GameObject singleton = (GameObject)Instantiate(Resources.Load("DialogManager"));
                    singleton.name = "DialogManager";
                    _instance = singleton.GetComponent<DialogManager>();
                }
                //Set the instance to persist between levels.
                //With dontDestroyOnLoad the instance seems to be not persitent across scenes
                //-> we generate a new instance on scene changed
                //DontDestroyOnLoad(_instance.gameObject);
            }
            //Return an instance, either that we found or that we created.
            return _instance;
        }
    }
    
    public static bool isShuttingDown;
    
    //Unity calls this function when quitting, I'm using that info to avoid creating
    //something when the game is quitting as unity doesn't like that.
    void OnApplicationQuit()
    {
        isShuttingDown = true;
    }

    //I made 2 types of dialogues, one that is only one OK Button
    //and another that has both an Yes and a No button which lead
    //to different callbacks.
    public enum DialogType
    {
        OkDialog,
        YesNoDialog,
        CharacterTutorialDialog
    };

    //Store the container panel that coantains both dialog boxes.
    public CanvasGroup dialogCanvasGroup;
    //Game object for the OK only dialog box
    public GameObject okDialogObject;
    //Game Object for the YesNO dialog box
    public GameObject yesNoDialogObject;
    // Game Object for the characterTutorial dialog box
    public GameObject characterTutorialDialogObject;

    //Here go the dialog texts for both dialogs.
    public Text[] dialogText;
    //Here go the tialog titles for both dialogs.
    public Text[] dialogTitle;

    //We're going to use a void delegate for the callbacks
    public delegate void dialogAnswer();
    //We have one for ok/yes
    public dialogAnswer okAnswer;
    //And one for no
    public dialogAnswer noAnswer;
    
    //Bool to check if there is already a dialog currently showing.
    public static bool showingDialog;

    void Awake()
    {
        //If there is no instance of this currently in the scene
        if (_instance == null)
        {
            //Set ourselves as the instance and mark us to persist between scenes
            _instance = this;
            //With dontDestroyOnLoad the instance seems to be not persitent across scenes
            //-> we generate a new instance on scene changed
            //DontDestroyOnLoad(this);
        }
        else
        {
            //If there is already an instance of this and It's not me, then destroy me as there should only be one.
            if (this != _instance)
                Destroy(this.gameObject);
        }
    }

    /// <summary>
    /// This is the method we'll call to show the dialog. It takes a string for title, one for text,
    /// a dialog type (Ok only or Yes/No) and 2 callbacks, one for OK/Yes and the other one for NO
    /// </summary>
    /// <param name="_title">Title of the dialog box</param>
    /// <param name="_text">Text contained in the dialog box</param>
    /// <param name="_desiredDialog">Type of the Dialog Box, either DialogType.OkDialog or DialogType.YesNoDialog</param>
    /// <param name="_dialogAnswer">Callback to call if user pressed [Ok] or [Yes] buttons</param>
    /// <param name="_dialogNegativeAnswer">Callback to call if the user presses the [No] button</param>
    public static void PopUpDialog(string _title, string _text,DialogType _desiredDialog = DialogType.OkDialog, dialogAnswer _dialogAnswer = null, dialogAnswer _dialogNegativeAnswer = null)
    {
        //If we're showing dialog already stop here.
        if (showingDialog) return;
        //Set the showing dialog bool to true to prevent another dialog over this.
        showingDialog = true;
        
        //Set our dialog boxes to show or not show based on it's desired type.
        switch (_desiredDialog)
        {
                case DialogType.OkDialog:
                    instance.okDialogObject.SetActive(true);
                    instance.yesNoDialogObject.SetActive(false);
                    instance.characterTutorialDialogObject.SetActive(false);
                break;
                case DialogType.YesNoDialog:
                    instance.okDialogObject.SetActive(false);
                    instance.yesNoDialogObject.SetActive(true);
                    instance.characterTutorialDialogObject.SetActive(false);
                break;
                case DialogType.CharacterTutorialDialog:
                    instance.okDialogObject.SetActive(false);
                    instance.yesNoDialogObject.SetActive(false);
                    instance.characterTutorialDialogObject.SetActive(true);
                break;
        }

        //Fill all the texts with the desired text.
        for (int _i = 0; _i < instance.dialogText.Length; _i++)
        {
            instance.dialogText[_i].text = _text;
        }

        //Fill all the titles with the desired title.
        for (int _i = 0; _i < instance.dialogTitle.Length; _i++)
        {
            instance.dialogTitle[_i].text = _title;
        }

        //Show the dialog canvas.
        instance.dialogCanvasGroup.gameObject.SetActive(true);

        //Set our callbacks to the ones we received.
        instance.okAnswer = _dialogAnswer;
        instance.noAnswer = _dialogNegativeAnswer;
    }

    
    public void DismissDialog(bool _answer)
    {
        //If answer is true call YES/OK delegate if one exists
        if (_answer)
        {
            if (okAnswer != null)
            {
                okAnswer();
            }
        }
        else
        {
            //If answer is false call NO delegate if one exists.
            if (noAnswer != null)
            {
                noAnswer();
            }
        }

        //Hide the gameobjects and set the showingDialog back to false to allow for new dialog calls.
        okDialogObject.SetActive(false);
        yesNoDialogObject.SetActive(false);
        characterTutorialDialogObject.SetActive(false);
        dialogCanvasGroup.gameObject.SetActive(false);
        showingDialog = false;
    }

    
}
