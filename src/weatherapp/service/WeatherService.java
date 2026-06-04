package weatherapp.service;

import weatherapp.model.ForecastEntry;
import weatherapp.model.WeatherData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Fetches current weather and forecast data from the OpenWeatherMap REST API.
 *
 * <p>This service targets the following endpoints:
 * <ul>
 *   <li>Current weather: {@code /data/2.5/weather}</li>
 *   <li>5-day / 3-hour forecast: {@code /data/2.5/forecast}</li>
 * </ul>
 *
 * <p>Responses are returned as metric units from the API (Celsius, m/s for wind).
 * Wind speed is immediately converted to km/h on parse so the rest of the app
 * works from a consistent km/h base.
 *
 * <p>JSON parsing is done with a minimal hand-written parser to avoid requiring
 * an external dependency like Gson or Jackson. Students may swap this out for
 * a proper JSON library in production use.</p>
 *
 * @author Owden Magnusen
 */
public class WeatherService {

    private static final String BASE_URL  = "https://api.openweathermap.org/data/2.5";
    private static final int    TIMEOUT_MS = 8000;

    // API key injected via constructor to keep credentials out of source control
    private final String apiKey;

    /**
     * Constructs a WeatherService with the given API key.
     *
     * @param apiKey valid OpenWeatherMap API key
     */
    public WeatherService(String apiKey) {
        this.apiKey = apiKey;
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Fetches current weather data for the specified city name.
     *
     * @param cityName name of the city (may include country code, e.g. "Accra,GH")
     * @return populated {@link WeatherData} object
     * @throws WeatherServiceException if the API call fails or returns an error
     */
    public WeatherData fetchByCity(String cityName) throws WeatherServiceException {
        String encodedCity = urlEncode(cityName.trim());
        String currentUrl  = BASE_URL + "/weather?q=" + encodedCity + "&units=metric&appid=" + apiKey;
        String forecastUrl = BASE_URL + "/forecast?q=" + encodedCity + "&units=metric&cnt=15&appid=" + apiKey;

        String currentJson  = get(currentUrl);
        String forecastJson = get(forecastUrl);

        WeatherData data = parseCurrentWeather(currentJson);
        data.setForecast(parseForecast(forecastJson));
        return data;
    }

    /**
     * Fetches current weather data for the given latitude and longitude.
     *
     * @param lat latitude in decimal degrees
     * @param lon longitude in decimal degrees
     * @return populated {@link WeatherData} object
     * @throws WeatherServiceException if the API call fails or returns an error
     */
    public WeatherData fetchByCoordinates(double lat, double lon) throws WeatherServiceException {
        String currentUrl  = BASE_URL + "/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=" + apiKey;
        String forecastUrl = BASE_URL + "/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&cnt=15&appid=" + apiKey;

        String currentJson  = get(currentUrl);
        String forecastJson = get(forecastUrl);

        WeatherData data = parseCurrentWeather(currentJson);
        data.setForecast(parseForecast(forecastJson));
        return data;
    }

    // =========================================================================
    // HTTP layer
    // =========================================================================

    /**
     * Executes an HTTP GET request and returns the response body as a String.
     *
     * @param urlString the full URL to request
     * @return response body text
     * @throws WeatherServiceException on connectivity failures or non-200 status codes
     */
    private String get(String urlString) throws WeatherServiceException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");

            int statusCode = connection.getResponseCode();

            if (statusCode == 401) {
                throw new WeatherServiceException("Invalid API key. Please check your OpenWeatherMap key.");
            }
            if (statusCode == 404) {
                throw new WeatherServiceException("Location not found. Please try a different city name or coordinates.");
            }
            if (statusCode == 429) {
                throw new WeatherServiceException("API rate limit exceeded. Please wait a moment before searching again.");
            }
            if (statusCode != 200) {
                throw new WeatherServiceException("Unexpected API response (HTTP " + statusCode + ").");
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            reader.close();
            return body.toString();

        } catch (UnknownHostException e) {
            throw new WeatherServiceException("No internet connection. Please check your network and try again.");
        } catch (IOException e) {
            throw new WeatherServiceException("Network error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // =========================================================================
    // JSON parsing (minimal hand-written parser)
    // =========================================================================

    /**
     * Parses the current-weather JSON response into a {@link WeatherData} object.
     *
     * <p>Precondition: {@code json} is a valid OWM /weather response string.</p>
     *
     * @param json raw JSON string from the API
     * @return populated WeatherData
     * @throws WeatherServiceException if a required field cannot be extracted
     */
    private WeatherData parseCurrentWeather(String json) throws WeatherServiceException {
        try {
            WeatherData data = new WeatherData();

            data.setCityName(extractString(json, "\"name\""));
            data.setCountryCode(extractNestedString(json, "\"country\""));
            data.setLatitude(extractDouble(json, "\"lat\""));
            data.setLongitude(extractDouble(json, "\"lon\""));
            data.setDataTimestampEpoch(extractLong(json, "\"dt\""));

            // "main" block
            data.setTemperatureCelsius(extractDouble(json, "\"temp\""));
            data.setFeelsLikeCelsius(extractDouble(json, "\"feels_like\""));
            data.setHumidity(extractDouble(json, "\"humidity\""));
            data.setPressureHpa(extractDouble(json, "\"pressure\""));

            // "wind" block - OWM returns m/s, convert to km/h
            double windMs = extractDouble(json, "\"speed\"");
            data.setWindSpeedKmh(windMs * 3.6);
            data.setWindDirectionDeg(extractDouble(json, "\"deg\""));

            // "clouds" block
            data.setCloudCoverPercent((int) extractDouble(json, "\"all\""));

            // "visibility" (metres)
            double visibilityMetres = extractDouble(json, "\"visibility\"");
            data.setVisibilityKm(visibilityMetres / 1000.0);

            // "weather" array - first entry
            data.setConditionCode(extractNestedString(json, "\"main\""));
            data.setConditionDescription(extractNestedString(json, "\"description\""));

            // "sys" block
            data.setSunriseEpoch(extractLong(json, "\"sunrise\""));
            data.setSunsetEpoch(extractLong(json, "\"sunset\""));

            return data;
        } catch (Exception e) {
            throw new WeatherServiceException("Failed to parse weather data: " + e.getMessage());
        }
    }

    /**
     * Parses the 5-day forecast JSON into an array of {@link ForecastEntry} objects.
     *
     * <p>Precondition: {@code json} is a valid OWM /forecast response string.</p>
     *
     * @param json raw JSON string from the API
     * @return array of ForecastEntry objects (up to 15 slots)
     * @throws WeatherServiceException if parsing fails
     */
    private ForecastEntry[] parseForecast(String json) throws WeatherServiceException {
        try {
            // Split on each "dt" occurrence that indicates a new forecast list item
            String[] items = json.split("\"dt\":");
            ForecastEntry[] entries = new ForecastEntry[Math.min(items.length - 1, 15)];

            for (int i = 0; i < entries.length; i++) {
                String item = items[i + 1];
                long   ts      = extractFirstLong(item);
                double temp    = extractDouble(item, "\"temp\"");
                double feels   = extractDouble(item, "\"feels_like\"");
                double humidity = extractDouble(item, "\"humidity\"");
                double windMs  = extractDouble(item, "\"speed\"");
                String code    = extractNestedString(item, "\"main\"");
                String desc    = extractNestedString(item, "\"description\"");
                double pop     = extractDoubleOrDefault(item, "\"pop\"", 0.0);

                entries[i] = new ForecastEntry(ts, temp, feels, humidity,
                        windMs * 3.6, code, desc, pop);
            }
            return entries;
        } catch (Exception e) {
            throw new WeatherServiceException("Failed to parse forecast data: " + e.getMessage());
        }
    }

    // =========================================================================
    // Minimal JSON extraction helpers
    // =========================================================================

    /**
     * Extracts a double value for the first occurrence of a key in the JSON string.
     *
     * @param json raw JSON
     * @param key  the key token including quotes, e.g. {@code "\"temp\""}
     * @return parsed double
     */
    private double extractDouble(String json, String key) {
        int index = json.indexOf(key);
        if (index < 0) return 0.0;
        int colon = json.indexOf(':', index);
        int end   = json.indexOf(',', colon);
        if (end < 0) end = json.indexOf('}', colon);
        String raw = json.substring(colon + 1, end).trim();
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Extracts a double with a default fallback when the key is absent.
     *
     * @param json         raw JSON
     * @param key          key token
     * @param defaultValue value to return if key not found
     * @return parsed double or default
     */
    private double extractDoubleOrDefault(String json, String key, double defaultValue) {
        int index = json.indexOf(key);
        if (index < 0) return defaultValue;
        return extractDouble(json, key);
    }

    /**
     * Extracts a long value for the first occurrence of a key.
     *
     * @param json raw JSON
     * @param key  key token
     * @return parsed long
     */
    private long extractLong(String json, String key) {
        return (long) extractDouble(json, key);
    }

    /**
     * Extracts the first numeric value in the string (used for "dt" splitting).
     *
     * @param fragment JSON fragment starting just after a "dt": token
     * @return parsed timestamp long
     */
    private long extractFirstLong(String fragment) {
        int end = fragment.indexOf(',');
        if (end < 0) end = fragment.indexOf('}');
        try {
            return Long.parseLong(fragment.substring(0, end).trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Extracts a quoted string value for the first occurrence of a key.
     *
     * @param json raw JSON
     * @param key  key token
     * @return string value without surrounding quotes
     */
    private String extractString(String json, String key) {
        int index = json.indexOf(key);
        if (index < 0) return "";
        int colon   = json.indexOf(':', index);
        int start   = json.indexOf('"', colon + 1) + 1;
        int end     = json.indexOf('"', start);
        return json.substring(start, end);
    }

    /**
     * Extracts a quoted string value for the SECOND occurrence of a key,
     * used for nested fields like "main" which also appears as a top-level key.
     *
     * @param json raw JSON
     * @param key  key token
     * @return string value, or empty string if not found
     */
    private String extractNestedString(String json, String key) {
        int first = json.indexOf(key);
        if (first < 0) return "";
        int second = json.indexOf(key, first + key.length());
        if (second < 0) second = first;
        int colon = json.indexOf(':', second);
        int start = json.indexOf('"', colon + 1) + 1;
        int end   = json.indexOf('"', start);
        return json.substring(start, end);
    }

    // =========================================================================
    // Utility
    // =========================================================================

    /**
     * URL-encodes a string by replacing spaces with "%20".
     * A full encoder is not used here to keep the class dependency-free.
     *
     * @param input raw string
     * @return URL-safe string
     */
    private String urlEncode(String input) {
        return input.replace(" ", "%20").replace(",", "%2C");
    }
}
