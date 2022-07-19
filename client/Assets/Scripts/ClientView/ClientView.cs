using Network;
using Settings;
using UnityEngine;

namespace ClientView
{
    public abstract class ClientView : MonoBehaviour
    {
        public KeybindingsObject clientKeybindingsObject;
        public SettingsObject clientSettingsObject;
        public NetworkAdapter networkAdapterObject;
    }
}