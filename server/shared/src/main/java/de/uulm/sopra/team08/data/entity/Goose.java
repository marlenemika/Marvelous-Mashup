package de.uulm.sopra.team08.data.entity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.data.item.InfinityStone;
import de.uulm.sopra.team08.util.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class helps implementing Goose's functionality by offering an inventory
 * to store and remove the swallowed InfinityStones and the ability to change the
 * x and y coordinates to teleport.
 *
 * @author Jan-Philipp
 */
public class Goose extends Character implements InfinityStoneInventory {

    public final static int ID = 0;
    private final InfinityStone[] inventory;
    private final List<Integer> stones;

    public Goose() {
        super("Goose", 1, 1, 0, 0, 0, 0, EntityID.NPC, ID);
        this.inventory = new InfinityStone[6];
        stones = new ArrayList<>();
    }

    @Override
    public InfinityStone removeFromInventory(int index) {
        final InfinityStone ret = this.inventory[index];

        stones.remove((Integer) ret.getId());

        this.inventory[index] = null;


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
     * Convenience method for getting Goose as JsonElement
     *
     * @return Goose JsonElement representation
     */
    public JsonElement toNPCJsonElement() {
        // standard specific JSON representation
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entityType", "NPC");
        jsonObject.addProperty("ID", 0);
        jsonObject.addProperty("MP", 0);
        jsonObject.add("stones", gson.toJsonTree(stones));
        jsonObject.add("position", Tuple.toJsonArray(this.getCoordinates()));
        return jsonObject;
    }

    public InfinityStone spitOut() {
        final List<InfinityStone> list = Arrays.stream(inventory).filter(Objects::nonNull).collect(Collectors.toList());
        if (list.size() == 0) throw new IllegalStateException("inventory spit empty");
        Collections.shuffle(list);
        removeFromInventory(list.get(0));
        return list.get(0);
    }

}
