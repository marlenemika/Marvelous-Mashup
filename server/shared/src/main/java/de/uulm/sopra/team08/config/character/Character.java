
package de.uulm.sopra.team08.config.character;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Character {

    /**
     * characterID
     * <p>
     * Eine ID, die es erleichtert einem Character eine grafische Darstellung zuzuordnen. Darf nicht doppelt vorkommen.
     * (Required)
     */
    @SerializedName("characterID")
    @Expose
    private int characterID;
    /**
     * charakterName
     * <p>
     * Der Name des Charakters
     * (Required)
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * healthPoints
     * <p>
     * Die Anzahl der Lebenspunkte des Charakters
     * (Required)
     */
    @SerializedName("HP")
    @Expose
    private int hp;
    /**
     * movementPoints
     * <p>
     * Die Anzahl der Bewegungspunkte des Charakters
     * (Required)
     */
    @SerializedName("MP")
    @Expose
    private int mp;
    /**
     * actionPoints
     * <p>
     * Die Anzahl der Aktionspunkte des Charakters
     * (Required)
     */
    @SerializedName("AP")
    @Expose
    private int ap;
    /**
     * meeleDamage
     * <p>
     * Der Nahkampfschaden des Charakters
     * (Required)
     */
    @SerializedName("meleeDamage")
    @Expose
    private int meleeDamage;
    /**
     * rangeCombatDamage
     * <p>
     * Der Fernkampfschaden des Charakters
     * (Required)
     */
    @SerializedName("rangeCombatDamage")
    @Expose
    private int rangeCombatDamage;
    /**
     * rangeCombatReach
     * <p>
     * Die Reichweite des Fernkampfschadens des Charakters
     * (Required)
     */
    @SerializedName("rangeCombatReach")
    @Expose
    private int rangeCombatReach;

    /**
     * characterID
     * <p>
     * Eine ID, die es erleichtert einem Character eine grafische Darstellung zuzuordnen. Darf nicht doppelt vorkommen.
     * (Required)
     */
    public int getCharacterID() {
        return characterID;
    }

    /**
     * charakterName
     * <p>
     * Der Name des Charakters
     * (Required)
     */
    public String getName() {
        return name;
    }

    /**
     * healtPoints
     * <p>
     * Die Anzahl der Lebenspunkte des Charakters
     * (Required)
     */
    public int getHp() {
        return hp;
    }

    /**
     * movementPoints
     * <p>
     * Die Anzahl der Bewegungspunkte des Charakters
     * (Required)
     */
    public int getMp() {
        return mp;
    }

    /**
     * actionPoints
     * <p>
     * Die Anzahl der Aktionspunkte des Charakters
     * (Required)
     */
    public int getAp() {
        return ap;
    }

    /**
     * meeleDamage
     * <p>
     * Der Nahkampfschaden des Charakters
     * (Required)
     */
    public int getMeleeDamage() {
        return meleeDamage;
    }

    /**
     * rangeCombatDamage
     * <p>
     * Der Fernkampfschaden des Charakters
     * (Required)
     */
    public int getRangeCombatDamage() {
        return rangeCombatDamage;
    }

    /**
     * rangeCombatReach
     * <p>
     * Die Reichweite des Fernkampfschadens des Charakters
     * (Required)
     */
    public int getRangeCombatReach() {
        return rangeCombatReach;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return characterID == character.characterID && hp == character.hp && mp == character.mp && ap == character.ap && meleeDamage == character.meleeDamage && rangeCombatDamage == character.rangeCombatDamage && rangeCombatReach == character.rangeCombatReach && Objects.equals(name, character.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, name, hp, mp, ap, meleeDamage, rangeCombatDamage, rangeCombatReach);
    }

    public Character(){

    }

    public Character(de.uulm.sopra.team08.data.entity.Character character){
        this.characterID = character.getId();
        this.ap = character.getCurrentAP();
        this.hp = character.getCurrentHP();
        this.mp = character.getCurrentMP();
        this.name = character.getName();
        this.meleeDamage = character.getMeleeAttack();
        this.rangeCombatDamage = character.getRangeAttack();
        this.rangeCombatReach = character.getAttackRange();
    }
}
