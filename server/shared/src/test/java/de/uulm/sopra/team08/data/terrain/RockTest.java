package de.uulm.sopra.team08.data.terrain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RockTest {

    @Test
    void damageTest() {
        final Rock rock = new Rock(1);

        Assertions.assertTrue(rock.damage(1000));

        Assertions.assertEquals(-900, rock.getHp());

        Assertions.assertTrue(rock.isDestroyed());
    }

}
