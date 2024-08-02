package io.github.kuroppoi.bwse.gui.icon;

import java.awt.Component;
import java.awt.Graphics2D;

public class DeleteIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        g2d.fillRect(1, 2, 13, 2);
        g2d.fillRect(4, 1, 7, 1);
        g2d.fillRect(2, 4, 11, 1);
        g2d.fillRect(2, 5, 2, 8);
        g2d.fillRect(5, 5, 2, 8);
        g2d.fillRect(8, 5, 2, 8);
        g2d.fillRect(11, 5, 2, 8);
        g2d.fillRect(2, 13, 11, 2);
    }
}
