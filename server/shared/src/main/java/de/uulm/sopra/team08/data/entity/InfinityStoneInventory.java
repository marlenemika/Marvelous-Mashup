package de.uulm.sopra.team08.data.entity;

import de.uulm.sopra.team08.data.item.InfinityStone;

import java.util.List;

/**
 * This interface offers functionality of an inventory used in
 * Marvelous-Mashup. This inventory can hold up to 6 unique
 * InfinityStones, add and remove an infinity stone.
 *
 * @author Jan-Philipp
 */
public interface InfinityStoneInventory {

    /**
     * Returns and removes the given infinity stone at the given index.
     *
     * @param index the index of the infinity stone that should be removed
     * @return the infinity stone at the given index
     */
    InfinityStone removeFromInventory(int index);

    /**
     * Removes the given infinity stone from the inventory.
     *
     * @param infinityStone that should be removed
     */
    void removeFromInventory(InfinityStone infinityStone);

    /**
     * Adds the given infinity stone to a free space in the inventory.
     *
     * @param infinityStone that should be added.
     */
    void addToInventory(InfinityStone infinityStone);

    /**
     * Returns true if the given infinity stone is already in this inventory.
     *
     * @param infinityStone the infinity stone that should be looked for
     * @return true if the infinity stone is in this inventory
     */
    boolean contains(InfinityStone infinityStone);

    /**
     * Returns true if inventory only contains non null infinity stones.
     *
     * @return true if inventory only contains non null infinity stones
     */
    boolean isFull();

    InfinityStone[] getInventory();

    List<InfinityStone> getInventoryList();

}
