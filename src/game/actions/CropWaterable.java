package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for a {@link game.crops.Crop} that can be watered by an Actor
 * carrying a watering tool. Not every crop supports this (e.g. Tomato does
 * not); implementing classes define the crop-specific effect of being
 * watered.
 * <p>
 * This is unrelated to {@link game.positions.Waterable}, which is a ground-level capability
 * for turning Dirt into a Puddle.
 */
public interface CropWaterable {

    /**
     * Returns the stamina cost required to water this crop.
     *
     * @return the stamina cost
     */
    int getWateringStaminaCost();

    /**
     * Records that this crop has been watered once, since several crop rules
     * depend on how many times it has been watered.
     */
    void recordWatering();

    /**
     * Applies the effect of watering this crop to the given Actor and Location.
     * Assumes stamina has already been verified as sufficient by the caller.
     *
     * @param actor    the Actor watering the crop
     * @param location the Location of the crop being watered
     * @return a description of the outcome, to be displayed to the player
     */
    String water(Actor actor, Location location);
}
