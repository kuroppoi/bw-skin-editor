package io.github.kuroppoi.bwse.gui.component;

import java.awt.Point;
import java.awt.image.BufferedImage;

import io.github.kuroppoi.bwse.graphics.CGearRenderer;

@SuppressWarnings("serial")
public class CGearCanvas extends ImageCanvas {
    
    private final CGearRenderer renderer;
    private Runnable changeListener;
    private boolean dirty;
    
    public CGearCanvas(CGearRenderer renderer) {
        this.renderer = renderer;
        updateImage();
    }
    
    @Override
    protected void onPixelClicked(Point pixelPoint) {        
        int x = pixelPoint.x;
        int y = pixelPoint.y;
        
        if(x >= 26 && x <= 42 && y >= 162 && y <= 178) {
            // Survey radar bounds
            renderer.toggleColorScheme();
            dirty = true;
        } else if(x >= 99 && x <= 153 && y >= 16 && y <= 26) {
            // C-Gear logo bounds
            renderer.cycleButtonStyle();
            dirty = true;
        }
        
        int button = renderer.getButtonIndex(x, y);
        
        if(button != -1) {
            renderer.cycleButtonType(button);
            dirty = true;
        }
        
        if(dirty && changeListener != null) {
            changeListener.run();
        }
        
        // Update image ourselves if still dirty
        if(dirty) {
            updateImage();
        }
    }
    
    @Override
    public void setImage(BufferedImage image) {
        throw new UnsupportedOperationException();
    }
    
    public void updateImage() {
        super.setImage(renderer.drawCGear());
        dirty = false;
    }
    
    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }
}
