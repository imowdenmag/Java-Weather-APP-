package weatherapp.service;

import weatherapp.model.SearchHistoryEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the in-session list of previous weather searches.
 *
 * <p>History is capped at {@link #MAX_HISTORY_SIZE} entries.
 * When the cap is reached, the oldest entry is removed to make room
 * for the newest one.</p>
 *
 * <p>History is stored in memory only for this session; it is not
 * persisted to disk.</p>
 *
 * @author Owden Magnusen
 */
public class SearchHistoryManager {

    /** Maximum number of history entries retained in memory. */
    public static final int MAX_HISTORY_SIZE = 20;

    // Most-recent entry is always at index 0
    private final List<SearchHistoryEntry> entries = new ArrayList<>();

    /**
     * Adds a new search record to the top of the history list.
     *
     * <p>If the new entry has the same resolved name as the most recent entry,
     * it is silently ignored to avoid duplicate consecutive records.</p>
     *
     * @param query        raw text the user searched for
     * @param resolvedName location name resolved by the API
     */
    public void record(String query, String resolvedName) {
        if (!entries.isEmpty()) {
            String lastResolved = entries.get(0).getResolvedName();
            if (lastResolved.equalsIgnoreCase(resolvedName)) {
                return;
            }
        }

        SearchHistoryEntry entry = new SearchHistoryEntry(query, resolvedName);
        entries.add(0, entry);

        if (entries.size() > MAX_HISTORY_SIZE) {
            entries.remove(entries.size() - 1);
        }
    }

    /**
     * Returns an unmodifiable view of the history list.
     * Most-recent entry is at index 0.
     *
     * @return read-only list of {@link SearchHistoryEntry} objects
     */
    public List<SearchHistoryEntry> getHistory() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Removes all entries from the history.
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Returns the number of entries currently stored.
     *
     * @return history size
     */
    public int size() {
        return entries.size();
    }

    /**
     * Returns whether the history list is empty.
     *
     * @return true if no entries have been recorded
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
