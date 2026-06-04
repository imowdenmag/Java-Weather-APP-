import weatherapp.ui.WeatherAppFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;/**
 /* Entry point for the Weather Information App.

 *
 * @author Owden Magnusen
 */
public class Main {
    private static final String API_KEY = "eab8f8c318cf6970a932ae1537663428";

    /**
     * Application entry point. Applies the system look-and-feel and then
     * launches the main window on the Swing Event Dispatch Thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Apply system look-and-feel for native widget rendering where possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to the default cross-platform look-and-feel silently
        }

        SwingUtilities.invokeLater(() -> {
            WeatherAppFrame frame = new WeatherAppFrame(API_KEY);
            frame.setVisible(true);
        });
    }
}
