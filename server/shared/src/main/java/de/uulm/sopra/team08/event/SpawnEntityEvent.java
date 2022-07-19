package de.uulm.sopra.team08.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.uulm.sopra.team08.config.Config;
import de.uulm.sopra.team08.data.entity.Character;
import de.uulm.sopra.team08.data.entity.*;
import de.uulm.sopra.team08.data.terrain.Rock;
import de.uulm.sopra.team08.util.Tuple;
import org.jetbrains.annotations.NotNull;

/**
 * SpawnEntity INGAME EVENT, sent when a new Entity (Rock, NPC ...) is spawned.
 */
public class SpawnEntityEvent extends MMIngameEvent {

    private final Entity entity;

    /**
     * Creates a new SpawnEntity INGAME Event
     *
     * @param entity the Entity to spawn
     */
    public SpawnEntityEvent(@NotNull Entity entity) {
        super(EventType.SPAWN_ENTITY);
        this.entity = entity;
    }

    /**
     * Convenience method to get a {@link SpawnEntityEvent} from a {@link JsonObject}
     *
     * @param json {@link JsonObject} to be transformed
     * @return the parsed {@link SpawnEntityEvent}
     * @throws IllegalArgumentException if {@link SpawnEntityEvent} could not be parsed from the {@link JsonObject}
     */
    public static SpawnEntityEvent fromJson(JsonObject json, Config config) {
        try {
            JsonObject entity = json.getAsJsonObject("entity");

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

                    return new SpawnEntityEvent(rock);
                }
                case "InfinityStone": {
                    // create and add the InfinityStoneEntity with specified position and id
                    int id = entity.get("ID").getAsInt();
                    InfinityStoneEntity infinityStoneEntity = new InfinityStoneEntity(id);
                    JsonArray jsonArray = entity.getAsJsonArray("position");
                    Tuple<Integer, Integer> pos = new Tuple<>(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt());
                    infinityStoneEntity.setCoordinates(pos);
                    return new SpawnEntityEvent(infinityStoneEntity);
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
                            return new SpawnEntityEvent(goose);
                        case 1:
                            StanLee stanLee = new StanLee();
                            stanLee.setCoordinates(pos);
                            return new SpawnEntityEvent(stanLee);
                        case 2:
                            int mp = entity.get("MP").getAsInt();
                            Thanos thanos = new Thanos(mp);
                            thanos.setCoordinates(pos);
                            return new SpawnEntityEvent(thanos);
                        default:
                            // NPC ID can only be 0,1,2
                            throw new IllegalArgumentException("NPC ID '" + id + "' not allowed!");
                    }
                }
                case "Character": {
                    // give entity data to Character and create one
                    return new SpawnEntityEvent(Character.fromJson(entity, config));
                }
            }
            // undefined entityType
            throw new IllegalArgumentException("Not an accepted entityType: " + entityType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not parse from JsonObject. Caused by:" + e);
        }
    }

    /**
     * Convenience method for transforming a {@link SpawnEntityEvent} into a JSON String
     *
     * @return {@link SpawnEntityEvent} as JSON String
     */
    @Override
    public String toJsonEvent() {
        // create JSON object
        JsonObject spawnEntityEvent = new JsonObject();

        // add JSON properties
        spawnEntityEvent.addProperty("eventType", "SpawnEntityEvent");
        spawnEntityEvent.add("entity", entity.toJsonElement());

        // return JSON as String
        return gson.toJson(spawnEntityEvent);
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpawnEntityEvent that = (SpawnEntityEvent) o;

        return getEntity().equals(that.getEntity());
    }

    @Override
    public int hashCode() {
        return getEntity().hashCode();
    }

}
