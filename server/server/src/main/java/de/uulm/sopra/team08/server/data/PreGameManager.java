package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.character.Character;
import de.uulm.sopra.team08.config.character.CharacterConfig;
import de.uulm.sopra.team08.event.GameAssignment;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class PreGameManager {

    private final ArrayList<Character> p1Assigned;
    private final ArrayList<de.uulm.sopra.team08.data.entity.Character> p1Picked;

    private final ArrayList<Character> p2Assigned;
    private final ArrayList<de.uulm.sopra.team08.data.entity.Character> p2Picked;

    private final Player player1;
    private final Player player2;

    public PreGameManager(@NotNull Player player1, @NotNull Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.p1Assigned = new ArrayList<>();
        this.p1Picked = new ArrayList<>();
        this.p2Assigned = new ArrayList<>();
        this.p2Picked = new ArrayList<>();

    }

    /**
     * Returns a Character entity of the given Character config.
     *
     * @param character the character json type
     * @param eID       the eID of the given character
     * @return the character entity of the given character config type
     */
    protected static de.uulm.sopra.team08.data.entity.Character toCharacterEntity(Character character, EntityID eID) {
        return new de.uulm.sopra.team08.data.entity.Character(
                character.getName(),
                character.getMp(),
                character.getAp(),
                character.getHp(),
                character.getMeleeDamage(),
                character.getRangeCombatDamage(),
                character.getRangeCombatReach(),
                eID,
                character.getCharacterID()
        );
    }

    /**
     * Checks if the boolean[] has length 12 and contains 6 true boolean values.
     *
     * @param picked the boolean[] that should be validated
     * @return true if picked is length 12 and contains 6 true valuess
     */
    public static boolean validatePicked(boolean[] picked) {
        boolean valid = picked.length == 12;
        int sum = 0;
        for (boolean b : picked) if (b) sum++;
        return valid && sum == 6;
    }

    /**
     * Splits the characters in the given {@link CharacterConfig} into 2 lists of size 12.
     * Returns a tuple with player1Assigned and player2Assigned of type {@link Character}.
     *
     * @param config a valid config of at least 24 characters
     * @return player1 assigned characters and player2 assigned characters
     */
    public Tuple<ArrayList<Character>, ArrayList<Character>> split(@NotNull CharacterConfig config) {
        if (p1Assigned.size() > 0 || p2Assigned.size() > 0) return null;
        // Load characters from character config
        LinkedList<Character> characters = new LinkedList<>(config.getCharacters());

        final Random rng = new Random();
        // each player gets 12 characters
        final int split = 12;

        // Player 1 assignment
        for (int i = 0; i < split; i++) {
            final int randomIndex = rng.nextInt(characters.size());
            this.p1Assigned.add(characters.remove(randomIndex));
        }

        // Player 2 assignment
        for (int i = 0; i < split; i++) {
            final int randomIndex = rng.nextInt(characters.size());
            this.p2Assigned.add(characters.remove(randomIndex));
        }
        return new Tuple<>(this.p1Assigned, this.p2Assigned);
    }

    /**
     * Returns true if the given choice of characters by the player is valid.
     * Does not create any instance of characters if the choice is not valid.
     *
     * @param player the player that has made his choice
     * @param choice the choice the given player made
     * @return true if the choice was valid
     */
    public boolean playerPicked(@NotNull Player player, boolean[] choice) {
        if (!validatePicked(choice)) return false;

        final boolean p1 = this.player1.equals(player);

        if (!p1 && !this.player2.equals(player)) return false;

        for (int i = 0; i < choice.length; i++) {
            if (choice[i]) {
                if (p1) this.p1Picked.add(toCharacterEntity(p1Assigned.get(i), EntityID.P1));
                else this.p2Picked.add(toCharacterEntity(p2Assigned.get(i), EntityID.P2));
            }
        }
        return true;
    }

    /**
     * See 'Standardisierungsdokument' definition 6.2.5 GameAssignment.
     * Returns the String[] in the defined Format in 6.2.5 for the characterSelection.
     *
     * @param player the player this String[] should be generated for.
     * @return the String[] in the defined Format in 6.2.5 for the characterSelection Message
     */
    public GameAssignment getGameAssignment(@NotNull Player player, String GameID) {
        if (this.player1.equals(player)) {
            return new GameAssignment(GameID, p1Assigned.toArray());
        }
        return new GameAssignment(GameID, p2Assigned.toArray());
    }

    public ArrayList<Character> getP1Assigned() {
        return p1Assigned;
    }

    public ArrayList<de.uulm.sopra.team08.data.entity.Character> getP1Picked() {
        return p1Picked;
    }

    public ArrayList<Character> getP2Assigned() {
        return p2Assigned;
    }

    public ArrayList<de.uulm.sopra.team08.data.entity.Character> getP2Picked() {
        return p2Picked;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

}