using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Settings
{
    [CreateAssetMenu(fileName = "KeybindingsObject", menuName = "ScriptableObjects/KeybindingsObject", order = 1)]
    public class KeybindingsObject : ScriptableObject
    {
        public KeyCode move,
            attack,
            finishTurn,
            pauseMenu;

        public KeyCode CheckKey(string key)
        {
            switch (key)
            {
                case "move":
                    return move;

                case "attack":
                    return attack;

                case "finishTurn":
                    return finishTurn;

                case "pauseMenu":
                    return pauseMenu;

                default:
                    return KeyCode.None;
            }
        }

        public bool KeyBound(KeyCode keyCode)
        {
            return (keyCode == move || keyCode == attack
                    || keyCode == finishTurn || keyCode == pauseMenu);
        }
    }
}