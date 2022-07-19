namespace Network.Objects
{
    public class Character
    {
        public int characterID;
        public string name;
        public int HP;
        public int MP;
        public int AP;
        public int meleeDamage;
        public int rangeCombatDamage;
        public int rangeCombatReach;

        public Character(
            int characterID,
            string name,
            int HP,
            int MP,
            int AP,
            int meleeDamage,
            int rangeCombatDamage,
            int rangeCombatReach
        )
        {
            this.characterID = characterID;
            this.name = name;
            this.HP = HP;
            this.MP = MP;
            this.AP = AP;
            this.meleeDamage = meleeDamage;
            this.rangeCombatDamage = rangeCombatDamage;
            this.rangeCombatReach = rangeCombatReach;
        }
    }
}
