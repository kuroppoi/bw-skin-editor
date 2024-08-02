package io.github.kuroppoi.bwse.gui.icon;

import java.awt.Component;
import java.awt.Graphics2D;

public class CopyIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        
        // Back paper
        g2d.fillRect(2, 1, 10, 2);
        g2d.fillRect(2, 3, 2, 9);
        
        // Front paper
        g2d.fillRect(5, 4, 2, 11);
        g2d.fillRect(13, 4, 2, 11);
        g2d.fillRect(7, 4, 6, 3);
        g2d.fillRect(7, 12, 6, 3);
        g2d.fillRect(7, 8, 6, 1);
        g2d.fillRect(7, 10, 6, 1);
    }
}
