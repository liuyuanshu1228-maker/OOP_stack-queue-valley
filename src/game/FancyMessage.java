package game;

import edu.monash.fit2099.engine.displays.Display;

/**
 * A multi-line message that is printed one line at a time, with a short pause
 * between lines. {@link #gameTitle()} gives the fancy ASCII-art banner printed
 * once when the game starts.
 */
public class FancyMessage {

    /** Pause between the lines of the message, in milliseconds. */
    private static final int LINE_DELAY_MS = 100;

    /** The ASCII-art banner printed, line by line, at game start. */
    private static final String GAME_TITLE = "   ,-,--.  ,--.--------.   ,---.       _,.----.  ,--.-.,-.                \n" +
            " ,-.'-  _\\/==/,  -   , -\\.--.'  \\    .' .' -   \\/==/- |\\  \\               \n" +
            "/==/_ ,_.'\\==\\.-.  - ,-./\\==\\-/\\ \\  /==/  ,  ,-'|==|_ `/_ /,--.--------.  \n" +
            "\\==\\  \\    `--`\\==\\- \\   /==/-|_\\ | |==|-   |  .|==| ,   //==/,  -   , -\\ \n" +
            " \\==\\ -\\        \\==\\_ \\  \\==\\,   - \\|==|_   `-' \\==|-  .| \\==\\.-.  - ,-./ \n" +
            " _\\==\\ ,\\       |==|- |  /==/ -   ,||==|   _  , |==| _ , \\ `--`--------`  \n" +
            "/==/\\/ _ |      |==|, | /==/-  /\\ - \\==\\.       /==/  '\\  |               \n" +
            "\\==\\ - , /      /==/ -/ \\==\\ _.\\=\\.-'`-.`.___.-'\\==\\ /\\=\\.'               \n" +
            " `--`---'       `--`--`  `--`                    `--`                     \n" +
            "    _,.---._                     ,----.                  ,----.           \n" +
            "  ,-.' - ,  `.   .--.-. .-.-. ,-.--` , \\ .--.-. .-.-. ,-.--` , \\          \n" +
            " /==/ ,    -  \\ /==/ -|/=/  ||==|-  _.-`/==/ -|/=/  ||==|-  _.-`          \n" +
            "|==| - .=.  ,  ||==| ,||=| -||==|   `.-.|==| ,||=| -||==|   `.-.          \n" +
            "|==|  : ;=:  - ||==|- | =/  /==/_ ,    /|==|- | =/  /==/_ ,    /          \n" +
            "|==|,  '='  ,  ||==|,  \\/ - |==|    .-' |==|,  \\/ - |==|    .-'           \n" +
            " \\==\\ _   -    ;|==|-   ,   /==|_  ,`-._|==|-   ,   /==|_  ,`-._          \n" +
            "  '.='.  ,  ; -\\/==/ , _  .'/==/ ,     //==/ , _  .'/==/ ,     /          \n" +
            "    `--`--'' `--`--`..---'  `--`-----`` `--`..---'  `--`-----``           \n" +
            "        ,-.-.  ,---.                              ,----.                  \n" +
            " ,--.-./=/ ,/.--.'  \\       _.-.      _.-.     ,-.--` , \\ ,--.-.  .-,--.  \n" +
            "/==/, ||=| -|\\==\\-/\\ \\    .-,.'|    .-,.'|    |==|-  _.-`/==/- / /=/_ /   \n" +
            "\\==\\,  \\ / ,|/==/-|_\\ |  |==|, |   |==|, |    |==|   `.-.\\==\\, \\/=/. /    \n" +
            " \\==\\ - ' - /\\==\\,   - \\ |==|- |   |==|- |   /==/_ ,    / \\==\\  \\/ -/     \n" +
            "  \\==\\ ,   | /==/ -   ,| |==|, |   |==|, |   |==|    .-'   |==|  ,_/      \n" +
            "  |==| -  ,//==/-  /\\ - \\|==|- `-._|==|- `-._|==|_  ,`-._  \\==\\-, /       \n" +
            "  \\==\\  _ / \\==\\ _.\\=\\.-'/==/ - , ,/==/ - , ,/==/ ,     /  /==/._/        \n" +
            "   `--`--'   `--`        `--`-----'`--`-----'`--`-----``   `--`-`         ";

    /** The lines of this message, separated by newline characters. */
    private final String text;

    /**
     * Constructor.
     *
     * @param text the message to print, with lines separated by newline characters
     */
    public FancyMessage(String text) {
        this.text = text;
    }

    /**
     * Creates the game's title banner.
     *
     * @return the ASCII-art title banner
     */
    public static FancyMessage gameTitle() {
        return new FancyMessage(GAME_TITLE);
    }

    /**
     * Prints this message one line at a time, pausing briefly between lines.
     *
     * @param display the I/O object to which the message is printed
     */
    public void printTo(Display display) {
        for (String line : text.split("\n")) {
            display.println(line);
            try {
                Thread.sleep(LINE_DELAY_MS);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
