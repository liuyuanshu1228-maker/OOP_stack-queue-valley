package viewer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Draws the latest {@link MapFrame} as a grid of coloured tiles.
 * <p>
 * This is a placeholder look (flat colour + the display character), not
 * real pixel art -- it exists so the viewer is useful immediately. Swapping
 * in real sprite images later only means changing how one tile is painted
 * here; nothing else in the viewer needs to change.
 */
public final class GameViewPanel extends JPanel {

    private static final int TILE_SIZE = 32;

    private final TileLegend legend;
    private volatile MapFrame frame;

    public GameViewPanel(TileLegend legend) {
        this.legend = legend;
        setBackground(new Color(30, 30, 30));
    }

    /**
     * Replaces the frame currently shown and repaints.
     *
     * @param frame the new frame to display
     */
    public void showFrame(MapFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(frame.width() * TILE_SIZE, frame.height() * TILE_SIZE));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        MapFrame current = frame;
        if (current == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        for (int row = 0; row < current.height(); row++) {
            for (int col = 0; col < current.width(); col++) {
                drawTile(g2, current.grid()[row][col], col * TILE_SIZE, row * TILE_SIZE);
            }
        }
    }

    private void drawTile(Graphics2D g2, char displayChar, int x, int y) {
        TileLegend.TileStyle style = legend.styleFor(displayChar);

        g2.setColor(style.color());
        g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);

        if (!style.label().isEmpty()) {
            g2.setColor(contrastingTextColor(style.color()));
            g2.drawString(style.label(), x + TILE_SIZE / 2 - 5, y + TILE_SIZE / 2 + 6);
        }
    }

    private Color contrastingTextColor(Color background) {
        double brightness = (0.299 * background.getRed()
                + 0.587 * background.getGreen()
                + 0.114 * background.getBlue()) / 255.0;
        return brightness > 0.6 ? Color.BLACK : Color.WHITE;
    }
}
