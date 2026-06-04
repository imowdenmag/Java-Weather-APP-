package weatherapp.model;

/**
 * Represents a single forecast slot (typically a 3-hour block)
 * returned by the OpenWeatherMap 5-day forecast endpoint.
 *
 * <p>Temperature is stored in Celsius; wind speed in km/h.</p>
 *
 * @author Owden Magnusen
 */
public class ForecastEntry {

    private long timestampEpoch;
    private double temperatureCelsius;
    private double feelsLikeCelsius;
    private double humidity;
    private double windSpeedKmh;
    private String conditionCode;
    private String conditionDescription;
    private double precipitationProbability; // 0.0 to 1.0

    /**
     * Constructs a ForecastEntry with all required fields.
     *
     * @param timestampEpoch          Unix epoch for this forecast slot
     * @param temperatureCelsius      predicted temperature in Celsius
     * @param feelsLikeCelsius        perceived temperature in Celsius
     * @param humidity                relative humidity percentage
     * @param windSpeedKmh            wind speed in km/h
     * @param conditionCode           short weather category (e.g. "Rain")
     * @param conditionDescription    human-readable description
     * @param precipitationProbability probability of precipitation (0-1)
     */
    public ForecastEntry(
            long timestampEpoch,
            double temperatureCelsius,
            double feelsLikeCelsius,
            double humidity,
            double windSpeedKmh,
            String conditionCode,
            String conditionDescription,
            double precipitationProbability
    ) {
        this.timestampEpoch = timestampEpoch;
        this.temperatureCelsius = temperatureCelsius;
        this.feelsLikeCelsius = feelsLikeCelsius;
        this.humidity = humidity;
        this.windSpeedKmh = windSpeedKmh;
        this.conditionCode = conditionCode;
        this.conditionDescription = conditionDescription;
        this.precipitationProbability = precipitationProbability;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public long getTimestampEpoch() { return timestampEpoch; }
    public void setTimestampEpoch(long timestampEpoch) { this.timestampEpoch = timestampEpoch; }

    public double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public double getFeelsLikeCelsius() { return feelsLikeCelsius; }
    public void setFeelsLikeCelsius(double feelsLikeCelsius) { this.feelsLikeCelsius = feelsLikeCelsius; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeedKmh() { return windSpeedKmh; }
    public void setWindSpeedKmh(double windSpeedKmh) { this.windSpeedKmh = windSpeedKmh; }

    public String getConditionCode() { return conditionCode; }
    public void setConditionCode(String conditionCode) { this.conditionCode = conditionCode; }

    public String getConditionDescription() { return conditionDescription; }
    public void setConditionDescription(String conditionDescription) { this.conditionDescription = conditionDescription; }

    public double getPrecipitationProbability() { return precipitationProbability; }
    public void setPrecipitationProbability(double precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }
}
