package de.uulm.sopra.team08.event

import com.google.gson.JsonObject
import de.uulm.sopra.team08.EntityID
import de.uulm.sopra.team08.util.Tuple
import java.util.*

/**
 * Teleported INGAME Event, sent when a character steps on a portal field.
 */
class TeleportedEvent(
        val teleportedEntity: Tuple<EntityID, Int>,
        val originField: Tuple<Int, Int>,
        val targetField: Tuple<Int, Int>,
        val originPortal: Tuple<EntityID, Int>,
        val targetPortal: Tuple<EntityID, Int>,
) : MMIngameEvent(MMEvent.EventType.TELEPORTED) {

    /**
     * Convenience method for transforming a [TeleportedEvent] into a JSON String
     *
     * @return [TeleportedEvent] as JSON String
     */
    override fun toJsonEvent(): String {
        // create JSON object
        val teleportedEvent = JsonObject()

        // add JSON properties
        teleportedEvent.addProperty("eventType", "TakenDamageEvent")
        teleportedEvent.add("teleportedEntity", gson.toJsonTree(teleportedEntity))
        teleportedEvent.add("originField", Tuple.toJsonArray(originField))
        teleportedEvent.add("targetField", Tuple.toJsonArray(targetField))
        teleportedEvent.add("originPortal", gson.toJsonTree(originPortal))
        teleportedEvent.add("targetPortal", gson.toJsonTree(targetPortal))

        //return JSON as String
        return gson.toJson(teleportedEvent)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TeleportedEvent

        if (teleportedEntity != that.teleportedEntity) return false
        if (originField != that.originField) return false
        if (targetField != that.targetField) return false
        if (originPortal != that.originPortal) return false
        return targetPortal == that.targetPortal
    }

    override fun hashCode(): Int {
        var result: Int = teleportedEntity.hashCode()
        result = 31 * result + originField.hashCode()
        result = 31 * result + targetField.hashCode()
        result = 31 * result + originPortal.hashCode()
        result = 31 * result + targetPortal.hashCode()
        return result
    }

    companion object {

        /**
         * Convenience method to get a [TeleportedEvent] from a [JsonObject]
         *
         * @param json [JsonObject] to be transformed
         * @return the parsed [TeleportedEvent]
         * @throws IllegalArgumentException if [TeleportedEvent] could not be parsed from the [JsonObject]
         */
        fun fromJson(json: JsonObject): TeleportedEvent {
            return try {

                // get targetEntity Object and create Tuple
                val teleportedEntity = json.getAsJsonObject("teleportedEntity")
                var id = EntityID.valueOf(teleportedEntity["entityID"].asString.uppercase(Locale.getDefault()))
                val teleportedEntityTuple = Tuple(id, teleportedEntity["ID"].asInt)

                // get targetLocation Array and create Tuple
                val originField = json.getAsJsonArray("originField")
                val originFieldTuple = Tuple(originField[0].asInt, originField[1].asInt)

                // get targetLocation Array and create Tuple
                val targetField = json.getAsJsonArray("targetField")
                val targetFieldTuple = Tuple(targetField[0].asInt, targetField[1].asInt)

                // get targetEntity Object and create Tuple
                val originPortal = json.getAsJsonObject("teleportedEntity")
                id = EntityID.valueOf(teleportedEntity["entityID"].asString.uppercase(Locale.getDefault()))
                val originPortalTuple = Tuple(id, teleportedEntity["ID"].asInt)

                // get targetEntity Object and create Tuple
                val targetPortal = json.getAsJsonObject("targetPortal")
                id = EntityID.valueOf(targetPortal["entityID"].asString.uppercase(Locale.getDefault()))
                val targetPortalTuple = Tuple(id, teleportedEntity["ID"].asInt)

                // create and return new event
                TeleportedEvent(
                        teleportedEntity = teleportedEntityTuple,
                        originField = originFieldTuple,
                        targetField = targetFieldTuple,
                        originPortal = originPortalTuple,
                        targetPortal = targetPortalTuple,
                )
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Could not parse from JsonObject. Caused by:$e")
            }
        }
    }
}