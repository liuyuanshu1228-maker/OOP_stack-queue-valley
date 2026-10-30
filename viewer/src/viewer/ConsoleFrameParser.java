package viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Watches the lines printed to the console and recognises a map drawing
 * among them.
 * <p>
 * The game's {@code GameMap.draw(Display)} prints one row of the map per
 * console line, with no other text mixed in between those rows. So a map
 * frame is just a run of consecutive lines that all share one of the game's
 * known map widths, with the run being exactly that map's known height. Any
 * other line (a narrative message, a menu entry, a prompt) does not match
 * one of those exact widths/heights and is simply ignored by this parser --
 * it is still printed to the real console unchanged.
 */
public final class ConsoleFrameParser {

    /** One map's fixed size, by its known width/height in the console grid. */
    private record MapSize(int width, int height) {
    }

    /** The map sizes this parser knows how to recognise. */
    private static final List<MapSize> KNOWN_MAP_SIZES = List.of(
            new MapSize(10, 7),  // Inherited Farm
            new MapSize(12, 8)   // The Mines
    );

    private final Consumer<MapFrame> onFrame;
    private final List<String> candidateLines = new ArrayList<>();

    /**
     * Creates a parser that calls back with each map frame it recognises.
     *
     * @param onFrame called with each recognised MapFrame, on the thread
     *                that printed the console line
     */
    public ConsoleFrameParser(Consumer<MapFrame> onFrame) {
        this.onFrame = onFrame;
    }

    /**
     * Feeds one printed console line into the parser.
     *
     * @param line the line, without its trailing newline
     */
    public void onLine(String line) {
        if (line.isEmpty()) {
            candidateLines.clear();
            return;
        }

        if (!sameWidthAsRunSoFar(line)) {
            candidateLines.clear();
        }
        candidateLines.add(line);
        tryEmitFrame();
    }

    private boolean sameWidthAsRunSoFar(String line) {
        return candidateLines.isEmpty() || candidateLines.get(0).length() == line.length();
    }

    private void tryEmitFrame() {
        int width = candidateLines.get(0).length();
        for (MapSize size : KNOWN_MAP_SIZES) {
            if (size.width() == width && candidateLines.size() == size.height()) {
                onFrame.accept(buildFrame(size));
                candidateLines.clear();
                return;
            }
        }
    }

    private MapFrame buildFrame(MapSize size) {
        char[][] grid = new char[size.height()][size.width()];
        for (int row = 0; row < size.height(); row++) {
            candidateLines.get(row).getChars(0, size.width(), grid[row], 0);
        }
        return new MapFrame(grid, size.width(), size.height());
    }
}
