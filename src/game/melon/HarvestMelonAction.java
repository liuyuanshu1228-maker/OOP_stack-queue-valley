package game.melon;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.Chance;
import game.HarvestCropAction;

import java.util.ArrayList;
import java.util.List;

/**
 * The action of harvesting a {@link Melon}.
 * <p>
 * Harvesting always removes the crop. There is then a chance it explodes,
 * damaging every actor on or adjacent to its tile; otherwise, a melon is
 * added to the actor's inventory.
 */
class HarvestMelonAction extends HarvestCropAction {

    private static final int STAMINA_COST = 5;
    private static final int EXPLOSION_CHANCE_PERCENT = 50;
    private static final int BASE_EXPLOSION_DAMAGE = 20;

    /**
     * Constructor.
     *
     * @param crop the melon to be harvested
     * @param cropLocation the location of the melon to be harvested
     */
    HarvestMelonAction(Melon crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * @return the stamina cost of harvesting a melon
     */
    @Override
    protected int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * Destroys the melon, then rolls the chance for it to explode. The
     * explosion's damage shrinks by one point for every time the melon has
     * been watered, down to a minimum of no damage at all.
     *
     * @param actor the actor harvesting the melon
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyHarvestEffect(Actor actor, GameMap map) {
        destroyCrop();

        if (Chance.rollPercent(EXPLOSION_CHANCE_PERCENT)) {
            int damage = Math.max(0, BASE_EXPLOSION_DAMAGE - crop.getWateringCount());
            explode(damage);
            return actor + " harvests the " + crop + ". It explodes, dealing " + damage + " damage to everyone nearby!";
        }

        actor.getInventory().add(new MelonItem());
        return actor + " harvests the " + crop + " and picks up a melon.";
    }

    /**
     * Damages every actor on the crop's own tile and on the tiles adjacent
     * to it.
     *
     * @param damage the damage to deal to each actor caught in the blast
     */
    private void explode(int damage) {
        List<Location> blastLocations = new ArrayList<>(cropLocation.getNearbyLocations(1));
        blastLocations.add(cropLocation);

        for (Location location : blastLocations) {
            if (location.containsAnActor()) {
                location.getActor().hurt(damage);
            }
        }
    }
}
