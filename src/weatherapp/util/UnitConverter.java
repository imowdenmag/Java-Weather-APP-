package weatherapp.util;

/**
 * Provides static conversion methods for temperature and wind speed units.
 *
 * <p>All source values are expected to already be in the base metric unit
 * (Celsius for temperature, km/h for wind speed). Results are rounded to
 * one decimal place for display purposes.</p>
 *
 * @author Owden Magnusen
 */
public final class UnitConverter {

    /** Constant used to shift Celsius to Kelvin (not directly exposed but useful internally). */
    private static final double KELVIN_OFFSET = 273.15;

    // Prevent instantiation of this utility class
    private UnitConverter() {}

    // =========================================================================
    // Temperature conversions
    // =========================================================================

    /**
     * Converts Celsius to Fahrenheit.
     *
     * @param celsius temperature in degrees Celsius
     * @return temperature in degrees Fahrenheit
     */
    public static double celsiusToFahrenheit(double celsius) {
        return celsius * 9.0 / 5.0 + 32.0;
    }

    /**
     * Converts Fahrenheit back to Celsius.
     *
     * @param fahrenheit temperature in degrees Fahrenheit
     * @return temperature in degrees Celsius
     */
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }

    /**
     * Converts a Celsius value to the requested unit.
     *
     * @param celsius  temperature stored in Celsius
     * @param unit     target unit: "C" or "F"
     * @return converted temperature, rounded to one decimal
     */
    public static double convertTemperature(double celsius, String unit) {
        if ("F".equalsIgnoreCase(unit)) {
            return Math.round(celsiusToFahrenheit(celsius) * 10.0) / 10.0;
        }
        return Math.round(celsius * 10.0) / 10.0;
    }

    // =========================================================================
    // Wind speed conversions
    // =========================================================================

    /**
     * Converts km/h to miles per hour.
     *
     * @param kmh wind speed in kilometres per hour
     * @return wind speed in miles per hour
     */
    public static double kmhToMph(double kmh) {
        return kmh * 0.621371;
    }

    /**
     * Converts km/h to metres per second.
     *
     * @param kmh wind speed in kilometres per hour
     * @return wind speed in metres per second
     */
    public static double kmhToMs(double kmh) {
        return kmh / 3.6;
    }

    /**
     * Converts a km/h value to the requested unit.
     *
     * @param kmh  wind speed stored in km/h
     * @param unit target unit: "kmh", "mph", or "ms"
     * @return converted wind speed, rounded to one decimal
     */
    public static double convertWindSpeed(double kmh, String unit) {
        double result;
        switch (unit.toLowerCase()) {
            case "mph":
                result = kmhToMph(kmh);
                break;
            case "ms":
                result = kmhToMs(kmh);
                break;
            default:
                result = kmh;
        }
        return Math.round(result * 10.0) / 10.0;
    }

    /**
     * Returns the display label for a wind speed unit code.
     *
     * @param unit unit code: "kmh", "mph", or "ms"
     * @return human-readable label such as "km/h"
     */
    public static String windUnitLabel(String unit) {
        switch (unit.toLowerCase()) {
            case "mph": return "mph";
            case "ms":  return "m/s";
            default:    return "km/h";
        }
    }

    /**
     * Returns the degree symbol string for a temperature unit code.
     *
     * @param unit "C" or "F"
     * @return "°C" or "°F"
     */
    public static String tempUnitLabel(String unit) {
        return "F".equalsIgnoreCase(unit) ? "°F" : "°C";
    }
}
