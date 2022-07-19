
package de.uulm.sopra.team08.config.partie;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;


/**
 * A simple class containing the partie/match config
 * Naming is as specified in the standard
 *
 * @see de.uulm.sopra.team08.config.Config
 */

public class PartieConfig {

    /**
     * Maximum amount of rounds played until Thanos appears. Using 0 will result in Thanos never appearing. Example: maxRounds=5 -> Thanos will appear
     * at the start of round 5.
     * (Required)
     */
    @SerializedName("maxRounds")
    @Expose
    private int maxRounds;
    /**
     * Time window in which the player can use/move one of their Heros. Time is measured in seconds, time will not continue during a pause.
     * (Required)
     */
    @SerializedName("maxRoundTime")
    @Expose
    private int maxRoundTime;
    /**
     * Optional: Maximum time to play the game, once the time runs out Thanos will spawn in the following round. Using 0 will result in no Time limit.
     * Time is measured in seconds.
     */
    @SerializedName("maxGameTime")
    @Expose
    private int maxGameTime;
    /**
     * Maximum time until the animation is done. Using 0 will result in no time limit. Time is measured in seconds.
     * (Required)
     */
    @SerializedName("maxAnimationTime")
    @Expose
    private int maxAnimationTime;
    /**
     * Cooldown for Space Stone. Cooldown measured in rounds.
     * (Required)
     */
    @SerializedName("spaceStoneCD")
    @Expose
    private int spaceStoneCD;
    /**
     * Cooldown for Mind Stone. Cooldown measured in rounds.
     * (Required)
     */
    @SerializedName("mindStoneCD")
    @Expose
    private int mindStoneCD;
    /**
     * Cooldown for Reality Stone. Cooldown measured in rounds.
     * (Required)
     */
    @SerializedName("realityStoneCD")
    @Expose
    private int realityStoneCD;
    /**
     * Cooldown for Power Stone. Cooldown measured in rounds.
     * (Required)
     */
    @SerializedName("powerStoneCD")
    @Expose
    private int powerStoneCD;
    /**
     * Cooldown for Time Stone. Cooldown measured in rounds.
     * (Required)
     */
    @SerializedName("timeStoneCD")
    @Expose
    private int timeStoneCD;
    /**
     * Cooldown for Soul Stone. Cooldown measured in rounds.
     * (Required)
     */
    @SerializedName("soulStoneCD")
    @Expose
    private int soulStoneCD;
    /**
     * Damage Value of Mind Stone.
     * (Required)
     */
    @SerializedName("mindStoneDMG")
    @Expose
    private int mindStoneDMG;
    /**
     * Timelimit for a pause. A disconnect from the game will result in a pause. Using 0 means no pauses are allowed. Time is measured in seconds.
     * (Required)
     */
    @SerializedName("maxPauseTime")
    @Expose
    private int maxPauseTime;
    /**
     * Duration the Server waits for a Response of a Client before a Timeout. Based on Usage of keep-alives.
     * (Required)
     */
    @SerializedName("maxResponseTime")
    @Expose
    private int maxResponseTime;

    /**
     * Maximum amount of rounds played until Thanos appears. Using 0 will result in Thanos never appearing. Example: maxRounds=5 -> Thanos will appear
     * at the start of round 5.
     * (Required)
     */
    public int getMaxRounds() {
        return maxRounds;
    }

    /**
     * Time window in which the player can use/move one of their Heros. Time is measured in seconds, time will not continue during a pause.
     * (Required)
     */
    public int getMaxRoundTime() {
        return maxRoundTime;
    }

    /**
     * Optional: Maximum time to play the game, once the time runs out Thanos will spawn in the following round. Using 0 will result in no Time limit.
     * Time is measured in seconds.
     */
    public int getMaxGameTime() {
        return maxGameTime;
    }

    /**
     * Maximum time until the animation is done. Using 0 will result in no time limit. Time is measured in seconds.
     * (Required)
     */
    public int getMaxAnimationTime() {
        return maxAnimationTime;
    }

    /**
     * Cooldown for Space Stone. Cooldown measured in rounds.
     * (Required)
     */
    public int getSpaceStoneCD() {
        return spaceStoneCD;
    }

    /**
     * Cooldown for Mind Stone. Cooldown measured in rounds.
     * (Required)
     */
    public int getMindStoneCD() {
        return mindStoneCD;
    }

    /**
     * Cooldown for Reality Stone. Cooldown measured in rounds.
     * (Required)
     */
    public int getRealityStoneCD() {
        return realityStoneCD;
    }

    /**
     * Cooldown for Power Stone. Cooldown measured in rounds.
     * (Required)
     */
    public int getPowerStoneCD() {
        return powerStoneCD;
    }

    /**
     * Cooldown for Time Stone. Cooldown measured in rounds.
     * (Required)
     */
    public int getTimeStoneCD() {
        return timeStoneCD;
    }

    /**
     * Cooldown for Soul Stone. Cooldown measured in rounds.
     * (Required)
     */
    public int getSoulStoneCD() {
        return soulStoneCD;
    }

    /**
     * Damage Value of Mind Stone.
     * (Required)
     */
    public int getMindStoneDMG() {
        return mindStoneDMG;
    }

    /**
     * Timelimit for a pause. A disconnect from the game will result in a pause. Using 0 means no pauses are allowed. Time is measured in seconds.
     * (Required)
     */
    public int getMaxPauseTime() {
        return maxPauseTime;
    }

    /**
     * Duration the Server waits for a Response of a Client before a Timeout. Based on Usage of keep-alives.
     * (Required)
     */
    public int getMaxResponseTime() {
        return maxResponseTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartieConfig that = (PartieConfig) o;
        return getMaxRounds() == that.getMaxRounds() && getMaxRoundTime() == that.getMaxRoundTime() && getMaxGameTime() == that.getMaxGameTime() && getMaxAnimationTime() == that.getMaxAnimationTime() && getSpaceStoneCD() == that.getSpaceStoneCD() && getMindStoneCD() == that.getMindStoneCD() && getRealityStoneCD() == that.getRealityStoneCD() && getPowerStoneCD() == that.getPowerStoneCD() && getTimeStoneCD() == that.getTimeStoneCD() && getSoulStoneCD() == that.getSoulStoneCD() && getMindStoneDMG() == that.getMindStoneDMG() && getMaxPauseTime() == that.getMaxPauseTime() && getMaxResponseTime() == that.getMaxResponseTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaxRounds(), getMaxRoundTime(), getMaxGameTime(), getMaxAnimationTime(), getSpaceStoneCD(), getMindStoneCD(), getRealityStoneCD(), getPowerStoneCD(), getTimeStoneCD(), getSoulStoneCD(), getMindStoneDMG(), getMaxPauseTime(), getMaxResponseTime());
    }

}
