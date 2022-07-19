package de.uulm.sopra.team08.data.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.partie.PartieConfig;
import de.uulm.sopra.team08.data.item.*;
import de.uulm.sopra.team08.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class represents a character and helps implementing their
 * functionality by providing methods to manage character stats.
 * <p>
 * This class does not check if an action can be done. The user should
 * check if the character is able to move or do certain actions.
 * <p>
 * All stats will be in between 0 and the passed in maximum.
 *
 * @author Jan-Philipp
 */
public class Character extends Entity implements InfinityStoneInventory {

    private final int maxMP;
    private final int maxAP;
    private final int maxHP;
    private final String name;
    private final int meleeAttack;
    private final int rangeAttack;
    private final int attackRange;
    private final InfinityStone[] inventory;
    private final List<Integer> stones;
    protected int currentMP;
    protected int currentAP;
    private int currentHP;


    /**
     * Parameterized constructor.
     *
     * @param name        the name of the character
     * @param mp          amount of movement points
     * @param ap          amount of action points
     * @param hp          amount of health
     * @param meleeAttack amount of melee attack damage
     * @param rangeAttack amount of ranged attack damage
     * @param attackRange amount of ranged attack range
     */
    public Character(String name, int mp, int ap, int hp, int meleeAttack, int rangeAttack, int attackRange, EntityID eID, int id) {
        super(eID, id);
        this.name = name;
        this.maxMP = mp;
        this.currentMP = mp;
        this.maxAP = ap;
        this.currentAP = ap;
        this.maxHP = hp;
        this.currentHP = hp;
        this.meleeAttack = meleeAttack;
        this.rangeAttack = rangeAttack;
        this.attackRange = attackRange;
        this.inventory = new InfinityStone[6];
        stones = new ArrayList<>();
    }

    /**
     * Creates a Character using a JSON Object. {@link Config} is needed for not submitted data.
     *
     * @param entity the JSON Object that will be parsed
     * @param config the current games config
     * @return parsed Character
     */
    public static Character fromJson(JsonObject entity, Config config) {
        String name = entity.get("name").getAsString();
        int id = entity.get("ID").getAsInt();
        // what stones does the entity hold?
        JsonArray stones = entity.getAsJsonArray("stones");
        List<InfinityStone> stoneList = new ArrayList<>(stones.size());
        if (stones.size() > 0) {
            PartieConfig pC = config.getPartieConfig();
            // add every stone to inventory
            for (JsonElement j : stones) {
                switch (j.getAsInt()) {
                    case 0:
                        stoneList.add(new SpaceStone(pC.getSpaceStoneCD()));
                        break;
                    case 1:
                        stoneList.add(new MindStone(pC.getMindStoneCD()));
                        break;
                    case 2:
                        stoneList.add(new RealityStone(pC.getRealityStoneCD()));
                        break;
                    case 3:
                        stoneList.add(new PowerStone(pC.getPowerStoneCD()));
                        break;
                    case 4:
                        stoneList.add(new TimeStone(pC.getTimeStoneCD()));
                        break;
                    case 5:
                        stoneList.add(new SoulStone(pC.getSoulStoneCD()));
                        break;
                }
            }
        }

        // get position
        JsonArray position = entity.getAsJsonArray("position");

        Tuple<Integer, Integer> positionTuple = new Tuple<>(position.get(0).getAsInt(), position.get(1).getAsInt());

        // If NPCs are given with EntityType Character.
        if (name.equals("Goose") && id == 0) {
            Goose goose = new Goose();
            stoneList.forEach(goose::addToInventory);
            goose.setCoordinates(positionTuple);
            return goose;
        } else if (name.equals("Stan Lee") && id == 1) {
            StanLee stanLee = new StanLee();
            stoneList.forEach(stanLee::addToInventory);
            stanLee.setCoordinates(positionTuple);
            return stanLee;
        } else if (name.equals("Thanos") && id == 2) {
            int mp = entity.get("MP").getAsInt();
            Thanos thanos = new Thanos(mp);
            stoneList.forEach(thanos::addToInventory);
            thanos.setCoordinates(positionTuple);
            return thanos;
        }
        int meleeAttack = 0;
        int rangeAttack = 0;
        int attackRange = 0;
        // get damage (not in message body)
        List<de.uulm.sopra.team08.config.character.Character> a = new ArrayList<>(config.getCharacterConfig().getCharacters());
        for (de.uulm.sopra.team08.config.character.Character character : a) {
            if (character.getCharacterID() == id) {
                meleeAttack = character.getMeleeDamage();
                rangeAttack = character.getRangeCombatDamage();
                attackRange = character.getRangeCombatReach();
                break;
            }
        }

        int hp = entity.get("HP").getAsInt();
        int mp = entity.get("MP").getAsInt();
        int ap = entity.get("AP").getAsInt();
        int pid = entity.get("PID").getAsInt();
        EntityID eID;
        if (pid == 1) {
            eID = EntityID.P1;
        } else if (pid == 2) {
            eID = EntityID.P2;
        } else {
            throw new IllegalArgumentException("PID must be 1 or 2!");
        }
        // create Character
        Character character = new Character(name, mp, ap, hp, meleeAttack, rangeAttack, attackRange, eID, id);
        character.setCoordinates(positionTuple);
        stoneList.forEach(character::addToInventory);


        return character;
    }

    /**
     * Heals the character by the given amount.
     *
     * @param amount the amount of healing
     */
    public void healCharacter(int amount) {
        if (amount > 0) this.currentHP = Math.min(this.maxHP, this.currentHP + amount);
    }

    /**
     * Heals the character to max hp
     */
    public void healMax() {
        this.currentHP = this.maxHP;
    }

    /**
     * Deals damage to the Character by the given amount.
     *
     * @param amount the amount of damage
     * @return the amount of damage done
     */
    public int damageCharacter(int amount) {
        if (!isVulnerable()) return 0;
        int oldHP = this.currentHP;
        if (amount > 0) this.currentHP = Math.max(0, this.currentHP - amount);
        return oldHP - this.currentHP;
    }

    /**
     * Reduces mp by the given amount.
     *
     * @param usedMP the amount of mp used
     */
    public void updateUsedMP(int usedMP) {
        this.currentMP = Math.max(0, this.currentMP - usedMP);
    }

    /**
     * Reduces ap by the given amount.
     *
     * @param usedAP the amount of ap used
     */
    public void updateUsedAP(int usedAP) {
        this.currentAP = Math.max(0, this.currentAP - usedAP);
    }

    /**
     * Refills MP and AP of this character to its maximum.
     */
    public void refillStats() {
        this.currentMP = this.maxMP;
        this.currentAP = this.maxAP;
    }

    public boolean isTurnFinished() {
        return currentAP <= 0 && currentMP <= 0 || isKnockedOut();
    }

    /**
     * A character is knocked out if his hp are 0 or lower.
     *
     * @return true if hp is 0 or below.
     */
    public boolean isKnockedOut() {
        return this.currentHP <= 0;
    }

    @Override
    public InfinityStone removeFromInventory(int index) {
        final InfinityStone ret = this.inventory[index];
        this.inventory[index] = null;

        stones.remove((Integer) ret.getId());

        // remove potential gap
        for (int i = index; i < this.inventory.length - 1; i++) {
            if (this.inventory[i] == null) {
                this.inventory[i] = this.inventory[i + 1];
                this.inventory[i + 1] = null;
            }
        }
        return ret;
    }

    @Override
    public void removeFromInventory(InfinityStone infinityStone) {
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null && this.inventory[i].equals(infinityStone)) {
                stones.remove((Integer) infinityStone.getId());
                removeFromInventory(i);
                return;
            }
        }
    }

    @Override
    public void addToInventory(InfinityStone infinityStone) {
        if (this.contains(infinityStone)) return;
        stones.add(infinityStone.getId());
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] == null) {
                this.inventory[i] = infinityStone;
                return;
            }
        }
    }

    @Override
    public boolean contains(InfinityStone infinityStone) {
        for (InfinityStone stone : this.inventory)
            if (infinityStone.equals(stone)) return true;
        return false;
    }

    @Override
    public boolean isFull() {
        int stones = 0;
        for (InfinityStone infinityStone : this.inventory) {
            if (infinityStone != null) stones++;
        }
        return stones == this.inventory.length;
    }

    @Override
    public InfinityStone[] getInventory() {
        return this.inventory;
    }

    /**
     * Returns a list of all infinity stones in inventory.
     * List indices don't match with the array indices.
     *
     * @return list
     */
    @Override
    public List<InfinityStone> getInventoryList() {
        return Arrays.stream(this.inventory).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public int getMaxMP() {
        return this.maxMP;
    }

    public int getMaxAP() {
        return this.maxAP;
    }

    public int getMaxHP() {
        return this.maxHP;
    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public int getCurrentMP() {
        return this.currentMP;
    }

    public int getCurrentAP() {
        return this.currentAP;
    }

    public int getMeleeAttack() {
        return this.meleeAttack;
    }

    public int getRangeAttack() {
        return this.rangeAttack;
    }

    public int getAttackRange() {
        return this.attackRange;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean isVulnerable() {
        return true;
    }

    @Override
    public boolean blocksSight() {
        return true;
    }

    /**
     * Convenience method for getting a Character as JsonElement
     *
     * @return Characters JsonElement representation
     */
    @Override
    public JsonElement toJsonElement() {
        if (this instanceof Goose) {
            return ((Goose) this).toNPCJsonElement();
        } else if (this instanceof StanLee) {
            return ((StanLee) this).toNPCJsonElement();
        } else if (this instanceof Thanos) {
            return ((Thanos) this).toNPCJsonElement();
        }
        // standard specific JSON representation
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "Character");
        jsonObject.addProperty("name", this.name);
        if (this.eID == EntityID.P1) {
            jsonObject.addProperty("PID", 1);
        } else if (this.eID == EntityID.P2) {
            jsonObject.addProperty("PID", 2);
        }
        jsonObject.addProperty("ID", this.id);
        jsonObject.addProperty("HP", currentHP);
        jsonObject.addProperty("MP", currentMP);
        jsonObject.addProperty("AP", currentAP);
        jsonObject.add("stones", gson.toJsonTree(stones));
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }

    @Override
    public int hashCode() {
        int result = maxMP;
        result = 31 * result + maxAP;
        result = 31 * result + maxHP;
        result = 31 * result + name.hashCode();
        result = 31 * result + meleeAttack;
        result = 31 * result + rangeAttack;
        result = 31 * result + attackRange;
        result = 31 * result + currentMP;
        result = 31 * result + currentAP;
        result = 31 * result + currentHP;
        result = 31 * result + Arrays.hashCode(inventory);
        result = 31 * result + stones.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Character character = (Character) o;

        if (currentMP != character.currentMP) return false;
        if (currentAP != character.currentAP) return false;
        if (currentHP != character.currentHP) return false;
        if (!name.equals(character.name)) return false;
        return stones.equals(character.stones);
    }

}
