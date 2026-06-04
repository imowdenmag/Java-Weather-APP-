package weatherapp.service;

/**
 * Checked exception thrown by {@link WeatherService} when an API call
 * fails for any reason, including network errors, invalid input, or
 * unexpected response codes.
 *
 * <p>The message is always user-friendly and safe to display directly
 * in the GUI error area.</p>
 *
 * @author Owden Magnusen
 */
public class WeatherServiceException extends Exception {

    /**
     * Constructs a WeatherServiceException with a descriptive message.
     *
     * @param message user-friendly error description
     */
    public WeatherServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a WeatherServiceException wrapping an underlying cause.
     *
     * @param message user-friendly error description
     * @param cause   the underlying exception
     */
    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
