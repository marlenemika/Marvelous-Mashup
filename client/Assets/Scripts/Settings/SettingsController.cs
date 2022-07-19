using Network;
using Network.Events;
using Network.Messages;
using UnityEngine;
using UnityEngine.UI;
using Slider = UnityEngine.UI.Slider;
using Toggle = UnityEngine.UI.Toggle;

namespace Settings
{
    public class SettingsController : MonoBehaviour, INetworkListener
    {
        public SettingsObject settingsObject;
        public KeybindingsObject keybindingsObject;

        public InputField usernameInputField;
        public Slider volumeLevelSlider;
        public Text volumeLevelValue;
        public Toggle volumeActiveCheckbox;

        public readonly static string changeStr = "Change",
            pressKeyStr = "Press Key";

        public GameObject popup;

        private bool _locked = false;

        public Text lblMoveButton,
            lblAttackButton,
            lblFinishTurnButton,
            lblPauseMenuButton;

        public bool changeMoveClicked,
            changeAttackClicked,
            changeFinishTurnClicked,
            changePauseMenuClicked;

        public Text moveKeybind,
            attackKeybind,
            finishTurnKeybind,
            pauseMenuKeybind;


        // set to true to update all ui components at next frame
        private bool _updateRequired = true;

        public void OnUsernameChanged()
        {
            settingsObject.username = usernameInputField.text;
            _updateRequired = true;
        }

        public void OnVolumeLevelChanged()
        {
            settingsObject.volumeLevel = volumeLevelSlider.value;
            _updateRequired = true;
        }

        public void OnIsVolumeOnChanged()
        {
            settingsObject.isVolumeOn = volumeActiveCheckbox.isOn;
            _updateRequired = true;
            MusicSettings [] objs = UnityEngine.Object.FindObjectsOfType<MusicSettings>();
            objs[0].SetMusic();
        }

        public static string ToPercent(float value)
        {
            return (int) (value * 100) + "%";
        }

        private void Start()
        {
            NetworkAdapter.Instance.UpdateOwner(this);
        }

        public void Update()
        {
            if (_updateRequired) UpdateUI();
        }

        private void UpdateUI()
        {
            _updateRequired = false;

            usernameInputField.text = settingsObject.username;
            volumeLevelSlider.value = settingsObject.volumeLevel;
            volumeLevelValue.text = ToPercent(settingsObject.volumeLevel);
            volumeActiveCheckbox.isOn = settingsObject.isVolumeOn;

            moveKeybind.text = keybindingsObject.move.ToString();
            attackKeybind.text = keybindingsObject.attack.ToString();
            finishTurnKeybind.text = keybindingsObject.finishTurn.ToString();
            pauseMenuKeybind.text = keybindingsObject.pauseMenu.ToString();
        }

        public void OnMessageReceived(BasicMessage message, string json)
        {
            // no messages expected -> nothing to handle
        }

        public void OnMessageReceived(BasicEvent message, string json)
        {
            // no events expected -> nothing to handle
        }

        public void OnOKClicked()
        {
            popup.SetActive(false);
            _locked = false;
            SetChangeDefaultState();
        }

        private void OnGUI()
        {
            var e = Event.current;

            if (Input.GetKeyUp(e.keyCode))
            {
                _locked = false;
                return;
            }

            if (!e.isKey || _locked)
                return;

            _locked = true;

            if (InputManager.instance.IsAlreadyBound(e.keyCode) && AnyButtonClicked())
            {
                SetChangeDefaultState();
                popup.SetActive(true);
                return;
            }

            if (changeMoveClicked)
            {
                keybindingsObject.move = e.keyCode;
                SetChangeDefaultState();
            }

            else if (changeAttackClicked)
            {
                keybindingsObject.attack = e.keyCode;
                SetChangeDefaultState();
            }

            else if (changeFinishTurnClicked)
            {
                keybindingsObject.finishTurn = e.keyCode;
                SetChangeDefaultState();
            }

            else if (changePauseMenuClicked)
            {
                keybindingsObject.pauseMenu = e.keyCode;
                SetChangeDefaultState();
            }

            _updateRequired = true;
        }

        private bool AnyButtonClicked()
        {
            return changeMoveClicked || changeAttackClicked
                   || changeFinishTurnClicked || changePauseMenuClicked;
        }

        private void SetChangeDefaultState()
        {
            changeMoveClicked = false;
            changeAttackClicked = false;
            changeFinishTurnClicked = false;
            changePauseMenuClicked = false;

            lblMoveButton.text = changeStr;
            lblAttackButton.text = changeStr;
            lblFinishTurnButton.text = changeStr;
            lblPauseMenuButton.text = changeStr;

            _updateRequired = true;
        }

        public void ChangeMoveClicked()
        {
            SetChangeDefaultState();
            changeMoveClicked = true;
            lblMoveButton.text = pressKeyStr;
        }

        public void ChangeAttackClicked()
        {
            SetChangeDefaultState();
            changeAttackClicked = true;
            lblAttackButton.text = pressKeyStr;
        }

        public void ChangeFinishTurnClicked()
        {
            SetChangeDefaultState();
            changeFinishTurnClicked = true;
            lblFinishTurnButton.text = pressKeyStr;
        }

        public void ChangePauseMenuClicked()
        {
            SetChangeDefaultState();
            changePauseMenuClicked = true;
            lblPauseMenuButton.text = pressKeyStr;
        }
    }
}