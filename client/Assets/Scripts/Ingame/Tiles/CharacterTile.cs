using Network;
using Network.Objects;
using Network.Requests;
using Objects;
using UnityEngine;

namespace Ingame.Tiles
{
    public class CharacterTile : GridObject
    {
        public GameState gameState;
        [SerializeField] private GameObject _highlight;
        [SerializeField] public GameObject active;
        [SerializeField] private GameObject _knockout;
        [SerializeField] private GameObject _healthBarGreen;
        [SerializeField] private GameObject _healthBarRed;
        private Vector3 localScale;
        private Vector2 _positionInGrid = new Vector2(0, 0);
        private EventEntity character;
        //private EventEntity activeCharacter;
        private bool drag;

        public void Init(int x, int y, EventEntity character, EventEntity activeCharacter)
        {
            _positionInGrid = new Vector2(x, y);
            this.character = character;

            if (character.ID == activeCharacter.ID)
            {
                active.SetActive(true);
            }
            else
            {
                active.SetActive(false);
            }

            GetComponent<SpriteRenderer>().sprite = CharacterSelectionController.GetCharacterImage(character.ID);

            if (character.HP <= 0)
            {
                _knockout.SetActive(true);
                _healthBarGreen.SetActive(false);
                _healthBarRed.SetActive(false);
            }
            else
            {
                _knockout.SetActive(false);

                // Show the green or red health bar
                // green if character is in own team, red if not
                // set the health of team 1 to green and team 2 to red if user is spectator
                if (character.PID == 1 && gameState.assignment.Equals("PlayerOne")
                    || character.PID == 2 && gameState.assignment.Equals("PlayerTwo")
                    || character.PID == 1 && NetworkAdapter.Instance.Role.Equals("SPECTATOR"))
                {
                    _healthBarGreen.SetActive(true);
                    _healthBarRed.SetActive(false);
                    localScale = _healthBarGreen.transform.localScale;
                    localScale.x = 0.3f / (100f / character.HP);
                    _healthBarGreen.transform.localScale = localScale;
                }
                else
                {
                    _healthBarRed.SetActive(true);
                    _healthBarGreen.SetActive(false);
                    localScale = _healthBarRed.transform.localScale;
                    localScale.x = 0.3f / (100f / character.HP);
                    _healthBarRed.transform.localScale = localScale;
                }
            }
        }

        private void OnMouseEnter()
        {
            _highlight.SetActive(true);
        }

        private void OnMouseExit()
        {
            _highlight.SetActive(false);
        }
    }
}
