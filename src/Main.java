import weatherapp.ui.WeatherAppFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Weather Information App.
 *
 * <p>Replace {@code YOUR_API_KEY_HERE} with a valid OpenWeatherMap API key
 * before compiling and running. Free keys are available at:
 * https://openweathermap.org/api</p>
 *
 * <p>To compile from the {@code src/} directory:
 * <pre>
 *   javac -d ../out weatherapp/Main.java \
 *         weatherapp/model/*.java \
 *         weatherapp/service/*.java \
 *         weatherapp/util/*.java \
 *         weatherapp/ui/*.java
 * </pre>
 *
 * To run:
 * <pre>
 *   java -cp ../out weatherapp.Main
 * </pre>
 * </p>
 *
 * @author Owden Magnusen
 */
public class Main {

    /**
     * Your OpenWeatherMap API key.
     * Obtain a free key at: https://openweathermap.org/api
     */
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
