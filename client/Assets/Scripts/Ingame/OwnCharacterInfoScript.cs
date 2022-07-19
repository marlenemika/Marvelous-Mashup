using System.Collections;
using System.Collections.Generic;
using Network.Objects;
using UnityEngine;
using UnityEngine.UI;

public class OwnCharacterInfoScript : MonoBehaviour
{
    public Image imgSkin;
    public Text lblName, txtHP;
    public Slider healthBar;
    public Image mindStone, powerStone, realityStone, soulStone, spaceStone, timeStone;

    public void Init(EventEntity eventEntity)
    {
        imgSkin.sprite = CharacterSelectionController.GetCharacterImage(eventEntity.ID);
        lblName.text = eventEntity.name;
        txtHP.text = eventEntity.HP.ToString();
        healthBar.value = ToPercent(eventEntity.HP);
        foreach (var stone in eventEntity.stones)
        {
            switch (stone)
            {
                case 0:
                    spaceStone.gameObject.SetActive(true);
                    break;
                case 1:
                    mindStone.gameObject.SetActive(true);
                    break;
                case 2:
                    realityStone.gameObject.SetActive(true);
                    break;
                case 3:
                    powerStone.gameObject.SetActive(true);
                    break;
                case 4:
                    timeStone.gameObject.SetActive(true);
                    break;
                case 5:
                    soulStone.gameObject.SetActive(true);
                    break;
            }
        }
    }

    private float ToPercent(int originHP)
    {
        return originHP / 100f;
    }
}
