using System;

[Serializable]
public class Character
{

        public int characterID;
        public string name;
        public int HP = 100;
        public int MP = 5;
        public int AP = 3;
        public int meleeDamage = 20;
        public int rangeCombatDamage = 15;
        public int rangeCombatReach = 10;

        public Character(int characterID)
        {
            this.characterID = characterID;
        }
    
 }

