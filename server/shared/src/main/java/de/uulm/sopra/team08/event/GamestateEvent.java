package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Gamestate INGAME Event, sent at the end of every message to clients.<br>
 * Informs the client of the current state.
 */
public class GamestateEvent extends MMIngameEvent {

    private final List<Entity> entities;
    private final Tuple<Integer, Integer> mapSize;
    private final List<Tuple<EntityID, Integer>> turnOrder;
    private final Tuple<EntityID, Integer> activeCharacter;
    // the cooldown order is Space Mind Reality Power Time Soul
    private final List<Integer> stoneCooldowns;
    private final boolean winCondition;

    /**
     * Creates a new Gamestate INGAME Event
     *
     * @param entities        the entities as List
     * @param mapSize         the mapSize as Tuple
     * @param turnOrder       the turnOrder as List
     * @param activeCharacter the activeCharacter as Tuple of EntityID and ID
     * @param stoneCooldowns  the stoneCooldowns as List (the cooldown order is Space, Mind, Reality, Power, Time, Soul)
     * @param winCondition    a boolean whether someone
     * @throws IllegalArgumentException if the length of stoneCooldowns isn't 6
     */
    public GamestateEvent(@NotNull List<Entity> entities, @NotNull Tuple<Integer, Integer> mapSize, @NotNull List<Tuple<EntityID, Integer>> turnOrder, @NotNull Tuple<EntityID, @NotNull Integer> activeCharacter, @NotNull List<Integer> stoneCooldowns, boolean winCondition) throws IllegalArgumentException {
        super(EventType.GAMESTATE);
        if (stoneCooldowns.size() != 6)
            throw new IllegalArgumentException("stoneCooldowns must be exactly 6 elements long");
        this.entities = entities;
        this.mapSize = mapSize;
        this.turnOrder = turnOrder;
        this.activeCharacter = activeCharacter;
        this.stoneCooldowns = stoneCooldowns;
        this.winCondition = winCondition;
    }

    //same with array instead of List

    /**
     * Creates a new Gamestate INGAME Event
     *
     * @param entities        the entities as array
     * @param mapSize         the mapSize as Tuple
     * @param turnOrder       the turnOrder as array
     * @param activeCharacter the activeCharacter as Tuple of EntityID and ID
     * @param stoneCooldowns  the stoneCooldowns as array (the cooldown order is Space, Mind, Reality, Power, Time, Soul)
     * @param winCondition    a boolean whether someone
     */
    public GamestateEvent(@NotNull Entity[] entities, @NotNull Tuple<Integer, Integer> mapSize, @NotNull Tuple<EntityID, Integer>[] turnOrder, @NotNull Tuple<EntityID, Integer> activeCharacter, @NotNull Integer[] stoneCooldowns, boolean winCondition) {
        super(EventType.GAMESTATE);
        if (stoneCooldowns.length != 6)
            throw new IllegalArgumentException("stoneCooldowns must be exactly 6 elements long");
        this.entities = Arrays.asList(entities);
        this.mapSize = mapSize;
        this.turnOrder = Arrays.asList(turnOrder);
        this.activeCharacter = activeCharacter;
        this.stoneCooldowns = Arrays.asList(stoneCooldowns);
        this.winCondition = winCondition;
    }

    /**
     * Convenience method to get a {@link GamestateEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link GamestateEvent}
     * @throws IllegalArgumentException if {@link GamestateEvent} could not be parsed from the {@link JsonObject}
     */
    public static GamestateEvent fromJson(JsonObject json, Config config) {
        try {
            // get all entities as Array/List
            JsonArray entitiesArray = json.getAsJsonArray("entities");
            List<Entity> entities = new ArrayList<>();
            for (int i = 0; i < entitiesArray.size(); i++) {
                JsonObject entity = entitiesArray.get(i).getAsJsonObject();
                String entityType = entity.get("entityType").getAsString();
                // decide what to do based on entityType
                switch (entityType) {
                    case "Rock": {
                        // create and add a rock with specified position and id
                        int id = entity.get("ID").getAsInt();

                        JsonArray jsonArray = entity.getAsJsonArray("position");
                        Tuple<Integer, Integer> pos = new Tuple<>(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt());

                        Rock rock = new Rock(id);
                        rock.setCoordinates(pos);

                        entities.add(rock);
                        break;
                    }
                    case "InfinityStone": {
                        // create and add the InfinityStoneEntity with specified position and id
                        int id = entity.get("ID").getAsInt();
                        InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(id);
                        JsonArray jsonArray = entity.getAsJsonArray("position");
                        Tuple<Integer, Integer> pos = new Tuple<>(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt());
                        infinityStoneEntity.setCoordinates(pos);
                        entities.add(infinityStoneEntity);
                        break;
                    }
                    case "NPC": {
                        // create and add NPC based on id
                        int id = entity.get("ID").getAsInt();
                        JsonArray jsonArray = entity.getAsJsonArray("position");
                        Tuple<Integer, Integer> pos = new Tuple<>(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt());
                        switch (id) {
                            case 0:
                                Goose goose = new Goose();
                                goose.setCoordinates(pos);
                                entities.add(goose);
                                break;
                            case 1:
                                StanLee stanLee = new StanLee();
                                stanLee.setCoordinates(pos);
                                entities.add(stanLee);
                                break;
                            case 2:
                                int mp = entity.get("MP").getAsInt();
                                Thanos thanos = new Thanos(mp);
                                thanos.setCoordinates(pos);
                                entities.add(thanos);
                                break;
                            default:
                                // NPC ID can only be 0,1,2
                                throw new IllegalArgumentException("NPC ID '" + id + "' not allowed!");
                        }
                        break;
                    }
                    case "Character": {
                        // give entity data to Character and create one
                        entities.add(Character.fromJson(entity, config));
                        break;
                    }
                    default:
                        // undefined entityType
                        throw new IllegalArgumentException("Not an accepted entityType: " + entityType);
                }
            }

            // get mapSize
            JsonArray mapSizeArray = json.getAsJsonArray("mapSize");
            Tuple<Integer, Integer> mapSize = new Tuple<>(mapSizeArray.get(0).getAsInt(), mapSizeArray.get(1).getAsInt());

            // get list of characters
            List<Tuple<EntityID, Integer>> turnOrder = new ArrayList<>();
            JsonArray turnOrderArray = json.getAsJsonArray("turnOrder");
            // extract every character
            for (int i = 0; i < turnOrderArray.size(); i++) {
                JsonObject character = turnOrderArray.get(i).getAsJsonObject();
                EntityID entityID = EntityID.valueOf(character.get("entityID").getAsString().toUpperCase());
                Tuple<EntityID, Integer> characterTuple = new Tuple<>(entityID, character.get("ID").getAsInt());
                turnOrder.add(characterTuple);
            }

            // get activeCharacter
            JsonObject activeCharacterObject = json.getAsJsonObject("activeCharacter");
            EntityID entityID = EntityID.valueOf(activeCharacterObject.get("entityID").getAsString().toUpperCase());
            Tuple<EntityID, Integer> activeCharacter = new Tuple<>(entityID, activeCharacterObject.get("ID").getAsInt());

            // get current Cooldowns of all Stones
            List<Integer> stoneCooldowns = new ArrayList<>();
            JsonArray stoneCooldownsArray = json.getAsJsonArray("stoneCooldowns");
            for (int i = 0; i < stoneCooldownsArray.size(); i++) {
                stoneCooldowns.add(stoneCooldownsArray.get(i).getAsInt());
            }

            boolean winCondition = json.get("winCondition").getAsBoolean();

            // create Event
            return new GamestateEvent(entities, mapSize, turnOrder, activeCharacter, stoneCooldowns, winCondition);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link GamestateEvent} into a JSON String
     *
     * @return {@link GamestateEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject gamestateEvent = new JsonObject();

        // add JSON properties
        gamestateEvent.addProperty("eventType", "GamestateEvent");
        JsonArray entities = new JsonArray();
        for (Entity entity : this.entities) {
            entities.add(entity.toJsonElement());
        }
        gamestateEvent.add("entities", entities);
        gamestateEvent.add("mapSize", Tuple.toJsonArray(mapSize));
        JsonArray turnOrder = new JsonArray();
        for (Tuple<EntityID, Integer> t : this.turnOrder) {
            turnOrder.add(gson.toJsonTree(t));
        }
        gamestateEvent.add("turnOrder", turnOrder);
        gamestateEvent.add("activeCharacter", gson.toJsonTree(activeCharacter));
        gamestateEvent.add("stoneCooldowns", gson.toJsonTree(stoneCooldowns));
        gamestateEvent.addProperty("winCondition", winCondition);

        // return JSON as String
        return gson.toJson(gamestateEvent);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Tuple<Integer, Integer> getMapSize() {
        return mapSize;
    }

    public List<Tuple<EntityID, Integer>> getTurnOrder() {
        return turnOrder;
    }

    public Tuple<EntityID, Integer> getActiveCharacter() {
        return activeCharacter;
    }

    public List<Integer> getStoneCooldowns() {
        return stoneCooldowns;
    }

    public boolean isWinCondition() {
        return winCondition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GamestateEvent that = (GamestateEvent) o;
        return isWinCondition() == that.isWinCondition() && getEntities().equals(that.getEntities()) && getMapSize().equals(that.getMapSize()) && getTurnOrder().equals(that.getTurnOrder()) && getActiveCharacter().equals(that.getActiveCharacter()) && getStoneCooldowns().equals(that.getStoneCooldowns());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEntities(), getMapSize(), getTurnOrder(), getActiveCharacter(), getStoneCooldowns(), isWinCondition());
    }

}
