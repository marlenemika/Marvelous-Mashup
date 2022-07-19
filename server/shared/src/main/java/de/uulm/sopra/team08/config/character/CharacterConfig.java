
package de.uulm.sopra.team08.config.character;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


/**
 * A simple class containing the character config
 *
 * @see de.uulm.sopra.team08.config.Config
 */
public class CharacterConfig {


    /**
     * characterArray
     * <p>
     * Das Array an Character Descriptions
     * (Required)
     */
    @SerializedName("characters")
    @Expose
    private final Set<Character> characters;

    public CharacterConfig(@NotNull HashSet<Character> characters) {
        this.characters = characters;
    }

    /**
     * characterArray
     * <p>
     * Das Array an Character Descriptions
     * (Required)
     */
    public Set<Character> getCharacters() {
        return new HashSet<>(characters);
    }

}
