package io.github.kuroppoi.bwse.gui.icon;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class CurvedArrowIcon extends AbstractIcon {
    
    private boolean flipped;
    
    public CurvedArrowIcon() {
        this(false);
    }
    
    public CurvedArrowIcon(boolean flipped) {
        this.flipped = flipped;
    }
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        
        if(flipped) {
            g2d.translate(width, 0);
            g2d.scale(-1, 1);
        }
        
        g2d.setStroke(new BasicStroke(2.5F));
        g2d.drawArc(2, 3, 12, 12, 0, 180);
        g2d.fill(FlatUIUtils.createPath(0, 4, 7, 9, 0, 12));
    }
}
