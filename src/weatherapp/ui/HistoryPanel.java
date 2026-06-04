package weatherapp.ui;

import weatherapp.model.SearchHistoryEntry;
import weatherapp.service.SearchHistoryManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/**
 * Displays the in-session weather search history as a scrollable list.
 *
 * <p>Exposes a callback interface so the parent window can respond when
 * the user clicks an entry to re-run that search.</p>
 *
 * @author Owden Magnusen
 */
public class HistoryPanel extends JPanel {

    /** Callback triggered when the user selects a history entry. */
    public interface HistorySelectionListener {
        /**
         * Called when the user clicks a history entry.
         *
         * @param entry the selected search history record
         */
        void onHistorySelected(SearchHistoryEntry entry);
    }

    private final ThemeManager          theme;
    private final SearchHistoryManager  historyManager;
    private final DefaultListModel<SearchHistoryEntry> listModel;
    private final JList<SearchHistoryEntry> historyList;

    private HistorySelectionListener selectionListener;

    /**
     * Constructs a HistoryPanel.
     *
     * @param theme          active ThemeManager for styling
     * @param historyManager the session history manager
     */
    public HistoryPanel(ThemeManager theme, SearchHistoryManager historyManager) {
        this.theme          = theme;
        this.historyManager = historyManager;
        this.listModel      = new DefaultListModel<>();
        this.historyList    = new JList<>(listModel);

        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        buildUI();
    }

    /**
     * Sets the listener that will receive click events on history entries.
     *
     * @param listener callback implementation
     */
    public void setHistorySelectionListener(HistorySelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Reloads the list model from the current history manager state.
     * Call this after each new search completes.
     */
    public void refresh() {
        listModel.clear();
        List<SearchHistoryEntry> entries = historyManager.getHistory();
        for (SearchHistoryEntry entry : entries) {
            listModel.addElement(entry);
        }
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /** Builds and attaches all child components. */
    private void buildUI() {
        // Header row with title and clear button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("RECENT SEARCHES");
        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setForeground(theme.getMutedTextColor());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearBtn.setForeground(theme.getMutedTextColor());
        clearBtn.setBackground(new Color(0, 0, 0, 0));
        clearBtn.setBorderPainted(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            historyManager.clear();
            refresh();
        });

        header.add(title, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // List component
        historyList.setOpaque(false);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setCellRenderer(new HistoryCellRenderer());
        historyList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        historyList.setFixedCellHeight(44);

        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = historyList.locationToIndex(e.getPoint());
                if (index >= 0 && selectionListener != null) {
                    SearchHistoryEntry entry = listModel.getElementAt(index);
                    historyList.clearSelection();
                    selectionListener.onHistorySelected(entry);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(historyList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(260, 200));

        add(scroll, BorderLayout.CENTER);
    }

    // =========================================================================
    // Inner class: HistoryCellRenderer
    // =========================================================================

    /**
     * Custom list cell renderer that draws each history entry as a two-line card.
     */
    private class HistoryCellRenderer implements ListCellRenderer<SearchHistoryEntry> {

        @Override
        public Component getListCellRendererComponent(
                JList<? extends SearchHistoryEntry> list,
                SearchHistoryEntry value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JPanel cell = new JPanel(new BorderLayout(0, 2));
            cell.setOpaque(false);
            cell.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0,
                            new Color(255, 255, 255, 30)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            if (isSelected) {
                cell.setBackground(new Color(255, 255, 255, 40));
                cell.setOpaque(true);
            }

            JLabel nameLabel = new JLabel(value.getResolvedName());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            nameLabel.setForeground(theme.getPrimaryTextColor());

            JLabel timeLabel = new JLabel(value.getFormattedTimestamp());
            timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            timeLabel.setForeground(theme.getMutedTextColor());

            cell.add(nameLabel, BorderLayout.NORTH);
            cell.add(timeLabel, BorderLayout.SOUTH);

            return cell;
        }
    }
}
