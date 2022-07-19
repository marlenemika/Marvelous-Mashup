package de.uulm.sopra.team08.server.util;

import de.uulm.sopra.team08.config.character.CharacterConfig;
import de.uulm.sopra.team08.config.partie.PartieConfig;
import de.uulm.sopra.team08.config.scenario.ScenarioConfig;
import de.uulm.sopra.team08.req.MMRequest;
import de.uulm.sopra.team08.server.data.IGameLogic;
import de.uulm.sopra.team08.server.data.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TestGameLogic implements IGameLogic {

    private Function<Player, Boolean> registerPlayer;
    private Function<Player, Boolean> unregisterPlayer;
    private BiFunction<Player, MMRequest, Boolean> handle;
    private Callable<Boolean> isGameRunning;
    private Callable<ScenarioConfig> getScenarioConfig;
    private Callable<CharacterConfig> getCharacterConfig;
    private Callable<PartieConfig> getPartieConfig;


    public TestGameLogic() {

    }


    public void setRegisterPlayer(Function<Player, Boolean> registerPlayer) {
        this.registerPlayer = registerPlayer;
    }

    @Override
    public boolean registerPlayer(Player player) {
        return registerPlayer.apply(player);
    }

    public void setUnregisterPlayer(Function<Player, Boolean> unregisterPlayer) {
        this.unregisterPlayer = unregisterPlayer;
    }

    @Override
    public boolean unregisterPlayer(Player player) {
        return unregisterPlayer.apply(player);
    }

    public void setHandle(BiFunction<Player, MMRequest, Boolean> handle) {
        this.handle = handle;
    }

    @Override
    public boolean handle(Player player, MMRequest request) {
        return handle.apply(player, request);
    }

    public void setIsGameRunning(Callable<Boolean> isGameRunning) {
        this.isGameRunning = isGameRunning;
    }

    @Override
    public boolean isGameRunning() {
        return isGameRunning.call();
    }

    public void setGetScenarioConfig(Callable<ScenarioConfig> getScenarioConfig) {
        this.getScenarioConfig = getScenarioConfig;
    }

    @Override
    public ScenarioConfig getScenarioConfig() {
        return getScenarioConfig.call();
    }

    public void setGetCharacterConfig(Callable<CharacterConfig> getCharacterConfig) {
        this.getCharacterConfig = getCharacterConfig;
    }

    @Override
    public CharacterConfig getCharacterConfig() {
        return getCharacterConfig.call();
    }

    public void setGetPartieConfig(Callable<PartieConfig> getPartieConfig) {
        this.getPartieConfig = getPartieConfig;
    }

    @Override
    public PartieConfig getPartieConfig() {
        return getPartieConfig.call();
    }

}
