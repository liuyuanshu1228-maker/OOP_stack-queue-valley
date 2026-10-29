package game;

import edu.monash.fit2099.engine.displays.Display;

/**
 * Entry point of the game: prints the title banner, then builds and runs the
 * StackQueue Valley world.
 */
public class Application {

    /**
     * Starts the game.
     *
     * @param args the command line arguments (unused)
     */
    public static void main(String[] args) {
        Display display = new Display();
        StackQueueValleyWorld world = new StackQueueValleyWorld(display);
        try {
            new FancyMessage().printTo(display);
            world.initialise();
            world.run();
        } catch (Exception e) {
            // General exception, to help debugging.
            e.printStackTrace();
        }
    }
}
