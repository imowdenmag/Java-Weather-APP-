package weatherapp.util;

/**
 * Maps OpenWeatherMap condition category strings to Unicode weather symbols
 * used throughout the GUI in lieu of image-based icon files.
 *
 * <p>The Unicode symbols are rendered via the system emoji font where available,
 * falling back gracefully to plain text labels on older JREs.</p>
 *
 * @author Owden Magnusen
 */
public final class WeatherIconUtil {

    private WeatherIconUtil() {}

    /**
     * Returns the Unicode emoji symbol that best represents the given
     * OpenWeatherMap condition main category string.
     *
     * <p>Condition categories sourced from:
     * https://openweathermap.org/weather-conditions</p>
     *
     * @param conditionCode the "main" field from the OWM weather array
     *                      (e.g. "Clear", "Clouds", "Rain")
     * @return a single Unicode string representing the condition
     */
    public static String getIcon(String conditionCode) {
        if (conditionCode == null) return "\u2753"; // question mark

        switch (conditionCode.toLowerCase()) {
            case "clear":
                return "\u2600\uFE0F";       // sun
            case "clouds":
                return "\u2601\uFE0F";       // cloud
            case "rain":
            case "drizzle":
                return "\uD83C\uDF27\uFE0F"; // cloud with rain
            case "thunderstorm":
                return "\u26C8\uFE0F";       // thunderstorm
            case "snow":
                return "\u2744\uFE0F";       // snowflake
            case "mist":
            case "smoke":
            case "haze":
            case "dust":
            case "fog":
            case "sand":
            case "ash":
            case "squall":
                return "\uD83C\uDF2B\uFE0F"; // fog
            case "tornado":
                return "\uD83C\uDF2A\uFE0F"; // tornado
            default:
                return "\uD83C\uDF21\uFE0F"; // thermometer fallback
        }
    }

    /**
     * Returns a descriptive label for the condition, suitable for aria-like
     * tooltip text on icon components.
     *
     * @param conditionCode the OWM main condition category
     * @return plain-text description of the weather condition
     */
    public static String getConditionLabel(String conditionCode) {
        if (conditionCode == null) return "Unknown";

        switch (conditionCode.toLowerCase()) {
            case "clear":        return "Clear Sky";
            case "clouds":       return "Cloudy";
            case "rain":         return "Rainy";
            case "drizzle":      return "Drizzle";
            case "thunderstorm": return "Thunderstorm";
            case "snow":         return "Snowy";
            case "mist":         return "Misty";
            case "fog":          return "Foggy";
            case "haze":         return "Hazy";
            case "tornado":      return "Tornado";
            default:             return conditionCode;
        }
    }

    /**
     * Returns the compass direction label for a wind bearing in degrees.
     *
     * @param degrees wind direction in meteorological degrees (0 = North)
     * @return abbreviated compass direction string e.g. "NE", "SW"
     */
    public static String windDirection(double degrees) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        int index = (int) Math.round(degrees / 45.0);
        return directions[index % 8];
    }
}
