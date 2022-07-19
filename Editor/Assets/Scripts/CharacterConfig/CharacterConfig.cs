using System;
using UnityEngine;

[Serializable]
[CreateAssetMenu(fileName = "CharacterConfigObject", menuName = "ConfigFiles/CharacterConfigObject", order = 1)]
public class CharacterConfig : ScriptableObject, IConfigFile
{
	
	public Character[] characters = new Character[24];
	
	private void setNames ()
	{
		foreach (var character in characters)
		{
			switch(character.characterID)
			{
				case 1:
					characters[0].name = "Rocket Raccoon";
					break;
				case 2:
					characters[1].name = "Quicksilver";
					break;
				case 3: 
					characters[2].name = "Hulk";
					break;
				case 4:
					characters[3].name = "Black Widow";
					break;
				case 5:
					characters[4].name = "Hawkeye";
					break;
				case 6:
					characters[5].name = "Captain America";
					break;
				case 7:
					characters[6].name = "Spiderman";
					break;
				case 8:
					characters[7].name = "Dr. Strange";
					break;
				case 9:
					characters[8].name = "Iron Man";
					break;
				case 10:
					characters[9].name = "Black Panther";
					break;
				case 11:
					characters[10].name = "Thor";
					break;
				case 12:
					characters[11].name = "Captain Marvel";
					break;
				case 13:
					characters[12].name = "Groot";
					break;
				case 14:
					characters[13].name = "Starlord";
					break;
				case 15:
					characters[14].name = "Gamora";
					break;
				case 16:
					characters[15].name = "Ant Man";
					break;
				case 17:
					characters[16].name = "Vision";
					break;
				case 18:
					characters[17].name = "Deadpool";
					break;
				case 19:
					characters[18].name = "Loki";
					break;
				case 20:
					characters[19].name = "Silver Surfer";
					break;
				case 21:
					characters[20].name = "Mantis";
					break;
				case 22:
					characters[21].name = "Ghost Rider";
					break;
				case 23:
					characters[22].name = "Jessica Jones";
					break;
				case 24:
					characters[23].name = "Scarlet Witch";
					break;
			}
		}
	}
	
	private void iterateCharacterID()  
	{
		for (int i=0; i<24; i++)
		{
			characters[i] = new Character(i+1);
		}
		
		setNames();
	}

	public CharacterConfig()
	{
		iterateCharacterID();
	}
} 