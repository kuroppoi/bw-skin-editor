package io.github.kuroppoi.bwse.gui.icon;

import java.awt.Component;
import java.awt.Graphics2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class FlipIcon extends AbstractIcon {
    
    private final boolean vertical;
    
    public FlipIcon() {
        this(false);
    }
    
    public FlipIcon(boolean vertical) {
        this.vertical = vertical;
    }
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        
        if(vertical) {
            g2d.translate(0, height);
            g2d.scale(1, -1);
            g2d.rotate(Math.toRadians(-90), 8, 8);
        }
        
        g2d.draw(FlatUIUtils.createPath(1, 13, 7, 13, 7, 2));
        g2d.fill(FlatUIUtils.createPath(9, 14, 15, 14, 9, 1));
    }
}
