package weatherapp.ui;

import weatherapp.model.WeatherData;
import weatherapp.service.SearchHistoryManager;
import weatherapp.service.WeatherService;
import weatherapp.util.TimeOfDayUtil;
import weatherapp.util.UnitConverter;
import weatherapp.util.WeatherIconUtil;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 * The main application window for the Weather Information App.
 *
 * <p>Composed of:
 * <ul>
 *   <li>A search bar (city name or lat/lon coordinates)</li>
 *   <li>A current-conditions panel showing temperature, humidity, wind speed, and conditions</li>
 *   <li>A {@link ForecastPanel} for the 5-day outlook</li>
 *   <li>A {@link HistoryPanel} for recent searches</li>
 *   <li>Unit conversion controls (°C/°F, km/h / mph / m/s)</li>
 *   <li>Dynamic background gradient via {@link ThemeManager}</li>
 * </ul>
 *
 * <p>API calls are executed on a background {@link SwingWorker} thread so the
 * GUI remains responsive during network requests.</p>
 *
 * @author Owden Magnusen
 */
public class WeatherAppFrame extends JFrame {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    private static final String WINDOW_TITLE  = "Weather App";
    private static final int    WINDOW_WIDTH  = 820;
    private static final int    WINDOW_HEIGHT = 680;

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------
    private final WeatherService       weatherService;
    private final SearchHistoryManager historyManager;
    private final ThemeManager         theme;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private String tempUnit  = "C";    // "C" or "F"
    private String windUnit  = "kmh";  // "kmh", "mph", "ms"
    private WeatherData currentData;

    // -------------------------------------------------------------------------
    // UI Components
    // -------------------------------------------------------------------------
    private ThemeManager.GradientPanel rootPanel;

    // Search area
    private JTextField searchField;
    private JTextField latField;
    private JTextField lonField;
    private JButton    searchButton;
    private JLabel     errorLabel;

    // Current conditions
    private JLabel conditionIconLabel;
    private JLabel temperatureLabel;
    private JLabel feelsLikeLabel;
    private JLabel conditionLabel;
    private JLabel locationLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private JLabel pressureLabel;
    private JLabel visibilityLabel;
    private JLabel sunriseLabel;
    private JLabel sunsetLabel;
    private JLabel timestampLabel;

    // Sub-panels
    private ForecastPanel forecastPanel;
    private HistoryPanel  historyPanel;

    // Unit toggle controls
    private JRadioButton celsiusBtn;
    private JRadioButton fahrenheitBtn;
    private JRadioButton kmhBtn;
    private JRadioButton mphBtn;
    private JRadioButton msBtn;

    // Loading overlay
    private JPanel   loadingOverlay;
    private CardLayout cardLayout;
    private JPanel   mainContent;
    private static final String CARD_MAIN    = "main";
    private static final String CARD_LOADING = "loading";

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs the main application window.
     *
     * @param apiKey valid OpenWeatherMap API key
     */
    public WeatherAppFrame(String apiKey) {
        this.weatherService = new WeatherService(apiKey);
        this.historyManager = new SearchHistoryManager();
        this.theme          = new ThemeManager();

        configureFrame();
        buildLayout();
        bindKeyboardShortcuts();
    }

    // -------------------------------------------------------------------------
    // Frame configuration
    // -------------------------------------------------------------------------

    /** Sets basic JFrame properties. */
    private void configureFrame() {
        setTitle(WINDOW_TITLE + " | " + TimeOfDayUtil.periodLabel(theme.getCurrentPeriod()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setMinimumSize(new Dimension(680, 540));
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // Layout construction
    // -------------------------------------------------------------------------

    /** Builds and attaches all panels and components to the frame. */
    private void buildLayout() {
        rootPanel = theme.createGradientPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));

        rootPanel.add(buildTopBar(),    BorderLayout.NORTH);
        rootPanel.add(buildSidePanel(), BorderLayout.WEST);
        rootPanel.add(buildMainArea(),  BorderLayout.CENTER);

        setContentPane(rootPanel);
        pack();
    }

    // =========================================================================
    // Top bar: search + unit toggles
    // =========================================================================

    /**
     * Builds the top search bar and unit conversion controls.
     *
     * @return the fully constructed top bar panel
     */
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel();
        topBar.setOpaque(false);
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.Y_AXIS));
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        topBar.add(buildSearchRow());
        topBar.add(buildUnitToggleRow());
        topBar.add(buildErrorRow());

        return topBar;
    }

    /** Builds the city-name and lat/lon input row with the search button. */
    private JPanel buildSearchRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        searchField = styledTextField(22);
        searchField.setToolTipText("Enter city name, e.g. Accra or London,GB");

        JLabel separator = new JLabel("or");
        separator.setFont(new Font("SansSerif", Font.PLAIN, 12));
        separator.setForeground(theme.getMutedTextColor());

        latField = styledTextField(7);
        latField.setToolTipText("Latitude, e.g. 5.6037");

        JLabel comma = new JLabel(",");
        comma.setForeground(theme.getMutedTextColor());

        lonField = styledTextField(7);
        lonField.setToolTipText("Longitude, e.g. -0.1870");

        searchButton = new JButton("Search");
        styleButton(searchButton);
        searchButton.addActionListener(e -> triggerSearch());

        row.add(labeledField("City", searchField));
        row.add(separator);
        row.add(labeledField("Lat", latField));
        row.add(comma);
        row.add(labeledField("Lon", lonField));
        row.add(searchButton);

        return row;
    }

    /** Builds the temperature and wind speed unit toggle controls. */
    private JPanel buildUnitToggleRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        row.setOpaque(false);

        celsiusBtn    = radioButton("°C");
        fahrenheitBtn = radioButton("°F");
        celsiusBtn.setSelected(true);

        ButtonGroup tempGroup = new ButtonGroup();
        tempGroup.add(celsiusBtn);
        tempGroup.add(fahrenheitBtn);

        celsiusBtn.addActionListener(e -> {
            tempUnit = "C";
            refreshDisplayedValues();
        });
        fahrenheitBtn.addActionListener(e -> {
            tempUnit = "F";
            refreshDisplayedValues();
        });

        kmhBtn = radioButton("km/h");
        mphBtn = radioButton("mph");
        msBtn  = radioButton("m/s");
        kmhBtn.setSelected(true);

        ButtonGroup windGroup = new ButtonGroup();
        windGroup.add(kmhBtn);
        windGroup.add(mphBtn);
        windGroup.add(msBtn);

        kmhBtn.addActionListener(e -> { windUnit = "kmh"; refreshDisplayedValues(); });
        mphBtn.addActionListener(e -> { windUnit = "mph"; refreshDisplayedValues(); });
        msBtn.addActionListener(e ->  { windUnit = "ms";  refreshDisplayedValues(); });

        JLabel tempLabel = mutedLabel("Temp:");
        JLabel windLabel = mutedLabel("Wind:");

        row.add(tempLabel);
        row.add(celsiusBtn);
        row.add(fahrenheitBtn);
        row.add(mutedLabel("|"));
        row.add(windLabel);
        row.add(kmhBtn);
        row.add(mphBtn);
        row.add(msBtn);

        return row;
    }

    /** Builds the error message row (initially hidden). */
    private JPanel buildErrorRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setForeground(new Color(0xFF6B6B));
        row.add(errorLabel);

        return row;
    }

    // =========================================================================
    // Side panel: history
    // =========================================================================

    /**
     * Builds the left-side history panel.
     *
     * @return configured side panel
     */
    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout());
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(230, 0));
        side.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 8));

        historyPanel = new HistoryPanel(theme, historyManager);
        historyPanel.setHistorySelectionListener(entry ->
                rerunSearch(entry.getQuery(), entry.getResolvedName())
        );

        side.add(historyPanel, BorderLayout.CENTER);
        return side;
    }

    // =========================================================================
    // Main content area: current conditions + forecast
    // =========================================================================

    /**
     * Builds the main content area using a CardLayout to allow switching
     * between a loading state and the weather display.
     *
     * @return the outer card-layout panel
     */
    private JPanel buildMainArea() {
        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 16));

        mainContent.add(buildWeatherDisplay(), CARD_MAIN);
        mainContent.add(buildLoadingPanel(),   CARD_LOADING);

        return mainContent;
    }

    /** Builds the main weather display panel (current + forecast). */
    private JPanel buildWeatherDisplay() {
        JPanel display = new JPanel(new BorderLayout(0, 16));
        display.setOpaque(false);

        display.add(buildCurrentConditionsPanel(), BorderLayout.CENTER);

        forecastPanel = new ForecastPanel(theme);
        display.add(forecastPanel, BorderLayout.SOUTH);

        return display;
    }

    /**
     * Builds the current-conditions card containing the large temperature,
     * icon, and detail grid.
     *
     * @return the conditions panel
     */
    private JPanel buildCurrentConditionsPanel() {
        JPanel card = new RoundedCard(theme.getCardBackground(), theme.getCardBorder(), 16);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hero section: icon + temperature + condition
        JPanel hero = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        hero.setOpaque(false);

        conditionIconLabel = new JLabel("\uD83C\uDF21\uFE0F");
        conditionIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 64));
        conditionIconLabel.setForeground(theme.getPrimaryTextColor());

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        temperatureLabel = new JLabel("--°");
        temperatureLabel.setFont(new Font("SansSerif", Font.BOLD, 52));
        temperatureLabel.setForeground(theme.getPrimaryTextColor());

        conditionLabel = new JLabel("Search for a city to begin");
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        conditionLabel.setForeground(theme.getMutedTextColor());

        locationLabel = new JLabel(" ");
        locationLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        locationLabel.setForeground(theme.getPrimaryTextColor());

        feelsLikeLabel = new JLabel(" ");
        feelsLikeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        feelsLikeLabel.setForeground(theme.getMutedTextColor());

        heroText.add(locationLabel);
        heroText.add(temperatureLabel);
        heroText.add(conditionLabel);
        heroText.add(feelsLikeLabel);

        hero.add(conditionIconLabel);
        hero.add(heroText);
        card.add(hero, BorderLayout.NORTH);

        // Detail grid
        card.add(buildDetailGrid(), BorderLayout.CENTER);

        // Timestamp
        timestampLabel = new JLabel(" ", SwingConstants.RIGHT);
        timestampLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timestampLabel.setForeground(theme.getMutedTextColor());
        card.add(timestampLabel, BorderLayout.SOUTH);

        return card;
    }

    /** Builds the 2x3 detail grid for humidity, wind, pressure, etc. */
    private JPanel buildDetailGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 8));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        humidityLabel   = detailCard("\uD83D\uDCA7", "Humidity", "--");
        windLabel       = detailCard("\uD83D\uDCA8", "Wind", "--");
        pressureLabel   = detailCard("\uD83C\uDF21\uFE0F", "Pressure", "--");
        visibilityLabel = detailCard("\uD83D\uDC41\uFE0F", "Visibility", "--");
        sunriseLabel    = detailCard("\uD83C\uDF05", "Sunrise", "--");
        sunsetLabel     = detailCard("\uD83C\uDF07", "Sunset", "--");

        // The detail cards are full panels; only label references are kept above
        // to allow text updates. Rebuild these as panels here.
        grid.add(buildDetailCard("\uD83D\uDCA7", "Humidity",   humidityLabel));
        grid.add(buildDetailCard("\uD83D\uDCA8", "Wind",       windLabel));
        grid.add(buildDetailCard("\uD83C\uDF21\uFE0F", "Pressure", pressureLabel));
        grid.add(buildDetailCard("\uD83D\uDC41\uFE0F", "Visibility", visibilityLabel));
        grid.add(buildDetailCard("\uD83C\uDF05", "Sunrise",    sunriseLabel));
        grid.add(buildDetailCard("\uD83C\uDF07", "Sunset",     sunsetLabel));

        return grid;
    }

    /**
     * Creates a single detail card panel containing an icon row, a value label,
     * and a key label below.
     *
     * @param icon       Unicode emoji icon string
     * @param key        label text for the metric name
     * @param valueLabel the pre-created JLabel reference for this value
     * @return the assembled detail card panel
     */
    private JPanel buildDetailCard(String icon, String key, JLabel valueLabel) {
        JPanel card = new RoundedCard(theme.getCardBackground(), theme.getCardBorder(), 10);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        iconLabel.setForeground(theme.getPrimaryTextColor());
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        valueLabel.setForeground(theme.getPrimaryTextColor());
        valueLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        keyLabel.setForeground(theme.getMutedTextColor());
        keyLabel.setAlignmentX(CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(valueLabel);
        card.add(keyLabel);

        return card;
    }

    /**
     * Creates a value JLabel. Used to pre-create label references before
     * building the card panel around them.
     *
     * @param icon  unused (kept for method symmetry with buildDetailCard)
     * @param key   unused
     * @param value initial text
     * @return new JLabel
     */
    private JLabel detailCard(String icon, String key, String value) {
        JLabel label = new JLabel(value, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        return label;
    }

    /** Builds the loading spinner panel. */
    private JPanel buildLoadingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel loading = new JLabel("Fetching weather data...", SwingConstants.CENTER);
        loading.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loading.setForeground(theme.getMutedTextColor());
        panel.add(loading, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // Search logic
    // =========================================================================

    /** Reads the input fields and dispatches the appropriate API call. */
    private void triggerSearch() {
        clearError();

        String cityInput = searchField.getText().trim();
        String latInput  = latField.getText().trim();
        String lonInput  = lonField.getText().trim();

        // Validate: at least one input must be provided
        if (cityInput.isEmpty() && (latInput.isEmpty() || lonInput.isEmpty())) {
            showError("Please enter a city name or both latitude and longitude.");
            return;
        }

        if (!latInput.isEmpty() || !lonInput.isEmpty()) {
            // Coordinate search
            if (latInput.isEmpty() || lonInput.isEmpty()) {
                showError("Please enter both latitude and longitude.");
                return;
            }
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(latInput);
                lon = Double.parseDouble(lonInput);
            } catch (NumberFormatException e) {
                showError("Coordinates must be valid numbers (e.g. 5.6037, -0.1870).");
                return;
            }
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                showError("Coordinates out of range. Latitude: -90 to 90, Longitude: -180 to 180.");
                return;
            }
            fetchByCoordinates(lat, lon, latInput + ", " + lonInput);
        } else {
            // City name search
            fetchByCity(cityInput);
        }
    }

    /**
     * Initiates an asynchronous search by city name.
     *
     * @param cityName the city name entered by the user
     */
    private void fetchByCity(String cityName) {
        showLoadingState();
        new SwingWorker<WeatherData, Void>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return weatherService.fetchByCity(cityName);
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    onWeatherLoaded(data, cityName);
                } catch (Exception e) {
                    String msg = unwrapMessage(e);
                    onWeatherError(msg);
                }
            }
        }.execute();
    }

    /**
     * Initiates an asynchronous search by geographic coordinates.
     *
     * @param lat   latitude
     * @param lon   longitude
     * @param label display label for the raw query
     */
    private void fetchByCoordinates(double lat, double lon, String label) {
        showLoadingState();
        new SwingWorker<WeatherData, Void>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return weatherService.fetchByCoordinates(lat, lon);
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    onWeatherLoaded(data, label);
                } catch (Exception e) {
                    String msg = unwrapMessage(e);
                    onWeatherError(msg);
                }
            }
        }.execute();
    }

    /**
     * Re-runs a past search from the history panel.
     *
     * @param query        original query text
     * @param resolvedName resolved location name (used for display only)
     */
    private void rerunSearch(String query, String resolvedName) {
        searchField.setText(query);
        latField.setText("");
        lonField.setText("");
        fetchByCity(query);
    }

    // =========================================================================
    // Display update
    // =========================================================================

    /**
     * Updates all display labels from a freshly fetched {@link WeatherData} object.
     *
     * @param data       the new weather data
     * @param rawQuery   the raw query string used to fetch this data
     */
    private void onWeatherLoaded(WeatherData data, String rawQuery) {
        this.currentData = data;

        historyManager.record(rawQuery, data.getDisplayLocation());
        historyPanel.refresh();

        updateThemeForCurrentTime();
        refreshDisplayedValues();

        hideLoadingState();
        clearError();
    }

    /**
     * Displays an error message and restores the main view.
     *
     * @param message user-friendly error text
     */
    private void onWeatherError(String message) {
        hideLoadingState();
        showError(message);
    }

    /**
     * Refreshes all display labels using the currently selected units.
     * Called both after a new data load and when unit toggles change.
     */
    private void refreshDisplayedValues() {
        if (currentData == null) return;

        WeatherData d = currentData;

        // Location and icon
        locationLabel.setText(d.getDisplayLocation());
        conditionIconLabel.setText(WeatherIconUtil.getIcon(d.getConditionCode()));
        conditionLabel.setText(WeatherIconUtil.getConditionLabel(d.getConditionCode())
                + " \u2014 " + capitalize(d.getConditionDescription()));

        // Temperature
        double displayTemp   = UnitConverter.convertTemperature(d.getTemperatureCelsius(), tempUnit);
        double displayFeels  = UnitConverter.convertTemperature(d.getFeelsLikeCelsius(), tempUnit);
        String unitStr       = UnitConverter.tempUnitLabel(tempUnit);

        temperatureLabel.setText(displayTemp + unitStr);
        feelsLikeLabel.setText("Feels like " + displayFeels + unitStr);

        // Detail grid
        humidityLabel.setText((int) d.getHumidity() + "%");

        double displayWind = UnitConverter.convertWindSpeed(d.getWindSpeedKmh(), windUnit);
        String windDir     = WeatherIconUtil.windDirection(d.getWindDirectionDeg());
        windLabel.setText(displayWind + " " + UnitConverter.windUnitLabel(windUnit) + " " + windDir);

        pressureLabel.setText((int) d.getPressureHpa() + " hPa");
        visibilityLabel.setText(d.getVisibilityKm() + " km");

        sunriseLabel.setText(epochToTime(d.getSunriseEpoch()));
        sunsetLabel.setText(epochToTime(d.getSunsetEpoch()));

        timestampLabel.setText("Updated " + epochToDateTime(d.getDataTimestampEpoch()));

        // Forecast
        if (d.getForecast() != null && d.getForecast().length > 0) {
            forecastPanel.refresh(d.getForecast(), tempUnit);
        }

        rootPanel.revalidate();
        rootPanel.repaint();
    }

    // =========================================================================
    // Theme
    // =========================================================================

    /** Updates the gradient background to match the current time of day. */
    private void updateThemeForCurrentTime() {
        TimeOfDayUtil.Period period = TimeOfDayUtil.getCurrentPeriod();
        theme.setPeriod(period);
        rootPanel.updateGradient(theme.getGradientTop(), theme.getGradientBottom());
        setTitle(WINDOW_TITLE + " | " + TimeOfDayUtil.periodLabel(period));
    }

    // =========================================================================
    // UI helpers
    // =========================================================================

    /** Shows the loading card. */
    private void showLoadingState() {
        cardLayout.show(mainContent, CARD_LOADING);
    }

    /** Shows the main weather display card. */
    private void hideLoadingState() {
        cardLayout.show(mainContent, CARD_MAIN);
    }

    /** Displays an error message in the error label. */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /** Clears the error label. */
    private void clearError() {
        errorLabel.setText(" ");
    }

    /** Binds Enter key in both input fields to trigger the search. */
    private void bindKeyboardShortcuts() {
        AbstractAction searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triggerSearch();
            }
        };

        for (JTextField field : new JTextField[]{searchField, latField, lonField}) {
            field.getInputMap(JComponent.WHEN_FOCUSED)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");
            field.getActionMap().put("search", searchAction);
        }
    }

    /**
     * Creates a small styled text field.
     *
     * @param columns number of character columns
     * @return configured JTextField
     */
    private JTextField styledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(new Color(255, 255, 255, 50));
        field.setForeground(theme.getPrimaryTextColor());
        field.setCaretColor(theme.getPrimaryTextColor());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getCardBorder(), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    /**
     * Applies button styling to a JButton.
     *
     * @param button the button to style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(new Color(255, 255, 255, 60));
        button.setForeground(theme.getPrimaryTextColor());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getCardBorder(), 1, true),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Creates a styled radio button for unit selection.
     *
     * @param text button label
     * @return configured JRadioButton
     */
    private JRadioButton radioButton(String text) {
        JRadioButton btn = new JRadioButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(theme.getPrimaryTextColor());
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a muted colour label for decorative text.
     *
     * @param text label content
     * @return configured JLabel
     */
    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(theme.getMutedTextColor());
        return label;
    }

    /**
     * Wraps a field in a vertically labelled container.
     *
     * @param labelText the field label
     * @param field     the input component
     * @return a small panel with label above and field below
     */
    private JPanel labeledField(String labelText, JTextField field) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(theme.getMutedTextColor());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);

        wrapper.add(lbl);
        wrapper.add(field);
        return wrapper;
    }

    /**
     * Unwraps a checked exception message from an ExecutionException.
     *
     * @param e the caught exception
     * @return the user-friendly message string
     */
    private String unwrapMessage(Exception e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            return cause.getMessage();
        }
        return e.getMessage();
    }

    /**
     * Converts a Unix epoch timestamp to a local time string (HH:mm am/pm).
     *
     * @param epoch Unix epoch seconds
     * @return formatted time string
     */
    private String epochToTime(long epoch) {
        if (epoch == 0) return "--";
        java.time.LocalTime time = java.time.Instant.ofEpochSecond(epoch)
                .atZone(java.time.ZoneId.systemDefault()).toLocalTime();
        return time.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
    }

    /**
     * Converts a Unix epoch timestamp to a local date-time string.
     *
     * @param epoch Unix epoch seconds
     * @return formatted date-time string
     */
    private String epochToDateTime(long epoch) {
        if (epoch == 0) return "--";
        java.time.LocalDateTime dt = java.time.Instant.ofEpochSecond(epoch)
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        return dt.format(java.time.format.DateTimeFormatter.ofPattern("EEE d MMM, hh:mm a"));
    }

    /**
     * Capitalises the first letter of a string.
     *
     * @param text input string
     * @return string with first letter upper-cased
     */
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    // =========================================================================
    // Inner class: RoundedCard
    // =========================================================================

    /**
     * A JPanel with a painted rounded rectangle background, reused for cards
     * throughout the layout.
     */
    private class RoundedCard extends JPanel {

        private final Color bgColor;
        private final Color borderColor;
        private final int   arc;

        RoundedCard(Color bgColor, Color borderColor, int arc) {
            this.bgColor     = bgColor;
            this.borderColor = borderColor;
            this.arc         = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
