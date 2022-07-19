package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.config.character.CharacterConfig;
import de.uulm.sopra.team08.config.partie.PartieConfig;
import de.uulm.sopra.team08.config.scenario.ScenarioConfig;
import de.uulm.sopra.team08.req.MMRequest;

public interface IGameLogic {

    boolean registerPlayer(Player player);

    boolean unregisterPlayer(Player player);

    boolean handle(Player player, MMRequest request);

    boolean isGameRunning();

    ScenarioConfig getScenarioConfig();

    CharacterConfig getCharacterConfig();

    PartieConfig getPartieConfig();

}
