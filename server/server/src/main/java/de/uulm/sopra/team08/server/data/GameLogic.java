package de.uulm.sopra.team08.server.data;


import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.config.character.CharacterConfig;
import de.uulm.sopra.team08.config.partie.PartieConfig;
import de.uulm.sopra.team08.config.scenario.ScenarioConfig;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.item.*;
import de.uulm.sopra.team08.data.terrain.Board;
import de.uulm.sopra.team08.data.terrain.Portal;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.event.*;
import de.uulm.sopra.team08.req.*;
import de.uulm.sopra.team08.server.Server;
import de.uulm.sopra.team08.server.net.NetworkManager;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GameLogic implements IGameLogic {

    enum LogicState {
        INIT,
        STARTED,
        RUNNING,
        PAUSED,
        WON,
        WAIT_FOR_RECONNECT,
        PAUSED_WAIT
    }

    Random random = new Random();
    private static final Logger LOGGER = LogManager.getLogger(GameLogic.class);
    private static final String GAME_ID = "TEAM08-gameID";
    final GameTimer gameTimer;
    private final Board gameBoard;
    private final ScenarioConfig scenarioConfig;
    private final CharacterConfig characterConfig;
    private final PartieConfig partieConfig;
    private final List<Character> playerCharacters;
    private List<Character> turnOrderCharacters;
    private final List<InfinityStone> infinityStones;
    private final Goose goose;
    private final StanLee stanLee;
    private final TieBreak tieBreak;
    Player player1;
    Player player2;
    PreGameManager preGameManager;
    private int nextRockID;
    private int nextPortalID;
    private List<Portal> portalList = new ArrayList<>();
    private boolean player1left;
    private boolean player2left;
    private int currentRound;
    private int currentTurn;
    private LogicState logicState = LogicState.INIT;
    private Thanos thanos;
    private ThanosLogic thanosLogic;
    private boolean firstSelection;
    private Timer timeoutTimer;

    public GameLogic(Config config) {
        // load config
        scenarioConfig = config.getScenarioConfig();
        characterConfig = config.getCharacterConfig();
        partieConfig = config.getPartieConfig();
        Replay.getInstance().setConfig(config);

        // init Rock ID
        nextRockID = 0;

        // init Portal ID
        nextPortalID = 0;

        // init Board
        final List<List<ScenarioConfig.Scenario>> scenario = scenarioConfig.getScenario();
        this.gameBoard = new Board(scenario.get(0).size(), scenario.size());
        for (int y = 0, scenarioSize = scenario.size(); y < scenarioSize; y++) {
            List<ScenarioConfig.Scenario> row = scenario.get(y);
            for (int x = 0, rowSize = row.size(); x < rowSize; x++) {
                ScenarioConfig.Scenario elem = row.get(x);
                if (elem.equals(ScenarioConfig.Scenario.ROCK)) {
                    gameBoard.setEntityAt(new Rock(nextRockID++), new Tuple<>(x, y));
                }
                if (elem.equals(ScenarioConfig.Scenario.PORTAL)) {
                    Portal newPortal = new Portal(nextPortalID++);
                    gameBoard.setEntityAt(newPortal, new Tuple<>(x, y));
                    portalList.add(newPortal);
                }
            }
        }

        // init round management
        gameTimer = new GameTimer();
        firstSelection = true;
        timeoutTimer = new Timer();
        tieBreak = new TieBreak();

        currentRound = 0;

        // init player management
        player1left = true;
        player2left = true;

        this.playerCharacters = new ArrayList<>(12);
        this.turnOrderCharacters = new ArrayList<>(12);
        this.infinityStones = new ArrayList<>(6);
        infinityStones.add(new SpaceStone(partieConfig.getSpaceStoneCD()));
        infinityStones.add(new MindStone(partieConfig.getMindStoneCD()));
        infinityStones.add(new RealityStone(partieConfig.getRealityStoneCD()));
        infinityStones.add(new PowerStone(partieConfig.getPowerStoneCD()));
        infinityStones.add(new TimeStone(partieConfig.getTimeStoneCD()));
        infinityStones.add(new SoulStone(partieConfig.getSoulStoneCD()));
        goose = new Goose();
        infinityStones.forEach(goose::addToInventory);
        stanLee = new StanLee();

        LOGGER.log(Level.TRACE, "GameLogic successful initialized");
    }

    /**
     * Return true if the two positions are next to each other (including diagonal)
     *
     * @param pos0 coordinate 0
     * @param pos1 coordinate 1
     * @return next to each other?
     */
    static boolean nextToEachOther(Tuple<Integer, Integer> pos0, Tuple<Integer, Integer> pos1) {
        return nextToEachOther(pos0.first, pos0.second, pos1.first, pos1.second);
    }

    /**
     * Return true if the two positions are next to each other (including diagonal)
     *
     * @param x0 x of coordinate 0
     * @param y0 y of coordinate 0
     * @param x1 x of coordinate 1
     * @param y1 y of coordinate 1
     * @return next to each other?
     */
    static boolean nextToEachOther(int x0, int y0, int x1, int y1) {
        return (Math.abs(x0 - x1) <= 1) && (Math.abs(y0 - y1) <= 1);
    }

    private void setLogicState(LogicState state) {
        LOGGER.log(Level.TRACE, String.format("logic state: %s -> %s", this.logicState, state));
        this.logicState = state;
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>WinEvent</li>
     * </ul>
     *
     * @param p p=1 Player1, p=2 Player2
     */
    private void handleWin(int p) {
        setLogicState(LogicState.WON);
        send(new WinEvent(
                p
        ));
        shutdown("game over", 0);
    }

    /**
     * Checks running game
     *
     * @throws IllegalArgumentException condition false
     */
    private void checkInGame() {
        if (!isGameRunning())
            throw new IllegalArgumentException("Not in game");
    }

    /**
     * Can be null before the game started
     *
     * @return Player1
     */
    private @Nullable
    Player getPlayer1() {
        if (player1 == null) LOGGER.warn("Player 1 was null");
        return player1;
    }

    /**
     * Can be null before the game started
     *
     * @return Player1
     */
    private @Nullable
    Player getPlayer2() {
        if (player2 == null) LOGGER.warn("Player 2 was null");
        return player2;
    }

    /**
     * Adds the event to the replay and sends it via {@link NetworkManager} to all
     *
     * @param e The event to send
     */
    private void send(MMEvent e) {
        Replay.getInstance().addEvent(e);
        NetworkManager.getInstance().send(e);
    }

    /**
     * Adds the event to the replay and sends it via {@link NetworkManager} to the specified Player
     *
     * @param e The event to send
     */
    private void send(@Nullable Player player, MMEvent e) {
        Replay.getInstance().addEvent(e);
        NetworkManager.getInstance().send(player, e);
    }

    private void sendGamestate() {
        final GamestateEvent gameStateEvent = getGameStateEvent();
        Replay.getInstance().addEvent(gameStateEvent);
        NetworkManager.getInstance().send(gameStateEvent);
    }

    private void sendGamestate(@Nullable Player player) {
        final GamestateEvent gameStateEvent = getGameStateEvent();
        Replay.getInstance().addEvent(gameStateEvent);
        NetworkManager.getInstance().send(player, gameStateEvent);
    }

    private GamestateEvent getGameStateEvent() {
        List<Integer> cooldowns = new ArrayList<>();
        infinityStones.forEach(infinityStone -> cooldowns.add(infinityStone.getCooldown()));
        List<Tuple<EntityID, Integer>> turnOrder = new LinkedList<Tuple<EntityID, Integer>>();
        for (var character :
                turnOrderCharacters) {
            LOGGER.debug(character);
            if(character!=null) turnOrder.add(character.getIDs());
        }
        for(int i = 0; i<gameBoard.getDimensions().first; i++){
            for(int j = 0; j<gameBoard.getDimensions().second; j++){
                LOGGER.debug("At ["+i+","+j+"]:"+gameBoard.getEntityAt(new Tuple<>(i,j)));
                if (gameBoard.getEntityAt(new Tuple<>(i,j))!=null){
                    if (gameBoard.getEntityAt(new Tuple<>(i,j)).getX()!=i || gameBoard.getEntityAt(new Tuple<>(i,j)).getY()!=j){
                        LOGGER.warn("Violation, should have been on:["+gameBoard.getEntityAt(new Tuple<>(i,j)).getX()+","+gameBoard.getEntityAt(new Tuple<>(i,j)).getY()+"]");
                    }
                }
            }
        }
        return new GamestateEvent(
                gameBoard.getEntities(),
                gameBoard.getDimensions(),
                turnOrder,//turnOrderCharacters.stream().map(Entity::getIDs).collect(Collectors.toList())
                turnOrderCharacters.get(currentTurn).getIDs(),
                cooldowns,
                logicState == LogicState.WON
        );
    }

    /**
     * Init the start of the game
     */
    private void startGame() {
        setLogicState(LogicState.STARTED);
        preGameManager = new PreGameManager(Objects.requireNonNull(getPlayer1()), Objects.requireNonNull(getPlayer2()));
        preGameManager.split(characterConfig);
        send(getPlayer1(), preGameManager.getGameAssignment(getPlayer1(), GAME_ID));
        send(getPlayer2(), preGameManager.getGameAssignment(getPlayer2(), GAME_ID));
    }

    /**
     * closes everything
     *
     * @param msg    The shutdown message
     * @param status The status code
     */
    private void shutdown(String msg, int status) {
        timeoutTimer.cancel();
        Server.shutdown(msg, status);
    }

    void handleRoundSetup() {
        currentRound++;

        // spawn thanos
        if (currentRound == partieConfig.getMaxRounds()) {
            thanos = new Thanos(playerCharacters.stream().mapToInt(Character::getMaxMP).max().orElse(1));
            thanosLogic = new ThanosLogic(thanos, this);
            final Tuple<Integer, Integer> randPos = gameBoard.getRandomFreeRockPosition();
            if (randPos == null) throw new IllegalStateException("no place for thanos");
            final Entity entityTarget = gameBoard.getEntityAt(randPos);
            if (entityTarget instanceof Rock)
                handleDestroyEntity(entityTarget);
            thanos.setCoordinates(randPos);
            handleSpawnEntity(thanos);
        }

        turnOrderCharacters.clear();
        turnOrderCharacters.addAll(playerCharacters);
        if (currentRound >= partieConfig.getMaxRounds() || gameTimer.getTime() >= partieConfig.getMaxGameTime()) {
            turnOrderCharacters.add(0, thanos);
        }
        Collections.shuffle(turnOrderCharacters);
        if (currentRound >= 1 && currentRound <= 6) {
            turnOrderCharacters.add(0, goose);
        }
        if (currentRound == 7) {
            turnOrderCharacters.add(0, stanLee);
        }

        // initialize
        if (currentRound == 1) {
            currentTurn = 0;
            sendGamestate();
        }

        currentTurn = -1;
        //to filter the null entries
        turnOrderCharacters = turnOrderCharacters.stream().filter(x -> x!=null).collect(Collectors.toList());
        send(new RoundSetupEvent(
                currentRound,
                turnOrderCharacters.stream().filter(x -> x!=null).map(Entity::getIDs).collect(Collectors.toList())
        ));

        handleTurn();
    }

    void handleTurn() {
        // end turn
        timeoutTimer.cancel();
        detectWin();

        // begin turn
        currentTurn++;
        if (currentTurn < turnOrderCharacters.size()) {
            final Character currentCharacter = turnOrderCharacters.get(currentTurn);
            currentCharacter.refillStats();
            send(new TurnEvent(
                    turnOrderCharacters.size(),
                    currentCharacter.getIDs()
            ));
            sendGamestate();
            // current turn is not controlled by a player (npc or knocked out character)
            if (currentCharacter.getEID().equals(EntityID.NPC)) {
                Tuple<Integer, Integer> randPos;
                switch (currentCharacter.getId()) {
                    case Goose.ID:
                        final InfinityStoneEntity infEntity = new InfinityStoneEntity(goose.spitOut().getId());
                        randPos = gameBoard.getRandomFreeRockPosition();
                        if (randPos == null) throw new IllegalStateException("no place for goose");

                        if (gameBoard.getEntityAt(randPos) instanceof Rock)
                            handleDestroyEntity(gameBoard.getEntityAt(randPos));
                        goose.setCoordinates(randPos);
                        handleSpawnEntity(goose);
                        handleDestroyEntity(goose);
                        infEntity.setCoordinates(randPos);
                        handleSpawnEntity(infEntity);
                        break;
                    case StanLee.ID:
                        boolean foundNoCharacter = true;
                        List<Character> stanLeeCharacters =
                                playerCharacters.stream().filter(Character::isKnockedOut).collect(Collectors.toList());
                        Collections.shuffle(stanLeeCharacters);
                        while (foundNoCharacter && stanLeeCharacters.size() > 0) {
                            final Character remove = stanLeeCharacters.remove(0);
                            final List<Tuple<Integer, Integer>> freeNeighbourPositions = gameBoard.getFreeNeighbourPositions(remove.getCoordinates());
                            if (freeNeighbourPositions.size() > 0) {
                                handleStanLee(freeNeighbourPositions.get(0));
                                foundNoCharacter = false;
                            }
                        }
                        stanLeeCharacters =
                                playerCharacters.stream()
                                        .filter(character -> !character.isKnockedOut())
                                        .sorted(Comparator.comparingInt(Character::getCurrentHP))
                                        .collect(Collectors.toList());
                        while (foundNoCharacter && stanLeeCharacters.size() > 0) {
                            final Character remove = stanLeeCharacters.remove(0);
                            final List<Tuple<Integer, Integer>> freeNeighbourPositions = gameBoard.getFreeNeighbourPositions(remove.getCoordinates());
                            if (freeNeighbourPositions.size() > 0) {
                                handleStanLee(freeNeighbourPositions.get(0));
                                foundNoCharacter = false;
                            }

                        }

                        break;
                    case Thanos.ID:
                        if (!thanosLogic.doTurn()) {
                            final EntityID winner = tieBreak.useTieBreak();
                            if (winner.equals(EntityID.P1)) handleWin(1);
                            else if (winner.equals(EntityID.P2)) handleWin(2);
                            else throw new IllegalStateException("only players can win");
                        }
                        checkRestTurnOrder(currentTurn);
                        break;
                }
                // next turn
                handleTurn();
            } else if (currentCharacter.isKnockedOut()) {
                // skip knocked out players
                // next turn
                handleTurn();
            } else {
                // round controlled by player
                timeoutTimer = new Timer();
                final Player currentPlayerForTimer = getCurrentPlayer();
                timeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        send(currentPlayerForTimer, new TurnTimeoutEvent());
                        handleTurn();
                        // not really needed but just to be sure
                        timeoutTimer.cancel();
                    }
                }, partieConfig.getMaxRoundTime() * 1000L);
            }
        } else {
            handleRoundSetup();
        }
    }

    private void checkRestTurnOrder(int currentTurn) {
        for(int i = 0;i<playerCharacters.size();i++){
            final var character = playerCharacters.get(i);
            if (gameBoard.getEntities().stream().noneMatch(entity -> entity.doesIDMatch(character.getIDs()))){
                playerCharacters.remove(character);
            }
        }
        int checkInt = currentTurn;
        for (;checkInt<turnOrderCharacters.size();checkInt++){
            final int currentInt = checkInt;
            if(playerCharacters.stream().noneMatch(character -> character.getIDs().equals(turnOrderCharacters.get(currentInt)))){
                turnOrderCharacters.remove(currentInt);
                checkInt--;
            }
        }
    }

    void handleStanLee(Tuple<Integer, Integer> pos) {
        stanLee.setCoordinates(pos);
        handleSpawnEntity(stanLee);

        playerCharacters.forEach(character -> {
            if (gameBoard.canSee(pos, character.getCoordinates())) {
                handleHealed(character, character.getMaxHP() - character.getCurrentHP());
            }
        });

        handleDestroyEntity(stanLee);
    }

    void detectTurnOver() {
        if (turnOrderCharacters.get(currentTurn).isTurnFinished()) {
            handleTurn();
        }
    }

    /**
     * Detects the victory condition for the running game and calls the handle method
     * It doesn't look for thanos and tie breaks
     */
    void detectWin() {
        final Optional<Character> optWinnerPlayerCharacter = playerCharacters.stream().filter(Character::isFull).findAny();

        if (optWinnerPlayerCharacter.isPresent()) {
            if (optWinnerPlayerCharacter.get().getEID().equals(EntityID.P1))
                handleWin(1);
            else if (optWinnerPlayerCharacter.get().getEID().equals(EntityID.P2))
                handleWin(2);
            else throw new IllegalStateException("not a player character");
        }
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>MeleeAttackEvent</li>
     *     <li>DestroyedEntityEvent</li>
     *     <li>TakenDamageEvent</li>
     *     <li>ConsumedAPEvent</li>
     * </ul>
     *
     * @param req MeleeAttackRequest
     */
    void handleRequest(MeleeAttackRequest req) {
        final Character characterOrigin = ((Character) gameBoard.getEntityAt(req.getOriginField()));
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        int damage = characterOrigin.getMeleeAttack();

        handleMeleeAttack(characterOrigin, entityTarget, damage);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>MeleeAttackEvent</li>
     *     <li>DestroyedEntityEvent</li>
     *     <li>TakenDamageEvent</li>
     *     <li>ConsumedAPEvent</li>
     * </ul>
     *
     * @param characterOrigin attacking character
     * @param entityTarget    attacked target
     * @param damage          damage
     */
    void handleMeleeAttack(Character characterOrigin, Entity entityTarget, int damage) {
        send(new MeleeAttackEvent(
                characterOrigin.getIDs(),
                entityTarget.getIDs(),
                characterOrigin.getCoordinates(),
                entityTarget.getCoordinates()
        ));
        handleAttacked(characterOrigin, entityTarget, damage);
        handleConsumedAP(characterOrigin, 1);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>RangedAttackEvent</li>
     *     <li>DestroyedEntityEvent</li>
     *     <li>TakenDamageEvent</li>
     *     <li>ConsumedAPEvent</li>
     * </ul>
     *
     * @param req RangedAttackRequest
     */
    void handleRequest(RangedAttackRequest req) {
        final Character characterOrigin = ((Character) gameBoard.getEntityAt(req.getOriginField()));
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        int damage = characterOrigin.getRangeAttack();

        handleRangedAttack(characterOrigin, entityTarget, damage);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>RangedAttackEvent</li>
     *     <li>DestroyedEntityEvent</li>
     *     <li>TakenDamageEvent</li>
     *     <li>ConsumedAPEvent</li>
     * </ul>
     *
     * @param characterOrigin attacking character
     * @param entityTarget    attacked target
     * @param damage          damage
     */
    void handleRangedAttack(Character characterOrigin, Entity entityTarget, int damage) {
        send(new RangedAttackEvent(
                characterOrigin.getIDs(),
                entityTarget.getIDs(),
                characterOrigin.getCoordinates(),
                entityTarget.getCoordinates()
        ));
        handleAttacked(characterOrigin, entityTarget, damage);
        handleConsumedAP(characterOrigin, 1);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>MoveEvent</li>
     *     <li>ConsumedMPEvent</li>
     *     <li>DestroyedEntityEvent</li>
     * </ul>
     *
     * @param req MoveRequest
     */
    void handleRequest(MoveRequest req) {
        Tuple<Integer, Integer> posOrigin = req.getOriginField();
        Tuple<Integer, Integer> posTarget = req.getTargetField();
        final Character characterOrigin = (Character) gameBoard.getEntityAt(posOrigin);

        handleMove(characterOrigin, posTarget);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>MoveEvent</li>
     *     <li>ConsumedMPEvent</li>
     *     <li>DestroyedEntityEvent</li>
     * </ul>
     *
     * @param character character to move
     * @param target    field coordinate to move to
     */
    void handleMove(Character character, Tuple<Integer, Integer> target) {
        send(new MoveEvent(
                character.getIDs(),
                character.getCoordinates(),
                target
        ));

        var teleported = false;
        Tuple<Integer, Integer> teleportTarget;

        Entity entityTarget = gameBoard.getEntityAt(target);
        gameBoard.freeEntityAt(character.getCoordinates());
        if (entityTarget instanceof Character) {
            gameBoard.setEntityAt(entityTarget, character.getCoordinates());
            gameBoard.freeEntityAt(target);
        } else if (entityTarget instanceof Portal) {
            Portal targetPortal = portalList.get(random.nextInt(portalList.size()));
            teleportTarget = gameBoard.getRecursiveFreePosition(targetPortal.getCoordinates());
            if (teleportTarget == null)
                throw new IllegalStateException("no space for teleportation found");
            send(new TeleportedEvent(
                    character.getIDs(),
                    entityTarget.getCoordinates(),
                    teleportTarget,
                    entityTarget.getIDs(),
                    targetPortal.getIDs()
            ));
            teleported = true;
            gameBoard.setEntityAt(character, teleportTarget);
        } else if (entityTarget instanceof InfinityStoneEntity) {
            // pick up infinity stone
            final InfinityStone infinityStone = this.infinityStones.get(entityTarget.getId());
            if (infinityStone.getId() != entityTarget.getId())
                throw new IllegalStateException("couldn't find infinity stone");

            handleDestroyEntity(entityTarget);
            character.addToInventory(infinityStone);

            tieBreak.updateInfinityStone(getPlayerCharacters());
        }

        if (!teleported)
            gameBoard.setEntityAt(character, target);

        handleConsumedMP(character, 1);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>ExchangeInfinityStoneEvent</li>
     *     <li>ConsumedAPEvent</li>
     * </ul>
     *
     * @param req ExchangeInfinityStoneRequest
     */
    void handleRequest(ExchangeInfinityStoneRequest req) {
        Tuple<Integer, Integer> posOrigin = req.getOriginField();
        Tuple<Integer, Integer> posTarget = req.getTargetField();
        final Character characterOrigin = (Character) gameBoard.getEntityAt(posOrigin);
        final Character characterTarget = (Character) gameBoard.getEntityAt(posTarget);

        final InfinityStone infinityStone = Arrays.stream(characterOrigin.getInventory())
                .filter(stone -> stone.getId() == req.getStoneType().second)
                .findAny()
                .orElseThrow(IllegalStateException::new);

        handleExchangeInfinityStone(characterOrigin, characterTarget, infinityStone);
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>ExchangeInfinityStoneEvent</li>
     *     <li>ConsumedAPEvent</li>
     * </ul>
     *
     * @param characterOrigin active character
     * @param characterTarget passive character
     * @param infinityStone   infinity stone
     */
    void handleExchangeInfinityStone(Character characterOrigin, Character characterTarget, InfinityStone infinityStone) {
        send(new ExchangeInfinityStoneEvent(
                characterOrigin.getIDs(),
                characterTarget.getIDs(),
                characterOrigin.getCoordinates(),
                characterTarget.getCoordinates(),
                infinityStone.getIDs()
        ));

        characterOrigin.removeFromInventory(infinityStone);
        characterTarget.addToInventory(infinityStone);
        handleConsumedAP(characterOrigin, 1);

        tieBreak.updateInfinityStone(getPlayerCharacters());
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>UseInfinityStoneRequest</li>
     *     <li>ConsumedAPEvent</li>
     *     <li>TakenDamageEvent</li>
     *     <li>DestroyedEntityEvent</li>
     *     <li>SpawnEntityEvent</li>
     *     <li>HealedEvent</li>
     * </ul>
     *
     * @param req UseInfinityStoneRequest
     */
    void handleRequest(UseInfinityStoneRequest req) {
        Tuple<Integer, Integer> posOrigin = req.getOriginField();
        Tuple<Integer, Integer> posTarget = req.getTargetField();
        final Character characterOrigin = (Character) gameBoard.getEntityAt(posOrigin);
        final Entity entityTarget = gameBoard.getEntityAt(posTarget);

        final InfinityStone infinityStone = Arrays.stream(characterOrigin.getInventory())
                .filter(stone -> stone.getId() == req.getStoneType().second)
                .findAny()
                .orElseThrow(IllegalStateException::new);

        infinityStone.resetCD();
        send(new UseInfinityStoneEvent(
                characterOrigin.getIDs(),
                req.getTargetEntity(gameBoard),
                characterOrigin.getCoordinates(),
                req.getTargetField(),
                infinityStone.getIDs()
        ));

        handleConsumedAP(characterOrigin, 1);

        switch (infinityStone.getId()) {
            case SpaceStone.ID:
                gameBoard.freeEntityAt(posOrigin);
                gameBoard.setEntityAt(characterOrigin, posTarget);
                return;

            case MindStone.ID:
                int damageMindStone = partieConfig.getMindStoneDMG();
                gameBoard.lineLaser(posOrigin, posTarget).forEach(pos -> {
                    Entity entity = gameBoard.getEntityAt(pos);
                    if (entity == null) return;
                    handleAttacked(characterOrigin, entity, damageMindStone);
                });
                return;

            case RealityStone.ID:
                if (req.getOriginEntity().equals(req.getTargetEntity(gameBoard))) {
                    // create
                    Rock newRock = new Rock(nextRockID++);
                    newRock.setCoordinates(posTarget);
                    handleSpawnEntity(newRock);
                } else {
                    // destroy
                    handleDestroyEntity(entityTarget);
                }
                return;

            case PowerStone.ID:
                int damagePowerStone = characterOrigin.getMeleeAttack() * 2;
                handleAttacked(characterOrigin, entityTarget, damagePowerStone);

                int amount = characterOrigin.getMaxHP() / 10;
                int damagePowerStoneSelf = amount < characterOrigin.getCurrentHP() ? amount : characterOrigin.getCurrentHP() - 1;
                handleTakenDamage(characterOrigin, damagePowerStoneSelf);
                return;

            case TimeStone.ID:
                characterOrigin.refillStats();
                return;

            case SoulStone.ID:
                Character characterTarget = (Character) entityTarget;
                int heal = characterTarget.getMaxHP() - characterTarget.getCurrentHP();
                handleHealed(characterTarget, heal);
        }
    }

    /**
     * Changes the state of the game and sends the events
     * Event: ConsumedAPEvent
     */
    void handleConsumedAP(Character character, int amount) {
        character.updateUsedAP(amount);
        send(new ConsumedAPEvent(
                character.getIDs(),
                character.getCoordinates(),
                amount
        ));
    }

    /**
     * Changes the state of the game and sends the events
     * Event: ConsumedMPEvent
     */
    void handleConsumedMP(Character character, int amount) {
        character.updateUsedMP(amount);
        send(new ConsumedMPEvent(
                character.getIDs(),
                character.getCoordinates(),
                amount
        ));
    }

    /**
     * Changes the state of the game and sends the events if actual damage more than 0
     * Event: TakenDamageEvent
     */
    void handleTakenDamage(Entity entity, int damage) {
        boolean sendEvent = false;
        if (entity instanceof Character) {
            sendEvent = ((Character) entity).damageCharacter(damage) > 0;
        } else if (entity instanceof Rock) {
            ((Rock) entity).damage(damage);
            sendEvent = true;
        } else throw new IllegalStateException("Can only do damage to Rock or Character");
        if (sendEvent)
            send(new TakenDamageEvent(
                    entity.getIDs(),
                    entity.getCoordinates(),
                    damage
            ));
    }

    /**
     * Changes the state of the game and sends the events
     * Event: DestroyEntityEvent
     */
    void handleDestroyEntity(Entity entity) {
        gameBoard.freeEntityAt(entity.getCoordinates());
        send(new DestroyedEntityEvent(
                entity.getCoordinates(),
                entity.getIDs()
        ));
        sendGamestate();
    }

    /**
     * Changes the state of the game and sends the events
     * Event: SpawnEntityEvent
     */
    void handleSpawnEntity(Entity entity) {
        gameBoard.setEntityAt(entity, entity.getCoordinates());
        send(new SpawnEntityEvent(
                entity
        ));
        if (currentTurn < turnOrderCharacters.size()) sendGamestate();
    }

    /**
     * Changes the state of the game and sends the events
     * Event: HealedEvent
     */
    void handleHealed(Character character, int heal) {
        character.healCharacter(heal);
        send(new HealedEvent(
                character.getIDs(),
                character.getCoordinates(),
                heal
        ));
        sendGamestate();
    }

    /**
     * Checks the MeleeAttackRequest for these conditions:
     * <ul>
     *     <li>Field are not equal</li>
     *     <li>Entities not null</li>
     *     <li>Entities in Request match with actual Entities on board</li>
     *     <li>Entities are next to each other</li>
     *     <li>Player authorized</li>
     *     <li>Entity origin a Character</li>
     *     <li>Entity target a Rock or Character</li>
     *     <li>Attacker has enough AP</li>
     *     <li>Attacker is at turn</li>
     *     <li>Characters are not knocked out</li>
     *     <li>No friendly fire</li>
     * </ul>
     * <p>
     * Optional: Melee damage(Value) matches game state
     *
     * @param req    Request
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(MeleeAttackRequest req, Player player) throws IllegalArgumentException {
        final Entity entityOrigin = gameBoard.getEntityAt(req.getOriginField());
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        if (req.getOriginField().equals(req.getTargetField()))
            throw new IllegalArgumentException("fields equal");
        if (entityOrigin == null || entityTarget == null)
            throw new IllegalArgumentException("entity null");
        if (!(entityOrigin.getIDs().equals(req.getOriginEntity()) && entityTarget.getIDs().equals(req.getTargetEntity())))
            throw new IllegalArgumentException("entity request != board");
        if (!nextToEachOther(req.getOriginField(), req.getTargetField()))
            throw new IllegalArgumentException("field not next to each other");
        if (!(player.equals(getPlayer1()) && entityOrigin.getEID().equals(EntityID.P1)
                || player.equals(getPlayer2()) && entityOrigin.getEID().equals(EntityID.P2)))
            throw new IllegalArgumentException("player not authorized");

        if (entityOrigin instanceof Character) {
            Character characterOrigin = (Character) entityOrigin;
            if (characterOrigin.getCurrentAP() <= 0)
                throw new IllegalArgumentException("character not enough ap");
            if (!characterOrigin.equals(turnOrderCharacters.get(currentTurn)))
                throw new IllegalArgumentException("not characters turn");

            if (entityTarget instanceof Character) {
                Character characterTarget = (Character) entityTarget;
                if (characterOrigin.isKnockedOut() || characterTarget.isKnockedOut())
                    throw new IllegalArgumentException("character knocked out");
                if (characterOrigin.getEID().equals(characterTarget.getEID()))
                    throw new IllegalArgumentException("friendly fire");
            } else if (!(entityTarget instanceof Rock))
                throw new IllegalArgumentException("entity not a melee attack target");
        } else throw new IllegalArgumentException("entity not a character");
    }

    /**
     * Checks the RangedAttackRequest for these conditions:
     * <ul>
     *     <li>Field are not equal</li>
     *     <li>Entities not null</li>
     *     <li>Entities in Request match with actual Entities on board</li>
     *     <li>Entities have line of sight</li>
     *     <li>Player authorized</li>
     *     <li>Entity origin a Character</li>
     *     <li>Entity target a Rock or Character</li>
     *     <li>Attacker has enough AP</li>
     *     <li>Attacker has enough range</li>
     *     <li>Attacker is at turn</li>
     *     <li>Characters are not knocked out</li>
     *     <li>No friendly fire</li>
     * </ul>
     * <p>
     * Optional: Ranged damage(Value) matches game state
     *
     * @param req    Request
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(RangedAttackRequest req, Player player) throws IllegalArgumentException {
        final Entity entityOrigin = gameBoard.getEntityAt(req.getOriginField());
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        if (req.getOriginField().equals(req.getTargetField()))
            throw new IllegalArgumentException("fields equal");
        if (entityOrigin == null || entityTarget == null)
            throw new IllegalArgumentException("entity null");
        if (!(entityOrigin.getIDs().equals(req.getOriginEntity()) && entityTarget.getIDs().equals(req.getTargetEntity())))
            throw new IllegalArgumentException("entity request != board");
        if (!gameBoard.canSee(req.getOriginField(), req.getTargetField()))
            throw new IllegalArgumentException("no line of sight");
        if (!(player.equals(getPlayer1()) && entityOrigin.getEID().equals(EntityID.P1)
                || player.equals(getPlayer2()) && entityOrigin.getEID().equals(EntityID.P2)))
            throw new IllegalArgumentException("player not authorized");

        if (entityOrigin instanceof Character) {
            Character characterOrigin = (Character) entityOrigin;
            if (characterOrigin.getCurrentAP() <= 0)
                throw new IllegalArgumentException("character not enough ap");
            int distance = Board.distance(req.getOriginField(), req.getTargetField());
            if (distance > characterOrigin.getAttackRange() || distance <= 1)
                throw new IllegalArgumentException("not in range");
            if (!characterOrigin.equals(turnOrderCharacters.get(currentTurn)))
                throw new IllegalArgumentException("not characters turn");

            if (entityTarget instanceof Character) {
                Character characterTarget = (Character) entityTarget;
                if (characterOrigin.isKnockedOut() || characterTarget.isKnockedOut())
                    throw new IllegalArgumentException("character knocked out");
                if (characterOrigin.getEID().equals(characterTarget.getEID()))
                    throw new IllegalArgumentException("friendly fire");
            } else if (!(entityTarget instanceof Rock))
                throw new IllegalArgumentException("entity not a ranged attack target");
        } else throw new IllegalArgumentException("entity not a character");
    }

    /**
     * Checks the MoveRequest for these conditions:
     * <ul>
     *     <li>Field are not equal</li>
     *     <li>Entity Origin not null</li>
     *     <li>Entity in Request matches with actual Entity on board</li>
     *     <li>Fields are next to each other</li>
     *     <li>Player authorized</li>
     *     <li>Entity origin a Character</li>
     *     <li>Character has enough MP</li>
     *     <li>Character isn't knocked out</li>
     *     <li>Field free or free to move onto it</li>
     *     <li>Character origin is at turn</li>
     * </ul>
     *
     * @param req    Request
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(MoveRequest req, Player player) throws IllegalArgumentException {
        final Entity entityOrigin = gameBoard.getEntityAt(req.getOriginField());
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        if (req.getOriginField().equals(req.getTargetField()))
            throw new IllegalArgumentException("fields equal");
        if (entityOrigin == null)
            throw new IllegalArgumentException("entity null");
        if (!(entityOrigin.getIDs().equals(req.getOriginEntity())))
            throw new IllegalArgumentException("entity request != board");
        if (!nextToEachOther(req.getOriginField(), req.getTargetField()))
            throw new IllegalArgumentException("field not next to each other");
        if (!(player.equals(getPlayer1()) && entityOrigin.getEID().equals(EntityID.P1)
                || player.equals(getPlayer2()) && entityOrigin.getEID().equals(EntityID.P2)))
            throw new IllegalArgumentException("player not authorized");

        if (entityOrigin instanceof Character) {
            Character characterOrigin = (Character) entityOrigin;

            if (characterOrigin.getCurrentMP() <= 0)
                throw new IllegalArgumentException("character not enough mp");
            if (characterOrigin.isKnockedOut())
                throw new IllegalArgumentException("character knocked out");
            if (entityTarget != null && entityTarget.blocksMovement())
                throw new IllegalArgumentException("character movement blocked");
            if (!characterOrigin.equals(turnOrderCharacters.get(currentTurn)))
                throw new IllegalArgumentException("not characters turn");
        } else throw new IllegalArgumentException("entity not a character");
    }

    /**
     * Checks the ExchangeInfinityStoneRequest for these conditions:
     * <ul>
     *     <li>Field are not equal</li>
     *     <li>Entities not null</li>
     *     <li>Entities in Request match with actual Entities on board</li>
     *     <li>Fields are next to each other</li>
     *     <li>Player authorized</li>
     *     <li>EID correct</li>
     *     <li>Entities are characters</li>
     *     <li>Character origin has enough AP</li>
     *     <li>Character origin isn't knocked out</li>
     *     <li>Character origin is at turn</li>
     *     <li>Infinity stone is in character origin inventory</li>
     * </ul>
     *
     * @param req    Request
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(ExchangeInfinityStoneRequest req, Player player) throws IllegalArgumentException {
        final Entity entityOrigin = gameBoard.getEntityAt(req.getOriginField());
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        if (req.getOriginField().equals(req.getTargetField()))
            throw new IllegalArgumentException("fields equal");
        if (entityOrigin == null || entityTarget == null)
            throw new IllegalArgumentException("entity null");
        if (!(entityOrigin.getIDs().equals(req.getOriginEntity()) && entityTarget.getIDs().equals(req.getTargetEntity())))
            throw new IllegalArgumentException("entity request != board");
        if (!nextToEachOther(req.getOriginField(), req.getTargetField()))
            throw new IllegalArgumentException("field not next to each other");
        if (!req.getStoneType().first.equals(EntityID.INFINITYSTONES))
            throw new IllegalArgumentException("infinity stone EID incorrect");
        if (!(player.equals(getPlayer1()) && entityOrigin.getEID().equals(EntityID.P1)
                || player.equals(getPlayer2()) && entityOrigin.getEID().equals(EntityID.P2)))
            throw new IllegalArgumentException("player not authorized");

        if (entityOrigin instanceof Character) {
            final Character characterOrigin = (Character) entityOrigin;

            if (characterOrigin.getCurrentAP() <= 0)
                throw new IllegalArgumentException("character not enough ap");
            if (characterOrigin.isKnockedOut())
                throw new IllegalArgumentException("character knocked out");
            if (!characterOrigin.equals(turnOrderCharacters.get(currentTurn)))
                throw new IllegalArgumentException("not characters turn");
            final Optional<InfinityStone> opt = characterOrigin.getInventoryList().stream()
                    .filter(stone -> stone.getId() == req.getStoneType().second)
                    .findAny();
            if (opt.isEmpty())
                throw new IllegalArgumentException("infinity stone not in inventory");
            if (!(entityTarget instanceof Character))
                throw new IllegalArgumentException("entity not a character");
        } else throw new IllegalArgumentException("entity not a character");
    }

    /**
     * Checks the UseInfinityStoneRequest for these conditions:
     * <ul>
     *     <li>Entity Origin not null</li>
     *     <li>Entity in Request matches with actual Entity on board</li>
     *     <li>EID correct</li>
     *     <li>Player authorized</li>
     *     <li>Entity origin is a character</li>
     *     <li>Character origin has enough AP</li>
     *     <li>Character origin isn't knocked out</li>
     *     <li>Infinity stone is in character origin inventory</li>
     *     <li>Infinity stone is not on cooldown</li>
     *     <li>Character origin is at turn</li>
     *     <li>Space Stone: Field are not equal</li>
     *     <li>Space Stone: Field free</li>
     *     <li>Space Stone: origin == target</li>
     *     <li>Mind Stone: Field are not equal</li>
     *     <li>Mind Stone: Entity target in Request matches with actual Entity on board</li>
     *     <li>Reality Stone: Field are not equal</li>
     *     <li>Reality Stone: rock or free</li>
     *     <li>Reality Stone: Fields next to each other</li>
     *     <li>Reality Stone: origin == target on creation</li>
     *     <li>Reality Stone: Entity in Request matches with actual Entity on board on destruction</li>
     *     <li>Power Stone: Field are not equal</li>
     *     <li>Power Stone: Entity target not null</li>
     *     <li>Power Stone: Fields next to each other</li>
     *     <li>Power Stone: Entity in Request matches with actual Entity on board</li>
     *     <li>Power Stone: Melee attack target</li>
     *     <li>Power Stone: no friendly fire</li>
     *     <li>Power Stone: not knocked out</li>
     *     <li>Time Stone: Field are equal</li>
     *     <li>Time Stone: origin == target on creation</li>
     *     <li>Soul Stone: Field are not equal</li>
     *     <li>Soul Stone: Fields next to each other</li>
     *     <li>Soul Stone: Entity in Request matches with actual Entity on board</li>
     *     <li>Soul Stone: Entity target is a character</li>
     *     <li>Soul Stone: Character target is knocked out</li>
     * </ul>
     *
     * @param req    Request
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(UseInfinityStoneRequest req, Player player) throws IllegalArgumentException {
        final Entity entityOrigin = gameBoard.getEntityAt(req.getOriginField());
        final Entity entityTarget = gameBoard.getEntityAt(req.getTargetField());

        if (entityOrigin == null)
            throw new IllegalArgumentException("entity null");
        if (!(entityOrigin.getIDs().equals(req.getOriginEntity())))
            throw new IllegalArgumentException("entity request != board");
        if (!req.getStoneType().first.equals(EntityID.INFINITYSTONES))
            throw new IllegalArgumentException("infinity stone EID incorrect");
        if (!(player.equals(getPlayer1()) && entityOrigin.getEID().equals(EntityID.P1)
                || player.equals(getPlayer2()) && entityOrigin.getEID().equals(EntityID.P2)))
            throw new IllegalArgumentException("player not authorized");

        if (entityOrigin instanceof Character) {
            final Character characterOrigin = (Character) entityOrigin;

            if (characterOrigin.getCurrentAP() <= 0)
                throw new IllegalArgumentException("character not enough ap");
            if (characterOrigin.isKnockedOut())
                throw new IllegalArgumentException("character knocked out");
            final Optional<InfinityStone> opt = characterOrigin.getInventoryList().stream()
                    .filter(stone -> stone.getId() == req.getStoneType().second)
                    .findAny();
            if (opt.isEmpty())
                throw new IllegalArgumentException("infinity stone not in inventory");
            final InfinityStone infinityStone = opt.get();
            if (!infinityStone.isOffCD())
                throw new IllegalArgumentException("infinity stone on cooldown");
            if (!characterOrigin.equals(turnOrderCharacters.get(currentTurn)))
                throw new IllegalArgumentException("not characters turn");

            switch (infinityStone.getId()) {
                case SpaceStone.ID:
                    if (req.getOriginField().equals(req.getTargetField()))
                        throw new IllegalArgumentException("fields equal");
                    if (!req.getOriginEntity().equals(req.getTargetEntity(gameBoard)))
                        throw new IllegalArgumentException("origin entity != target entity");
                    if (entityTarget != null)
                        throw new IllegalArgumentException("space stone target not free");
                    return;
                case MindStone.ID:
                    if (req.getOriginField().equals(req.getTargetField()))
                        throw new IllegalArgumentException("fields equal");
                    if (!(entityTarget.getIDs().equals(req.getTargetEntity(gameBoard))))
                        throw new IllegalArgumentException("entity target request != board");
                    return;
                case RealityStone.ID:
                    if (req.getOriginField().equals(req.getTargetField()))
                        throw new IllegalArgumentException("fields equal");
                    if (!nextToEachOther(req.getOriginField(), req.getTargetField()))
                        throw new IllegalArgumentException("fields not next to each other");
                    if (entityTarget == null) {
                        // spawn
                        if (!req.getOriginEntity().equals(req.getTargetEntity(gameBoard)))
                            throw new IllegalArgumentException("origin entity != target entity");
                    } else if (entityTarget instanceof Rock) {
                        // destroy
                        if (!(entityTarget.getIDs().equals(req.getTargetEntity(gameBoard))))
                            throw new IllegalArgumentException("entity target request != board");
                    } else throw new IllegalArgumentException("not rock or free");
                    return;
                case PowerStone.ID:
                    if (req.getOriginField().equals(req.getTargetField()))
                        throw new IllegalArgumentException("fields equal");
                    if (entityTarget == null)
                        throw new IllegalArgumentException("entity null");
                    if (!(entityTarget.getIDs().equals(req.getTargetEntity(gameBoard))))
                        throw new IllegalArgumentException("entity target request != board");
                    if (!nextToEachOther(req.getOriginField(), req.getTargetField()))
                        throw new IllegalArgumentException("field not next to each other");
                    if (entityTarget instanceof Character) {
                        final Character characterTarget = (Character) entityTarget;
                        if (characterOrigin.getEID().equals(characterTarget.getEID()))
                            throw new IllegalArgumentException("friendly fire");
                        if (characterTarget.isKnockedOut())
                            throw new IllegalArgumentException("character knocked out");
                    } else if (!(entityTarget instanceof Rock))
                        throw new IllegalArgumentException("entity not a melee attack target");
                    return;
                case TimeStone.ID:
                    if (!req.getOriginField().equals(req.getTargetField()))
                        throw new IllegalArgumentException("fields not equal");
                    if (!req.getOriginEntity().equals(req.getTargetEntity(gameBoard)))
                        throw new IllegalArgumentException("origin entity != target entity");
                    return;
                case SoulStone.ID:
                    if (req.getOriginField().equals(req.getTargetField()))
                        throw new IllegalArgumentException("fields equal");
                    if (!(entityTarget.getIDs().equals(req.getTargetEntity(gameBoard))))
                        throw new IllegalArgumentException("entity target request != board");
                    if (!nextToEachOther(req.getOriginField(), req.getTargetField()))
                        throw new IllegalArgumentException("field not next to each other");
                    if (entityTarget instanceof Character) {
                        final Character characterTarget = (Character) entityTarget;
                        if (!characterTarget.isKnockedOut())
                            throw new IllegalArgumentException("character not knocked out");
                    } else throw new IllegalArgumentException("entity not a character");
            }
        } else throw new IllegalArgumentException("entity not a character");
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * all event round, turn beginning and npc events
     *
     * @param req EndRoundRequest
     * @see GameLogic#handleTurn()
     */
    void handleRequest(EndRoundRequest req) {
        handleTurn();
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>PauseStartEvent</li>
     * </ul>
     *
     * @param req PauseStartRequest
     */
    void handleRequest(PauseStartRequest req) {
        gameTimer.pause();
        setLogicState(LogicState.PAUSED);
        send(new PauseStartEvent());
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>PauseStopEvent</li>
     * </ul>
     *
     * @param req PauseStopRequest
     */
    void handleRequest(PauseStopRequest req) {
        gameTimer.unpause();
        setLogicState(LogicState.RUNNING);
        send(new PauseStopEvent());
    }

    /**
     * Changes the state of the game and generates the events
     * Events possible:
     * <ul>
     *     <li>TakenDamageEvent</li>
     *     <li>DestroyedEntityEvent</li>
     *     <li>SpawnEntityEvent</li>
     * </ul>
     *
     * @param characterOrigin attacking character
     * @param entityTarget    Entity to be attacked
     */
    void handleAttacked(Character characterOrigin, Entity entityTarget, int damage) {
        if (damage <= 0) return;
        if (entityTarget instanceof Character) {
            Character characterTarget = (Character) entityTarget;
            boolean knockOut = characterTarget.isKnockedOut();
            handleTakenDamage(entityTarget, damage);
            knockOut = !knockOut && characterTarget.isKnockedOut(); // true if character gets knocked out

            tieBreak.updateDamageDealtToEnemies(characterOrigin.getEID(), damage);

            if (knockOut) {
                // drop infinity stones
                characterTarget.getInventoryList().forEach(infinityStone -> {
                    final Tuple<Integer, Integer> pos = gameBoard.getRecursiveFreePosition(characterTarget.getCoordinates());
                    if (pos == null)
                        throw new IllegalStateException("no space for dropped infinity stone");
                    final InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(infinityStone.getId());
                    infinityStoneEntity.setCoordinates(pos);
                    handleSpawnEntity(infinityStoneEntity);
                    characterTarget.removeFromInventory(infinityStone);
                });

                tieBreak.updateKnockOut(characterOrigin.getEID());
            }

        } else if (entityTarget instanceof Rock) {
            handleTakenDamage(entityTarget, damage);
            if (((Rock) entityTarget).isDestroyed()) {
                handleDestroyEntity(entityTarget);
            }
        }
    }

    /**
     * Checks the EndRoundRequest for these conditions:
     * <ul>
     *     <li>Player authorized</li>
     * </ul>
     *
     * @param req    EndRoundRequest
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(EndRoundRequest req, Player player) throws IllegalArgumentException {
        if (!player.equals(getCurrentPlayer()))
            throw new IllegalArgumentException("player not authorized");
    }

    /**
     * Checks the PauseStartRequest for these conditions:
     * <ul>
     *     <li>Client authorized</li>
     *     <li>Not already paused</li>
     * </ul>
     *
     * @param req    PauseStartRequest
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(PauseStartRequest req, Player player) throws IllegalArgumentException {
        if (!player.getRole().equals(Role.PLAYER))
            throw new IllegalArgumentException("client not authorized");
        if (logicState.equals(LogicState.PAUSED) || logicState.equals(LogicState.PAUSED_WAIT))
            throw new IllegalArgumentException("already paused");
    }

    /**
     * Checks the PauseStopRequest for these conditions:
     * <ul>
     *     <li>Client authorized</li>
     *     <li>paused</li>
     * </ul>
     *
     * @param req    PauseStopRequest
     * @param player Player
     * @throws IllegalArgumentException condition false
     */
    void checkRequest(PauseStopRequest req, Player player) throws IllegalArgumentException {
        if (!player.getRole().equals(Role.PLAYER))
            throw new IllegalArgumentException("client not authorized");
        if (!(logicState.equals(LogicState.PAUSED) || logicState.equals(LogicState.PAUSED_WAIT)))
            throw new IllegalArgumentException("not paused");
    }

    /**
     * Checks for pause
     *
     * @throws IllegalArgumentException condition false
     */
    void checkPaused() throws IllegalArgumentException {
        if (logicState.equals(LogicState.PAUSED) || logicState.equals(LogicState.PAUSED_WAIT))
            throw new IllegalArgumentException("game paused");
    }

    /**
     * @return returns the player who is currently on the turn
     */
    Player getCurrentPlayer() {
        if (turnOrderCharacters.get(currentTurn).getEID().equals(EntityID.P1))
            return getPlayer1();
        else if (turnOrderCharacters.get(currentTurn).getEID().equals(EntityID.P2))
            return getPlayer2();
        else throw new IllegalStateException("current character not from player");
    }

    void handleRequest(CharacterSelection r, Player player) {
        if (isGameRunning() || !PreGameManager.validatePicked(r.getCharacters())) {
            send(new ConfirmSelection(false));
            return;
        }
        if (firstSelection) {
            firstSelection = false;
            preGameManager.playerPicked(player, r.getCharacters());
            send(player, new ConfirmSelection(true));
        } else {
            preGameManager.playerPicked(player, r.getCharacters());
            var p1Characters = preGameManager.getP1Picked().stream().map(x -> new de.uulm.sopra.team08.config.character.Character(x)).collect(Collectors.toList()).toArray();
            var p2Characters = preGameManager.getP2Picked().stream().map(x -> new de.uulm.sopra.team08.config.character.Character(x)).collect(Collectors.toList()).toArray();
            send(getPlayer1(), new GameStructure("PlayerOne",
                    preGameManager.getPlayer1().getName(),
                    preGameManager.getPlayer2().getName(),
                    p1Characters,
                    p2Characters,
                    partieConfig, scenarioConfig));
            send(getPlayer2(), new GameStructure("PlayerTwo",
                    preGameManager.getPlayer1().getName(),
                    preGameManager.getPlayer2().getName(),
                    p1Characters,
                    p2Characters,
                    partieConfig, scenarioConfig));

            // get character from players
            playerCharacters.addAll(preGameManager.getP1Picked());
            playerCharacters.addAll(preGameManager.getP2Picked());
            if (playerCharacters.size() > 12)
                throw new IllegalStateException("To much player received in game logic");
            else if (playerCharacters.size() < 12)
                throw new IllegalStateException("To few player received in game logic");
            //add playerCharacters to GameBoard
            for (var character:playerCharacters) {
                var position = gameBoard.getRandomFreePosition();
                character.setCoordinates(position);
                gameBoard.setEntityAt(character,position);
            }
            setLogicState(LogicState.RUNNING);
            tieBreak.reset();
            gameTimer.startGameTimer();
            handleRoundSetup();
        }
    }

    @Override
    public boolean handle(Player player, MMRequest req) {
        try {
            LOGGER.debug(req.toJsonRequest());
            switch (req.getRequestType()) {
                case MELEE_ATTACK:
                    MeleeAttackRequest meleeAttackRequest = (MeleeAttackRequest) req;
                    checkInGame();
                    checkPaused();
                    checkRequest(meleeAttackRequest, player);
                    handleRequest(meleeAttackRequest);
                    sendGamestate();
                    //detectTurnOver();
                    return true;
                case RANGED_ATTACK:
                    RangedAttackRequest rangedAttackRequest = (RangedAttackRequest) req;
                    checkInGame();
                    checkPaused();
                    checkRequest(rangedAttackRequest, player);
                    handleRequest(rangedAttackRequest);
                    sendGamestate();
                    //detectTurnOver();
                    return true;
                case MOVE:
                    MoveRequest moveRequest = (MoveRequest) req;
                    checkInGame();
                    checkPaused();
                    checkRequest(moveRequest, player);
                    handleRequest(moveRequest);
                    sendGamestate();
                    //detectTurnOver();
                    return true;
                case USE_INFINITY_STONE:
                    UseInfinityStoneRequest useInfinityStoneRequest = (UseInfinityStoneRequest) req;
                    checkInGame();
                    checkPaused();
                    checkRequest(useInfinityStoneRequest, player);
                    handleRequest(useInfinityStoneRequest);
                    sendGamestate();
                    //detectTurnOver();
                    return true;
                case EXCHANGE_INFINITY_STONE:
                    ExchangeInfinityStoneRequest exchangeInfinityStoneRequest = (ExchangeInfinityStoneRequest) req;
                    checkInGame();
                    checkPaused();
                    checkRequest(exchangeInfinityStoneRequest, player);
                    handleRequest(exchangeInfinityStoneRequest);
                    sendGamestate();
                    //detectTurnOver();
                    return true;
                case PAUSE_START:
                    PauseStartRequest pauseStartRequest = (PauseStartRequest) req;
                    checkInGame();
                    checkRequest(pauseStartRequest, player);
                    handleRequest(pauseStartRequest);
                    sendGamestate();
                    return true;
                case PAUSE_STOP:
                    PauseStopRequest pauseStopRequest = (PauseStopRequest) req;
                    checkInGame();
                    checkRequest(pauseStopRequest, player);
                    handleRequest(pauseStopRequest);
                    sendGamestate();
                    return true;
                case END_ROUND:
                    EndRoundRequest endRoundRequest = (EndRoundRequest) req;
                    checkInGame();
                    checkPaused();
                    checkRequest(endRoundRequest, player);
                    handleRequest(endRoundRequest);
                    return true;
                case REQ:
                    checkInGame();
                    sendGamestate(player);
                    return true;
                case CHARACTER_SELECTION:
                    final CharacterSelection characterSelection = (CharacterSelection) req;
                    handleRequest(characterSelection, player);
                    return true;
                case RECONNECT:
                case HELLO_SERVER:
                case PLAYER_READY:
                case DISCONNECT:
                case ERROR:
                    throw new IllegalArgumentException("isn't valid for handle()");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARN, String.format("%s: %s", req.getClass().getSimpleName(), e.getMessage()));
        } catch (IllegalStateException e) {
            LOGGER.log(Level.ERROR, String.format("%s: %s", req.getClass().getSimpleName(), e.getMessage()));
        }
        return false;
    }

    public Tuple<Integer, Integer> getBoardDimensions() {
        return gameBoard.getDimensions();
    }

    public List<Character> getPlayerCharacters() {
        return playerCharacters;
    }

    @Override
    public ScenarioConfig getScenarioConfig() {
        return scenarioConfig;
    }

    @Override
    public CharacterConfig getCharacterConfig() {
        return characterConfig;
    }

    @Override
    public PartieConfig getPartieConfig() {
        return partieConfig;
    }

    /**
     * Registers a Player
     *
     * @param player The player to be added to the game
     * @return true if it was possible to register the player (not a spectator)
     */
    @Override
    public boolean registerPlayer(@NotNull Player player) {
        if (player.getRole().equals(Role.SPECTATOR)) {
            send(player, new GeneralAssignment(GAME_ID));
            return true;
        }
        // new registration
        final String uniqueID = player.getUniqueID();
        if (player1 == null) {
            player1 = player;
            player1left = false;
            LOGGER.debug(String.format("%s registered as player 1", player.getName()));
        } else if (player2 == null) {
            player2 = player;
            player2left = false;
            LOGGER.debug(String.format("%s registered as player 2", player.getName()));
            Replay.getInstance().setNameP1(player1.getName());
            Replay.getInstance().setNameP2(player2.getName());
            startGame();

        }
        // reregistration after unregister
        else if (player1left && player1.getUniqueID().equals(uniqueID)) {
            // change state
            if (logicState.equals(LogicState.WAIT_FOR_RECONNECT))
                setLogicState(LogicState.RUNNING);
            else if (logicState.equals(LogicState.PAUSED_WAIT))
                setLogicState(LogicState.PAUSED);

            // general assignment as in 6.2.6
            send(player, new GeneralAssignment(GAME_ID));
            send(getPlayer1(), new GameStructure("PlayerOne",
                    preGameManager.getPlayer1().getName(),
                    preGameManager.getPlayer2().getName(),
                    preGameManager.getP1Picked().toArray(),
                    preGameManager.getP2Picked().toArray(),
                    partieConfig, scenarioConfig));
            player1 = player;
            LOGGER.debug(String.format("%s reregistered as player 1", player.getName()));
        } else if (player2left && player2.getUniqueID().equals(uniqueID)) {
            if (logicState.equals(LogicState.WAIT_FOR_RECONNECT))
                setLogicState(LogicState.RUNNING);
            else if (logicState.equals(LogicState.PAUSED_WAIT))
                setLogicState(LogicState.PAUSED);

            // general assignment as in 6.2.6
            send(player, new GeneralAssignment(GAME_ID));
            send(getPlayer2(), new GameStructure("PlayerTwo",
                    preGameManager.getPlayer1().getName(),
                    preGameManager.getPlayer2().getName(),
                    preGameManager.getP1Picked().toArray(),
                    preGameManager.getP2Picked().toArray(),
                    partieConfig,
                    scenarioConfig));
            player2 = player;
            LOGGER.debug(String.format("%s reregistered as player 2", player.getName()));
        } else {
            LOGGER.warn(String.format("There are already 2 players, but %s tried to register", player.getName()));
            return false;
        }
        return true;
    }

    /**
     * Unregisters a Player so he can later rejoin/reregister
     *
     * @param player the player to unregister
     * @return true if it was possible to unregister the player
     */
    @Override
    public boolean unregisterPlayer(@NotNull Player player) {
        if (player.getRole().equals(Role.SPECTATOR)) return false;
        final String uniqueID = player.getUniqueID();
        if (isGameRunning()) {
            if (player1 != null && player1.getUniqueID().equals(uniqueID)) {
                // change state
                if (logicState.equals(LogicState.RUNNING))
                    setLogicState(LogicState.WAIT_FOR_RECONNECT);
                else if (logicState.equals(LogicState.PAUSED))
                    setLogicState(LogicState.PAUSED_WAIT);
                player1left = true;
                return true;
            } else if (player2 != null && player2.getUniqueID().equals(uniqueID)) {
                // change state
                if (logicState.equals(LogicState.RUNNING))
                    setLogicState(LogicState.WAIT_FOR_RECONNECT);
                else if (logicState.equals(LogicState.PAUSED))
                    setLogicState(LogicState.PAUSED_WAIT);
                player2left = true;
                return true;
            }
        }
        // prepare for rejoin while not running
        else {
            if (logicState.equals(LogicState.STARTED) && (player1.getUniqueID().equals(player.getUniqueID()) || player2.getUniqueID().equals(player.getUniqueID()))) {
                LOGGER.fatal("player left during STARTED -> shutdown server");
                shutdown("player left during STARTED", -1);
            }

            if (player1 != null && player1.getUniqueID().equals(uniqueID)) {
                player1 = null;
                player1left = true;
                return true;
            } else if (player2 != null && player2.getUniqueID().equals(uniqueID)) {
                player2 = null;
                player2left = true;
                return true;
            }
        }

        return false;
    }

    /**
     * Running ist specified in {@link LogicState}
     *
     * @return true if the game is running
     */
    @Override
    public boolean isGameRunning() {
        return !logicState.equals(LogicState.INIT) && !logicState.equals(LogicState.STARTED);
    }

    /**
     * A short form for {@link Board#getEntityAt(Tuple)}
     *
     * @param pos the position of the entity on the game board
     * @return the specified entity
     */
    public Entity getEntityAt(Tuple<Integer, Integer> pos) {
        return gameBoard.getEntityAt(pos);
    }

}
