package weatherapp.ui;

import weatherapp.model.ForecastEntry;
import weatherapp.util.UnitConverter;
import weatherapp.util.WeatherIconUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A horizontal strip displaying up to 5 forecast slots (one per 3-hour block
 * from the OWM forecast endpoint).
 *
 * <p>Each slot shows the time, a weather icon, the high temperature,
 * and a precipitation probability indicator.</p>
 *
 * @author Owden Magnusen
 */
public class ForecastPanel extends JPanel {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("EEE\nha");

    private static final int CARD_WIDTH  = 90;
    private static final int CARD_HEIGHT = 110;

    private final ThemeManager theme;

    // Current unit preferences (updated via refresh)
    private String tempUnit  = "C";

    /**
     * Constructs an empty ForecastPanel using the given ThemeManager.
     *
     * @param theme the active theme for colours
     */
    public ForecastPanel(ThemeManager theme) {
        this.theme = theme;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    /**
     * Rebuilds the forecast slot cards from a new array of entries.
     *
     * @param entries  array of forecast data (up to 15 slots used, first 5 shown per day)
     * @param tempUnit "C" or "F"
     */
    public void refresh(ForecastEntry[] entries, String tempUnit) {
        this.tempUnit = tempUnit;
        removeAll();

        JLabel sectionLabel = new JLabel("5-DAY FORECAST");
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        sectionLabel.setForeground(theme.getMutedTextColor());
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(sectionLabel, BorderLayout.NORTH);

        JPanel cardRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cardRow.setOpaque(false);

        int slotsToShow = Math.min(entries.length, 5);
        for (int i = 0; i < slotsToShow; i++) {
            cardRow.add(buildSlotCard(entries[i]));
        }

        JScrollPane scroll = new JScrollPane(cardRow,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 4));

        add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Builds a single forecast slot card panel.
     *
     * @param entry the forecast entry to display
     * @return a styled JPanel card
     */
    private JPanel buildSlotCard(ForecastEntry entry) {
        RoundedCard card = new RoundedCard(theme.getCardBackground(), theme.getCardBorder(), 12);
        card.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));

        // Time label
        LocalDateTime dt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(entry.getTimestampEpoch()), ZoneId.systemDefault());
        String timeText = dt.format(DateTimeFormatter.ofPattern("EEE")) + "\n"
                + dt.format(DateTimeFormatter.ofPattern("ha"));
        JLabel timeLabel = centeredLabel(timeText.replace("\n", " "), 10, Font.BOLD);
        timeLabel.setForeground(theme.getMutedTextColor());

        // Icon
        String icon = WeatherIconUtil.getIcon(entry.getConditionCode());
        JLabel iconLabel = centeredLabel(icon, 20, Font.PLAIN);
        iconLabel.setForeground(theme.getPrimaryTextColor());

        // Temperature
        double displayTemp = UnitConverter.convertTemperature(
                entry.getTemperatureCelsius(), tempUnit);
        String tempText = displayTemp + UnitConverter.tempUnitLabel(tempUnit);
        JLabel tempLabel = centeredLabel(tempText, 13, Font.BOLD);
        tempLabel.setForeground(theme.getPrimaryTextColor());

        // Precipitation probability
        int popPct = (int) Math.round(entry.getPrecipitationProbability() * 100);
        JLabel popLabel = centeredLabel("\uD83D\uDCA7 " + popPct + "%", 10, Font.PLAIN);
        popLabel.setForeground(theme.getMutedTextColor());

        card.add(timeLabel);
        card.add(iconLabel);
        card.add(tempLabel);
        card.add(popLabel);

        return card;
    }

    /**
     * Creates a centred JLabel with the given font settings.
     *
     * @param text      label text
     * @param fontSize  font size in points
     * @param fontStyle Font.BOLD or Font.PLAIN
     * @return configured JLabel
     */
    private JLabel centeredLabel(String text, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("SansSerif", fontStyle, fontSize));
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    // =========================================================================
    // Inner class: RoundedCard
    // =========================================================================

    /**
     * A JPanel that paints itself with a rounded rectangle background and border.
     */
    private static class RoundedCard extends JPanel {

        private final Color bgColor;
        private final Color borderColor;
        private final int   arcRadius;

        RoundedCard(Color bgColor, Color borderColor, int arcRadius) {
            this.bgColor     = bgColor;
            this.borderColor = borderColor;
            this.arcRadius   = arcRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcRadius, arcRadius);

            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcRadius, arcRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
