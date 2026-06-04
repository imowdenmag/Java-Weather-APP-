package weatherapp.ui;

import weatherapp.util.TimeOfDayUtil;
import weatherapp.util.TimeOfDayUtil.Period;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Provides the colour palette and gradient background for the application,
 * adapting to the current time of day.
 *
 * <p>Each {@link Period} is mapped to a pair of gradient stop colours and
 * a set of foreground text colours suitable for rendering against that background.</p>
 *
 * @author Owden Magnusen
 */
public class ThemeManager {

    // =========================================================================
    // Morning theme (warm golden sky)
    // =========================================================================
    private static final Color MORNING_TOP    = new Color(0xFF9A3C, false);
    private static final Color MORNING_BOTTOM = new Color(0xFFD580, false);
    private static final Color MORNING_TEXT   = new Color(0x3D1C02, false);

    // =========================================================================
    // Afternoon theme (clear blue day)
    // =========================================================================
    private static final Color AFTERNOON_TOP    = new Color(0x1A78C2, false);
    private static final Color AFTERNOON_BOTTOM = new Color(0x87CEEB, false);
    private static final Color AFTERNOON_TEXT   = new Color(0x0A2A45, false);

    // =========================================================================
    // Evening theme (deep amber sunset)
    // =========================================================================
    private static final Color EVENING_TOP    = new Color(0xC0392B, false);
    private static final Color EVENING_BOTTOM = new Color(0xF39C12, false);
    private static final Color EVENING_TEXT   = new Color(0x2C0B0B, false);

    // =========================================================================
    // Night theme (deep navy sky)
    // =========================================================================
    private static final Color NIGHT_TOP    = new Color(0x0A0E27, false);
    private static final Color NIGHT_BOTTOM = new Color(0x1A2353, false);
    private static final Color NIGHT_TEXT   = new Color(0xD4E5FF, false);

    // Shared muted text colour used for labels and secondary values
    private static final Color MUTED_LIGHT = new Color(255, 255, 255, 180);
    private static final Color MUTED_DARK  = new Color(0, 0, 0, 120);

    // Current active period
    private Period currentPeriod;

    /**
     * Constructs a ThemeManager and sets the theme based on the current time.
     */
    public ThemeManager() {
        this.currentPeriod = TimeOfDayUtil.getCurrentPeriod();
    }

    /**
     * Forces a specific period (useful for testing or manual override).
     *
     * @param period the period to activate
     */
    public void setPeriod(Period period) {
        this.currentPeriod = period;
    }

    /** @return the active time-of-day period */
    public Period getCurrentPeriod() {
        return currentPeriod;
    }

    /**
     * Returns the gradient top colour for the current period.
     *
     * @return top gradient stop colour
     */
    public Color getGradientTop() {
        switch (currentPeriod) {
            case MORNING:   return MORNING_TOP;
            case AFTERNOON: return AFTERNOON_TOP;
            case EVENING:   return EVENING_TOP;
            default:        return NIGHT_TOP;
        }
    }

    /**
     * Returns the gradient bottom colour for the current period.
     *
     * @return bottom gradient stop colour
     */
    public Color getGradientBottom() {
        switch (currentPeriod) {
            case MORNING:   return MORNING_BOTTOM;
            case AFTERNOON: return AFTERNOON_BOTTOM;
            case EVENING:   return EVENING_BOTTOM;
            default:        return NIGHT_BOTTOM;
        }
    }

    /**
     * Returns the primary text colour suitable for this period's background.
     *
     * @return foreground text colour
     */
    public Color getPrimaryTextColor() {
        switch (currentPeriod) {
            case MORNING:   return MORNING_TEXT;
            case AFTERNOON: return AFTERNOON_TEXT;
            case EVENING:   return EVENING_TEXT;
            default:        return NIGHT_TEXT;
        }
    }

    /**
     * Returns a semi-transparent muted colour for secondary labels.
     *
     * @return muted text colour
     */
    public Color getMutedTextColor() {
        return (currentPeriod == Period.NIGHT) ? MUTED_LIGHT : MUTED_DARK;
    }

    /**
     * Returns a semi-transparent white or dark colour for card backgrounds.
     *
     * @return card background colour
     */
    public Color getCardBackground() {
        if (currentPeriod == Period.NIGHT) {
            return new Color(255, 255, 255, 25);
        }
        return new Color(255, 255, 255, 60);
    }

    /**
     * Returns a contrasting card border colour.
     *
     * @return border colour
     */
    public Color getCardBorder() {
        if (currentPeriod == Period.NIGHT) {
            return new Color(255, 255, 255, 40);
        }
        return new Color(255, 255, 255, 100);
    }

    /**
     * Creates and returns a {@link GradientPanel} using the current theme colours.
     *
     * @return a new GradientPanel configured with the active gradient
     */
    public GradientPanel createGradientPanel() {
        return new GradientPanel(getGradientTop(), getGradientBottom());
    }

    // =========================================================================
    // Inner class: GradientPanel
    // =========================================================================

    /**
     * A JPanel that paints itself with a vertical linear gradient background.
     * Used as the main content area of the application window.
     */
    public static class GradientPanel extends JPanel {

        private Color topColor;
        private Color bottomColor;

        /**
         * Constructs a GradientPanel with the given gradient stop colours.
         *
         * @param topColor    colour at the top of the panel
         * @param bottomColor colour at the bottom of the panel
         */
        public GradientPanel(Color topColor, Color bottomColor) {
            this.topColor    = topColor;
            this.bottomColor = bottomColor;
            setOpaque(false);
        }

        /**
         * Updates the gradient colours and triggers a repaint.
         *
         * @param topColor    new top colour
         * @param bottomColor new bottom colour
         */
        public void updateGradient(Color topColor, Color bottomColor) {
            this.topColor    = topColor;
            this.bottomColor = bottomColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    0, getHeight(), bottomColor
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
