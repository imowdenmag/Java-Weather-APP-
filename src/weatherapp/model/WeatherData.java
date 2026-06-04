package weatherapp.model;

/**
 * Represents a complete weather snapshot for a given location,
 * including current conditions and a short-term forecast.
 *
 * <p>All temperature values are stored internally in Celsius.
 * All wind speed values are stored internally in km/h.
 * Conversion to other units is handled by {@link weatherapp.util.UnitConverter}.</p>
 *
 * @author Owden Magnusen
 */
public class WeatherData {

    // Location identity
    private String cityName;
    private String countryCode;
    private double latitude;
    private double longitude;

    // Current conditions (stored in SI/metric units)
    private double temperatureCelsius;
    private double feelsLikeCelsius;
    private double humidity;          // percentage 0-100
    private double windSpeedKmh;
    private double windDirectionDeg;
    private String conditionCode;     // e.g. "Clear", "Rain", "Thunderstorm"
    private String conditionDescription;
    private int cloudCoverPercent;
    private double visibilityKm;
    private double pressureHpa;
    private long sunriseEpoch;
    private long sunsetEpoch;
    private long dataTimestampEpoch;

    // 5-day / 3-hour forecast entries
    private ForecastEntry[] forecast;

    /**
     * Constructs an empty WeatherData instance.
     * All fields should be populated via setters after API parsing.
     */
    public WeatherData() {
        this.forecast = new ForecastEntry[0];
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public double getFeelsLikeCelsius() { return feelsLikeCelsius; }
    public void setFeelsLikeCelsius(double feelsLikeCelsius) { this.feelsLikeCelsius = feelsLikeCelsius; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeedKmh() { return windSpeedKmh; }
    public void setWindSpeedKmh(double windSpeedKmh) { this.windSpeedKmh = windSpeedKmh; }

    public double getWindDirectionDeg() { return windDirectionDeg; }
    public void setWindDirectionDeg(double windDirectionDeg) { this.windDirectionDeg = windDirectionDeg; }

    public String getConditionCode() { return conditionCode; }
    public void setConditionCode(String conditionCode) { this.conditionCode = conditionCode; }

    public String getConditionDescription() { return conditionDescription; }
    public void setConditionDescription(String conditionDescription) { this.conditionDescription = conditionDescription; }

    public int getCloudCoverPercent() { return cloudCoverPercent; }
    public void setCloudCoverPercent(int cloudCoverPercent) { this.cloudCoverPercent = cloudCoverPercent; }

    public double getVisibilityKm() { return visibilityKm; }
    public void setVisibilityKm(double visibilityKm) { this.visibilityKm = visibilityKm; }

    public double getPressureHpa() { return pressureHpa; }
    public void setPressureHpa(double pressureHpa) { this.pressureHpa = pressureHpa; }

    public long getSunriseEpoch() { return sunriseEpoch; }
    public void setSunriseEpoch(long sunriseEpoch) { this.sunriseEpoch = sunriseEpoch; }

    public long getSunsetEpoch() { return sunsetEpoch; }
    public void setSunsetEpoch(long sunsetEpoch) { this.sunsetEpoch = sunsetEpoch; }

    public long getDataTimestampEpoch() { return dataTimestampEpoch; }
    public void setDataTimestampEpoch(long dataTimestampEpoch) { this.dataTimestampEpoch = dataTimestampEpoch; }

    public ForecastEntry[] getForecast() { return forecast; }
    public void setForecast(ForecastEntry[] forecast) { this.forecast = forecast; }

    /**
     * Returns a display-friendly location string, e.g. "Accra, GH".
     *
     * @return formatted location label
     */
    public String getDisplayLocation() {
        if (countryCode != null && !countryCode.isEmpty()) {
            return cityName + ", " + countryCode;
        }
        return cityName;
    }
}
