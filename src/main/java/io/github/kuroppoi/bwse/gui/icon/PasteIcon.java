package io.github.kuroppoi.bwse.gui.icon;

import java.awt.Component;
import java.awt.Graphics2D;

public class PasteIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        g2d.fillRect(2, 2, 4, 3);
        g2d.fillRect(10, 2, 4, 3);
        g2d.fillRect(6, 4, 4, 1);
        g2d.fillRect(5, 1, 6, 1);
        g2d.fillRect(2, 5, 2, 10);
        g2d.fillRect(12, 5, 2, 10);
        g2d.fillRect(4, 13, 8, 2);
    }
}
