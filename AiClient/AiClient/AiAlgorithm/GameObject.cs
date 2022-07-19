using System;
using System.Collections.Generic;
using System.Linq;
using AiClient.Network.Objects;

namespace AiClient
{
    public abstract class GameObject
    {
        public string entityType;
        public int ID;
        public Position position;
        public abstract GameObject DeapCopy();
    }
    public class SuperheroObject : GameObject
    {
        public int actionPoints;
        public int attackRange;
        public int damageClose;
        public int damageRange;
        public int healthPoints;
        public List<int> inventory;
        public bool isAlive;
        public int movementPoints;

        public string name;
        public int PID;

        public SuperheroObject(string name, bool isAlive, int healthPoints, int movementPoints, int actionPoints,
            int damageClose, int damageRange, int attackRange, int PID, int ID, Position position)
        {
            this.name = name;
            this.isAlive = isAlive;
            this.healthPoints = healthPoints;
            this.movementPoints = movementPoints;
            this.actionPoints = actionPoints;
            this.damageClose = damageClose;
            this.damageRange = damageRange;
            this.attackRange = attackRange;
            this.PID = PID;
            this.ID = ID;
            this.entityType = "Character";
            inventory = new List<int>();
            this.position = position;
        }

        public SuperheroObject(string name, bool isAlive, int healthPoints, int movementPoints, int actionPoints,
            int damageClose, int damageRange, int attackRange, int PID, int ID, Position position, List<int> inventory)
        {
            this.name = name;
            this.isAlive = isAlive;
            this.healthPoints = healthPoints;
            this.movementPoints = movementPoints;
            this.actionPoints = actionPoints;
            this.damageClose = damageClose;
            this.damageRange = damageRange;
            this.attackRange = attackRange;
            this.PID = PID;
            this.ID = ID;
            this.entityType = "Character";
            this.inventory = inventory;
            this.position = position;
        }
        public SuperheroObject(EventEntity entity, List<Character> characters)
        {
            this.entityType = "Character";
            this.name = entity.name;
            this.isAlive = entity.HP > 0;
            this.healthPoints = entity.HP;
            this.movementPoints = entity.MP;
            this.actionPoints = entity.AP;
            this.damageClose = characters.Find(x => x.characterID == entity.ID).meleeDamage;
            this.damageRange = characters.Find(x => x.characterID == entity.ID).rangeCombatDamage;
            this.attackRange = characters.Find(x => x.characterID == entity.ID).rangeCombatReach;
            

            this.PID = entity.PID;
            this.ID = entity.ID;
            inventory = entity.stones.ToList();
            this.position = new Position(entity.position);
        }

        public bool Equals(GameObject y)
        {
            //MyLogger.GetInstance().Debug("Entered Superhero.Equals");
            if (ReferenceEquals(this, y)) return true;
            if (ReferenceEquals(this, null)) return false;
            if (ReferenceEquals(y, null)) return false;
            if (GetType() != y.GetType()) return false;
            var yCast = (SuperheroObject) y;
            /*if (healthPoints != yCast.healthPoints && name.Equals(yCast.name)) 
                MyLogger.GetInstance().Debug("different Healthpoints" + name + "," + yCast.name);*/
            return name == yCast.name && isAlive == yCast.isAlive && healthPoints == yCast.healthPoints &&
                   movementPoints == yCast.movementPoints && actionPoints == yCast.actionPoints &&
                   damageClose == yCast.damageClose && damageRange == yCast.damageRange &&
                   attackRange == yCast.attackRange;
        }

        public override GameObject DeapCopy()
        {
            var inventoryCopy = new List<int>();
            foreach (var stone in inventory)
            {
                inventoryCopy.Add(stone);
            }
            //MyLogger.GetInstance().Debug("Superhero DeapCopy Entered");
            return new SuperheroObject((string) name.Clone(), isAlive, healthPoints, movementPoints, actionPoints,
                damageClose,
                damageRange, attackRange, PID, ID, position.DeapCopy(),inventoryCopy);
        }
    }


    public class InfinityStoneObject : GameObject
    {
        public int cooldown;
        public int cooldownLeft;
        public bool isUsable;

        public InfinityStoneObject(bool isUsable, int cooldown, int cooldownLeft,
            Position position, int ID)
        {
            this.entityType = "InfinityStone";
            this.isUsable = isUsable;
            this.cooldown = cooldown;
            this.cooldownLeft = cooldownLeft;
            this.position = position;
            this.ID = ID;
        }

        public InfinityStoneObject(EventEntity entity, int[] stoneCooldowns)
        {
            this.entityType = "InfinityStone";
            this.isUsable = stoneCooldowns[entity.ID]==0;
            this.cooldownLeft = stoneCooldowns[entity.ID];
            this.position = new Position(entity.position[0], entity.position[1]);
            this.ID = entity.ID;
        }

        public override GameObject DeapCopy()
        {
            //MyLogger.GetInstance().Debug("InfinityStone DeapCopy Entered");
            //this is problematic because its not the same instance as the superhero in entity list
            return new InfinityStoneObject(isUsable, cooldown, cooldownLeft,
                position.DeapCopy(), ID);
        }
    }

    public class RockObject : GameObject
    {
        public int healthPoints;

        public RockObject(int healthPoints, Position position, int ID)
        {
            this.entityType = "Rock";
            this.position = position;
            this.healthPoints = healthPoints;
            this.ID = ID;
        }

        public RockObject(EventEntity entity)
        {
            this.entityType = "Rock";
            this.position = new Position(entity.position[0], entity.position[1]);
            this.healthPoints = entity.HP;
            this.ID = entity.ID;
        }

        public override GameObject DeapCopy()
        {
            //MyLogger.GetInstance().Debug("Rock DeapCopy Entered");
            return new RockObject(healthPoints, position.DeapCopy(), ID);
        }
    }

    public class Portal : GameObject
    {
        public Portal(int ID, Position position)
        {
            this.entityType = "Portal";
            this.ID = ID;
            this.position = position;
        }

        public Portal(EventEntity entity)
        {
            this.entityType = "Portal";
            this.ID = entity.ID;
            this.position = new Position(entity.position[0], entity.position[1]);
        }

        public override GameObject DeapCopy()
        {
            //MyLogger.GetInstance().Debug("Portal DeapCopy Entered");
            return new Portal(ID, position.DeapCopy());
        }
    }
    public class NPC : GameObject
    {
        public int MP;
        public List<int> stones;
        
        public NPC(int ID, int MP, List<int> stones, Position position)
        {
            this.entityType = "NPC";
            this.ID = ID;
            this.MP = MP;
            this.stones = stones;
            this.position = position;
        }

        public NPC(EventEntity entity)
        {
            this.entityType = "NPC";
            this.ID = entity.ID;
            this.MP = entity.MP;
            this.stones = entity.stones.ToList();
            this.position = new Position(entity.position[0], entity.position[1]);
        }

        public override GameObject DeapCopy()
        {
            //MyLogger.GetInstance().Debug("NPC");
            var stonesCopy = new List<int>();
            foreach (var i in stones)
            {
                stonesCopy.Add(i);
            }
            return new NPC(ID, MP,stonesCopy,position.DeapCopy());
        }
    }
}