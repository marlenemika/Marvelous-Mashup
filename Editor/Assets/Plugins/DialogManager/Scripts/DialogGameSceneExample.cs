using UnityEngine;

public class DialogGameSceneExample : MonoBehaviour {

	// Update is called once per frame
	void Update () {
        //If press left mouse button and no dialog is being shown, call for a popup dialog
        //Set the Yes callback to call the TurnGreen method, and the No callback to call the TurnRed method.
	    if (Input.GetMouseButtonDown(0) && !DialogManager.showingDialog)
	    {
	        DialogManager.PopUpDialog("Wow this happened!",
                "It really happened! Wow! Much dialog!\n Did you like this dialog?",
                DialogManager.DialogType.YesNoDialog,
                TurnGreen,
                TurnRed);
	    }

        //If press right mouse button and no dialog is being shown, call for a popup dialog
        //Set the ok callback to call TurnBlue method.
        if (Input.GetMouseButtonDown(1) && !DialogManager.showingDialog)
        {
            DialogManager.PopUpDialog("Wow this happened!",
                "It really happened! Wow! Much dialog!",
                DialogManager.DialogType.OkDialog,
                TurnBlue);
        }
    }

    void TurnBlue()
    {
        gameObject.GetComponent<Renderer>().material.color = Color.blue;
    }

    void TurnGreen()
    {
        gameObject.GetComponent<Renderer>().material.color = Color.green;
    }

    void TurnRed()
    {
        gameObject.GetComponent<Renderer>().material.color = Color.red;
    }
}
