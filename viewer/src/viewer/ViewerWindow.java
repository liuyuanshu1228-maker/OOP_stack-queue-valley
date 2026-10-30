package viewer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

/**
 * The window that shows the mirrored game view. Runs independently of the
 * console: closing it does not stop the game, and it never reads input --
 * all commands still go through the console exactly as before.
 */
public final class ViewerWindow extends JFrame {

    private final GameViewPanel panel;

    public ViewerWindow() {
        super("Stack Queue Valley (viewer)");
        this.panel = new GameViewPanel(new TileLegend());

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        setResizable(false);
    }

    /**
     * Shows a new frame, updating the window on the Swing event thread.
     *
     * @param frame the frame to display
     */
    public void showFrame(MapFrame frame) {
        SwingUtilities.invokeLater(() -> {
            panel.showFrame(frame);
            pack();
        });
    }
}
