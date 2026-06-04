package weatherapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Records a single weather search event for history tracking.
 *
 * <p>Each entry stores the query string used (city name or coordinates)
 * and the resolved display name alongside the timestamp of the lookup.</p>
 *
 * @author Owden Magnusen
 */
public class SearchHistoryEntry {

    // Formatter used for human-readable display in the history panel
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private final String query;           // raw user input
    private final String resolvedName;    // city + country from API response
    private final LocalDateTime timestamp;

    /**
     * Constructs a new history entry stamped with the current date and time.
     *
     * @param query        the raw search string entered by the user
     * @param resolvedName the location name resolved from the API response
     */
    public SearchHistoryEntry(String query, String resolvedName) {
        this.query = query;
        this.resolvedName = resolvedName;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructs a history entry with an explicit timestamp (useful for testing).
     *
     * @param query        raw search string
     * @param resolvedName API-resolved location name
     * @param timestamp    explicit timestamp for this entry
     */
    public SearchHistoryEntry(String query, String resolvedName, LocalDateTime timestamp) {
        this.query = query;
        this.resolvedName = resolvedName;
        this.timestamp = timestamp;
    }

    // -------------------------------------------------------------------------
    // Getters (no setters; history entries are immutable once created)
    // -------------------------------------------------------------------------

    public String getQuery() { return query; }

    public String getResolvedName() { return resolvedName; }

    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Returns a formatted timestamp string for display in the history panel.
     *
     * @return timestamp formatted as "yyyy-MM-dd hh:mm a"
     */
    public String getFormattedTimestamp() {
        return timestamp.format(DISPLAY_FORMATTER);
    }

    @Override
    public String toString() {
        return resolvedName + "  |  " + getFormattedTimestamp();
    }
}
