
package de.uulm.sopra.team08.config.scenario;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


/**
 * A simple class containing the scenario config
 *
 * @see de.uulm.sopra.team08.config.Config
 */
public class ScenarioConfig {

    public enum Scenario {
        ROCK,
        GRASS,
        PORTAL
    }

    /**
     * The scenario scheme
     * <p>
     * Two dimensional array of fields.
     * (Required)
     */
    @SerializedName("scenario")
    @Expose
    private final List<List<Scenario>> scenario;
    /**
     * The scenario name
     * <p>
     * name of the scenario
     * (Required)
     */
    @SerializedName("name")
    @Expose
    private final String name;
    /**
     * Name of the author
     * <p>
     * Name of the author
     */
    @SerializedName("author")
    @Expose
    private final String author;

    public ScenarioConfig(@NotNull List<List<Scenario>> scenario, @NotNull String name, @NotNull String author) {
        this.scenario = scenario;
        this.name = name;
        this.author = author;
    }

    /**
     * The scenario scheme
     * <p>
     * Two dimensional array of fields.
     * (Required)
     */
    public List<List<Scenario>> getScenario() {
        return scenario;
    }

    /**
     * The scenario name
     * <p>
     * name of the scenario
     * (Required)
     */
    public String getName() {
        return name;
    }

    /**
     * Name of the author
     * <p>
     * Name of the author
     */
    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScenarioConfig that = (ScenarioConfig) o;
        return getScenario().equals(that.getScenario()) && getName().equals(that.getName()) && getAuthor().equals(that.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScenario(), getName(), getAuthor());
    }

}
