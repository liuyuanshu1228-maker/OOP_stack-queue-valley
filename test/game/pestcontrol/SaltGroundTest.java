package game.pestcontrol;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.pests.SlimeSnail;
import game.positions.area.AreaFactory;
import game.positions.area.PlantableTile;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests salted ground on its own.
 */
class SaltGroundTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private GameMap map;
    private Location tile;

    @BeforeEach
    void setUp() {
        map = TestFarm.map(",.");
        tile = map.at(0, 0);
    }

    @Test
    void keepsPestsOutButLetsOthersIn() {
        SaltGround salt = new SaltGround(new Soil());

        assertFalse(salt.canActorEnter(new SlimeSnail(random, areaFactory)));
        assertTrue(salt.canActorEnter(new Ladybug(random)));
    }

    @Test
    void cannotBePlantedWhileTheSaltLasts() {
        tile.setGround(new SaltGround(tile.getGround()));

        assertFalse(new PlantableTile().isMetBy(tile));
    }

    @Test
    void turnsBackIntoTheGroundItCoveredAfterEightTurns() {
        Soil soil = new Soil();
        tile.setGround(new SaltGround(soil));

        TestFarm.tick(tile, 7);
        assertInstanceOf(SaltGround.class, tile.getGround(), "still salted after seven turns");
        TestFarm.tick(tile, 1);

        assertSame(soil, tile.getGround());
    }

    @Test
    void saltedDirtTurnsBackIntoDirt() {
        Location dirtTile = map.at(1, 0);
        dirtTile.setGround(new SaltGround(dirtTile.getGround()));

        TestFarm.tick(dirtTile, 8);

        assertInstanceOf(Dirt.class, dirtTile.getGround());
    }
}
