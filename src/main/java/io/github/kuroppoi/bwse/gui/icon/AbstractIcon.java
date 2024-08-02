package io.github.kuroppoi.bwse.gui.icon;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

import javax.swing.UIManager;

import com.formdev.flatlaf.icons.FlatAbstractIcon;

public abstract class AbstractIcon extends FlatAbstractIcon {

    public AbstractIcon() {
        super(16, 16, UIManager.getColor("Actions.Grey"));
    }
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        if(!component.isEnabled()) {
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
            g2d.setComposite(AlphaComposite.Src);
        }
    }
}
