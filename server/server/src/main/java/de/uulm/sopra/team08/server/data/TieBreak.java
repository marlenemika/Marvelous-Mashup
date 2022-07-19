package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.entity.Character;

import java.util.List;
import java.util.Random;

public class TieBreak {

    private int maxInfStoneP1;
    private int maxInfStoneP2;

    private int knockOutP1;
    private int knockOutP2;

    private int damageDealtP1;
    private int damageDealtP2;

    public TieBreak() {
        reset();
    }

    public void updateInfinityStone(EntityID entityID, int value) {
        if (entityID.equals(EntityID.P1))
            maxInfStoneP1 = Math.max(maxInfStoneP1, value);
        else if (entityID.equals(EntityID.P2))
            maxInfStoneP2 = Math.max(maxInfStoneP2, value);
    }

    public void updateInfinityStone(List<Character> characters) {
        int countInfP1 = 0;
        int countInfP2 = 0;
        for (Character character : characters) {
            if (character.getEID().equals(EntityID.P1)) countInfP1 += character.getInventoryList().size();
            else if (character.getEID().equals(EntityID.P2)) countInfP2 += character.getInventoryList().size();
        }
        updateInfinityStone(EntityID.P1, countInfP1);
        updateInfinityStone(EntityID.P2, countInfP2);
    }

    public void updateKnockOut(EntityID entityID) {
        if (entityID.equals(EntityID.P1))
            knockOutP1++;
        else if (entityID.equals(EntityID.P2))
            knockOutP2++;
    }

    public void updateDamageDealtToEnemies(EntityID entityID, int value) {
        if (entityID.equals(EntityID.P1))
            damageDealtP1 += value;
        else if (entityID.equals(EntityID.P2))
            damageDealtP2 += value;
    }

    public EntityID useTieBreak() {
        Random random = new Random();
        if (maxInfStoneP1 != maxInfStoneP2)
            return maxInfStoneP1 > maxInfStoneP2 ? EntityID.P1 : EntityID.P2;
        else if (knockOutP1 != knockOutP2)
            return knockOutP1 > knockOutP2 ? EntityID.P1 : EntityID.P2;
        else if (damageDealtP1 != damageDealtP2)
            return damageDealtP1 > damageDealtP2 ? EntityID.P1 : EntityID.P2;
        else return random.nextBoolean() ? EntityID.P1 : EntityID.P2;
    }

    public void reset() {
        maxInfStoneP1 = 0;
        maxInfStoneP2 = 0;
        knockOutP1 = 0;
        knockOutP2 = 0;
        damageDealtP1 = 0;
        damageDealtP2 = 0;
    }

}
