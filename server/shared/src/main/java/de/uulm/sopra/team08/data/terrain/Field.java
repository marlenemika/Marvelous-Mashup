package de.uulm.sopra.team08.data.terrain;

import de.uulm.sopra.team08.data.entity.Entity;

/**
 * This class is used to represent a single field in the Marvelous-Mashup game.
 * An entity can be placed on this field. Managing the entities needs to be done
 * by the user of this class (i.e. swapping positions).
 *
 * @author Jan-Philipp
 */
class Field {

    private Entity entity;

    /**
     * Parameterized constructor.
     *
     * @param entity the entity that is placed on this field
     */
    Field(Entity entity) {
        this.entity = entity;
    }

    Field() {

    }

    Entity getEntity() {
        return entity;
    }

    void setEntity(Entity entity) {
        this.entity = entity;
    }

}
