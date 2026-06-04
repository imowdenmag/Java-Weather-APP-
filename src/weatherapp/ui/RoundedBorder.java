package weatherapp.ui;

import javax.swing.border.Border;
import java.awt.*;

/**
 * A rounded-rectangle border for Swing components.
 *
 * @param color     border stroke colour
 * @param thickness stroke width in pixels
 * @param radius    arc radius for the rounded corners
 */
public class RoundedBorder implements Border {

    private final Color color;
    private final int   thickness;
    private final int   radius;

    public RoundedBorder(Color color, int thickness, int radius) {
        this.color     = color;
        this.thickness = thickness;
        this.radius    = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int pad = radius / 4 + thickness;
        return new Insets(pad, pad, pad, pad);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
