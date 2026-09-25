package game;

import edu.monash.fit2099.engine.displays.Display;
import game.displays.FancyMessage;

public class Application {
    public static void main(String[] args) {
        Display display = new Display();
        StackQueueValleyWorld world = new StackQueueValleyWorld(display);
        try{
            for (String line : FancyMessage.GAME_TITLE.split("\n")) {
                new Display().println(line);
                try {
                    Thread.sleep(100);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            world.initialise();
            world.run();
        }
        catch (Exception e) {
            // General exception, to help debugging.
            e.printStackTrace();
        }
    }
}
