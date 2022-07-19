using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Security.Principal;
using Settings;
using UnityEngine;

public class InputManager : MonoBehaviour
{
    public static InputManager instance;

    public KeybindingsObject keybindingsObject;

    //  does instance exist already?
    private void Awake()
    {
        //  no: bind it
        if (instance == null)
            instance = this;

        //  yes but another object: destroy this script
        //  only one instance can exist!
        else if (instance != this)
            Destroy(this);

        // don't destroy @this when another scene is loaded
        DontDestroyOnLoad(this);

    }

    //  search pressed key in keybindings
    public bool GetKeyDown(string key)
    {
        return Input.GetKeyDown(keybindingsObject.CheckKey(key));
    }
    
    //  check if key is already a hotkey
    public bool IsAlreadyBound(KeyCode keyCode)
    {
        return keybindingsObject.KeyBound(keyCode);
    }
}
