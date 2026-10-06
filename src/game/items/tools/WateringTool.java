package game.items.tools;

/**
 * Capability interface for Items that can be used to water Ground.
 * Any Item implementing this interface can perform a WaterAction, and it
 * also allows the actor carrying it to water crops.
 */
public interface WateringTool extends HasStaminaCost {
}