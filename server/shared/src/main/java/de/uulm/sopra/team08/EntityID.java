package de.uulm.sopra.team08;

import com.google.gson.annotations.SerializedName;

/**
 * An enum representing the different types of entities
 */
public enum EntityID {

    NPC,
    P1,
    P2,
    @SerializedName("Rocks")
    ROCKS,
    @SerializedName("Portals")
    PORTALS,
    @SerializedName("InfinityStones")
    INFINITYSTONES
}
