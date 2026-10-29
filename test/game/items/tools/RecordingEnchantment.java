package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enchantments.Enchantment;

import java.util.List;

/**
 * A fake Enchantment used only by tests. Instead of doing anything to the
 * game world, it records how many times it fired and what it was given, so a
 * test can check that the real caller (an {@link EnchantmentSlot}) passes
 * through the right arguments without having to depend on any production
 * Enchantment's own effect.
 */
class RecordingEnchantment implements Enchantment {

    /** How many times {@link #applyTo} has been called. */
    private int timesFired = 0;

    /** The tiles passed to the most recent {@link #applyTo} call. */
    private List<Location> lastAffectedTiles;

    /** The activator passed to the most recent {@link #applyTo} call. */
    private Actor lastActivator;

    /** The origin passed to the most recent {@link #applyTo} call. */
    private Location lastOrigin;

    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator, Location origin) {
        timesFired++;
        lastAffectedTiles = affectedTiles;
        lastActivator = activator;
        lastOrigin = origin;
        return "fired";
    }

    /**
     * @return how many times this Enchantment has fired
     */
    int timesFired() {
        return timesFired;
    }

    /**
     * @return the tiles passed to the most recent fire
     */
    List<Location> lastAffectedTiles() {
        return lastAffectedTiles;
    }

    /**
     * @return the activator passed to the most recent fire
     */
    Actor lastActivator() {
        return lastActivator;
    }

    /**
     * @return the origin passed to the most recent fire
     */
    Location lastOrigin() {
        return lastOrigin;
    }
}
