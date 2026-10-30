package viewer;

import game.Application;

/**
 * Local-only entry point for presentation recordings: starts the real game
 * exactly as {@link Application#main(String[])} would, but with its console
 * output mirrored into a graphical window alongside the unchanged console.
 * <p>
 * This class, and the rest of the {@code viewer} package, is the only code
 * involved in the mirroring. It lives entirely outside {@code src/game} and
 * {@code src/edu.monash.fit2099}, and does not modify either -- it only
 * calls the real, unmodified {@link Application#main(String[])} and watches
 * what it prints. All commands are still typed into the console exactly as
 * before; closing this window has no effect on the game.
 */
public final class ViewerMain {

    private ViewerMain() {
    }

    /**
     * Opens the viewer window, then starts the real game.
     *
     * @param args forwarded to {@link Application#main(String[])} (unused by the game)
     * @throws Exception whatever starting the real game might throw
     */
    public static void main(String[] args) throws Exception {
        ViewerWindow window = new ViewerWindow();
        ConsoleFrameParser parser = new ConsoleFrameParser(window::showFrame);
        System.setOut(new MirroringPrintStream(System.out, parser));

        window.setVisible(true);
        Application.main(args);
    }
}
