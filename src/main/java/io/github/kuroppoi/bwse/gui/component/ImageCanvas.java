package io.github.kuroppoi.bwse.gui.component;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import io.github.kuroppoi.bwse.gui.ActionManager;
import io.github.kuroppoi.bwse.gui.ActionTarget;

@SuppressWarnings("serial")
public class ImageCanvas extends JComponent implements ActionTarget {
    
    public static final int MAX_SCALE = 10;
    private BufferedImage image;
    private int scale = 1;
    private Point viewDragPoint;
    private Point pixelDragPoint;
    private Rectangle pixelSelectionRect;
    
    public ImageCanvas() {        
        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(SwingUtilities.isLeftMouseButton(event)) {
                    Point point = getPixelPoint(event.getPoint());
                    onPixelClicked(isPointInImage(point) ? point : null);
                } else if(SwingUtilities.isRightMouseButton(event)) {
                    onRightClick(event.getPoint());
                }
            }
            
            @Override
            public void mousePressed(MouseEvent event) {
                ActionManager.setActionTarget(ImageCanvas.this);
                Point point = event.getPoint();
                
                if(SwingUtilities.isMiddleMouseButton(event)) {
                    viewDragPoint = new Point(point);
                }
                
                if(SwingUtilities.isLeftMouseButton(event)) {
                    pixelDragPoint = getPixelPoint(point);
                    
                    // Clear pixel selection if a point outside of the image was clicked
                    if(!isPointInImage(pixelDragPoint)) {
                        pixelSelectionRect = null;
                        onPixelSelectionChanged(pixelSelectionRect);
                    } else {
                        pixelSelectionRect = new Rectangle(pixelDragPoint.x, pixelDragPoint.y, 0, 0);
                        onPixelSelectionChanged(pixelSelectionRect);
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent event) {
                 onPixelHover(null);
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                if(SwingUtilities.isMiddleMouseButton(event)) {
                    return;
                }
                
                if(!event.isControlDown()) {
                    Container parent = getParent();
                    
                    if(parent != null) {
                        parent.dispatchEvent(event);
                    }
                    
                    return;
                }
                
                int nextScale = Math.max(1, Math.min(MAX_SCALE, scale - (int)event.getPreciseWheelRotation()));
                
                if(nextScale == scale) {
                    return;
                }
                
                Point point = event.getPoint();
                double factor = nextScale / (double)scale;
                int offsetX = (int)(point.x * factor) - point.x;
                int offsetY = (int)(point.y * factor) - point.y;
                setLocation(getX() - offsetX, getY() - offsetY);
                setScale(nextScale);
            }
            
            @Override
            public void mouseDragged(MouseEvent event) {
                if(SwingUtilities.isLeftMouseButton(event)) {
                    if(!isPointInImage(pixelDragPoint)) {
                        return;
                    }
                                        
                    pixelSelectionRect = getPixelSelectionRect(pixelDragPoint, getPixelPoint(event.getPoint()));
                    scrollRectToVisible(new Rectangle(event.getPoint()));
                    onPixelHover(null);
                    onPixelSelectionChanged(pixelSelectionRect);
                } else if(SwingUtilities.isMiddleMouseButton(event)) {
                    JViewport viewport = (JViewport)SwingUtilities.getAncestorOfClass(JViewport.class, ImageCanvas.this);
                    
                    if(viewport == null) {
                        return;
                    }
                    
                    Rectangle view = viewport.getViewRect();
                    int deltaX = viewDragPoint.x - event.getX();
                    int deltaY = viewDragPoint.y - event.getY();
                    view.x += deltaX;
                    view.y += deltaY;
                    scrollRectToVisible(view);
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent event) {
                Point point = getPixelPoint(event.getPoint());
                
                if(!isPointInImage(point)) {
                    point = null;
                }
                
                onPixelHover(point);
            }
        };
        
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        if(image == null) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D)graphics;
        Rectangle drawBounds = getDrawBounds();
        g2d.clip(drawBounds);
        
        // Draw transparency grid
        for(int x = 0; x < getWidth(); x += 8) {
            for(int y = 0; y < getHeight(); y += 8) {
                Color color = ((x ^ y) & 8) == 0 ? Color.WHITE : Color.LIGHT_GRAY;
                g2d.setColor(color);
                g2d.fillRect(x - getX(), y - getY(), 8, 8);
            }
        }
        
        // Draw image
        g2d.drawImage(image, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height, null);
    }
    
    @Override
    public void onFocusGained() {
        requestFocusInWindow();
    }
    
    @Override
    public void onFocusLost() {
        if(isShowing()) {
            pixelSelectionRect = null;
            onPixelSelectionChanged(null);
        }
    }
    
    protected void onRightClick(Point point) {
        // Override
    }
    
    protected void onPixelClicked(Point pixelPoint) {
        // Override
    }
    
    protected void onPixelHover(Point pixelPoint) {
        // Override
    }
    
    protected void onPixelSelectionChanged(Rectangle pixelArea) {
        // Override
    }
    
    protected void updatePreferredSize() {
        if(image == null) {
            return;
        }
        
        setPreferredSize(new Dimension(image.getWidth() * scale, image.getHeight() * scale));
        Container parent = getParent();
        
        if(parent != null) {
            parent.doLayout();
        }
    }
    
    protected Rectangle getDrawSpaceRect(Rectangle rectangle) {
        Rectangle drawBounds = getDrawBounds();
        return new Rectangle(
            drawBounds.x + rectangle.x * scale,
            drawBounds.y + rectangle.y * scale,
            rectangle.width * scale - 1,
            rectangle.height * scale - 1
        );
    }
    
    protected Rectangle getDrawBounds() {
        if(image == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        
        int width = image.getWidth() * scale;
        int height = image.getHeight() * scale;
        int x = getWidth() / 2 - width / 2;
        int y = getHeight() / 2 - height / 2;
        return new Rectangle(x, y, width, height);
    }
    
    protected Point getPixelPoint(Point point) {
        Rectangle drawBounds = getDrawBounds();
        int x = (point.x - drawBounds.x) / scale;
        int y = (point.y - drawBounds.y) / scale;
        return new Point(x, y);
    }
    
    protected Rectangle getPixelSelectionRect(Point pointA, Point pointB) {
        Rectangle rectangle = new Rectangle(clampPointInImage(pointA));
        rectangle.add(clampPointInImage(pointB));
        return rectangle;
    }
    
    private Point clampPointInImage(Point point) {
        int x = Math.max(0, Math.min(image.getWidth(), point.x));
        int y = Math.max(0, Math.min(image.getHeight(), point.y));
        return new Point(x, y);
    }
    
    protected boolean isPointInImage(Point point) {
        return image != null && point != null && point.x >= 0 && point.x < image.getWidth() && point.y >= 0 && point.y < image.getHeight();
    }
    
    public void setImage(BufferedImage image) {
        boolean sizeChanged = this.image == null || (this.getImage().getWidth() != image.getWidth() || this.image.getHeight() != image.getHeight());
        this.image = image;
        
        if(sizeChanged) {
            updatePreferredSize();
        } else {
            repaint();
        }
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void setScale(int scale) {
        this.scale = Math.max(1, Math.min(MAX_SCALE, scale));
        updatePreferredSize();
    }
    
    public int getScale() {
        return scale;
    }
}
