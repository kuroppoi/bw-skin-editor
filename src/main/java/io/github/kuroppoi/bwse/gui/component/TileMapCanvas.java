package io.github.kuroppoi.bwse.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import io.github.kuroppoi.bwse.graphics.PaletteTable;
import io.github.kuroppoi.bwse.graphics.Tile;
import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.graphics.TileRenderer;
import io.github.kuroppoi.bwse.graphics.TileSet;
import io.github.kuroppoi.bwse.gui.model.TileInfo;
import io.github.kuroppoi.bwse.gui.model.TileSelection;
import io.github.kuroppoi.bwse.util.SwingUtils;

@SuppressWarnings("serial")
public class TileMapCanvas extends ImageCanvas {
    
    private static TileSelection clipboard; // Kinda shit but it does the job :)
    private final String[] paletteNames = new String[PaletteTable.PALETTE_COUNT];
    private final KeyStroke copyKeyStroke;
    private final KeyStroke pasteKeyStroke;
    private final KeyStroke deleteKeyStroke;
    private final Action copyTilesAction;
    private final Action pasteTilesAction;
    private final Action deleteTilesAction;
    private final Action horizontalFlipAction;
    private final Action verticalFlipAction;
    private final Action deselectAction;
    private int tilesWidth;
    private int tilesHeight;
    private TileSet tileSet;
    private PaletteTable paletteTable;
    private TileMap tileMap;
    private Point tileHoverPoint;
    private Rectangle tileSelectionRect;
    private Rectangle pixelSelectionRect;
    private TileSelection tileSelection;
    private Consumer<TileInfo> tileHoverListener;
    private boolean readOnly;
    
    public TileMapCanvas() {
        copyTilesAction = SwingUtils.createAction("Copy Tiles", () -> clipboard = tileSelection);
        pasteTilesAction = SwingUtils.createAction("Paste Tiles", this::pasteTiles);
        deleteTilesAction = SwingUtils.createAction("Delete Tiles", this::deleteSelectedTiles);
        horizontalFlipAction = SwingUtils.createAction("Flip Horizontally", () -> flipSelectedTiles(true, false));
        verticalFlipAction = SwingUtils.createAction("Flip Vertically", () -> flipSelectedTiles(false, true));
        deselectAction = SwingUtils.createAction("Deselect", this::clearTileSelection);
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        copyKeyStroke = KeyStroke.getKeyStroke('C', shortcutMask);
        pasteKeyStroke = KeyStroke.getKeyStroke('V', shortcutMask);
        deleteKeyStroke = KeyStroke.getKeyStroke('D', shortcutMask);
        registerKeyboardAction(copyTilesAction, copyKeyStroke, WHEN_FOCUSED);
        registerKeyboardAction(pasteTilesAction, pasteKeyStroke, WHEN_FOCUSED);
        registerKeyboardAction(deleteTilesAction, deleteKeyStroke, WHEN_FOCUSED);
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D)graphics;
        
        // Draw selection area
        if(tileSelectionRect != null) {
            Rectangle rectangle = getDrawSpaceRect(pixelSelectionRect);
            g2d.setColor(new Color(255, 0, 0, 80));
            g2d.fill(rectangle);
            g2d.setColor(Color.RED);
            g2d.draw(rectangle);
        }
        
        // Draw hover rect
        if(tileHoverPoint != null) {
            Rectangle rectangle = getDrawSpaceRect(new Rectangle(tileHoverPoint.x * Tile.WIDTH, tileHoverPoint.y * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT));
            g2d.setColor(Color.RED);
            g2d.draw(rectangle);
        }
    }
    
    @Override
    public void onRightClick(Point point) {
        if(tileHoverPoint == null) {
            return;
        }
        
        int x = tileHoverPoint.x;
        int y = tileHoverPoint.y;
        
        if(tileSelectionRect == null || !tileSelectionRect.contains(tileHoverPoint)) {
            tileSelectionRect = new Rectangle(x, y, 1, 1);
            pixelSelectionRect = new Rectangle(x * Tile.WIDTH, y * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT);
            updateTileSelection();
            repaint();
        }
        
        // Update action states
        copyTilesAction.setEnabled(tileSelection != null);
        deleteTilesAction.setEnabled(tileSelection != null && !readOnly);
        horizontalFlipAction.setEnabled(tileSelection != null && !readOnly);
        verticalFlipAction.setEnabled(tileSelection != null && !readOnly);
        pasteTilesAction.setEnabled(clipboard != null && !readOnly);
        
        // Create context menu
        JPopupMenu menu = new JPopupMenu("Edit");
        menu.add(copyTilesAction).setAccelerator(copyKeyStroke);
        menu.add(pasteTilesAction).setAccelerator(pasteKeyStroke);
        menu.add(deleteTilesAction).setAccelerator(deleteKeyStroke);
        menu.addSeparator();
        menu.add(horizontalFlipAction);
        menu.add(verticalFlipAction);
        menu.addSeparator();
        JMenu paletteMenu = new JMenu("Set Palette");
        paletteMenu.setEnabled(tileSelection != null && !readOnly);
        
        for(int i = 0; i < PaletteTable.PALETTE_COUNT; i++) {
            int paletteIndex = i;
            String name = paletteNames[i] == null ? "Unknown Palette" : paletteNames[i];
            paletteMenu.add(SwingUtils.createAction("%s - %s".formatted(i, name), () -> setSelectedTilesPalette(paletteIndex)));
        }
        
        menu.add(paletteMenu);
        menu.addSeparator();
        menu.add(deselectAction);
        menu.show(this, point.x, point.y);
    }
    
    @Override
    public void onPixelClicked(Point pixelPoint) {        
        if(pixelPoint == null) {
            onPixelSelectionChanged(null);
            return;
        }
        
        int x = pixelPoint.x / Tile.WIDTH;
        int y = pixelPoint.y / Tile.HEIGHT;
        tileSelectionRect = new Rectangle(x, y, 1, 1);
        pixelSelectionRect = new Rectangle(x * Tile.WIDTH, y * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT);
        updateTileSelection();
        repaint();
    }
    
    @Override
    protected void onPixelHover(Point pixelPoint) {
        tileHoverPoint = pixelPoint == null ? null : new Point(pixelPoint.x / Tile.WIDTH, pixelPoint.y / Tile.HEIGHT);
        
        if(tileHoverListener != null) {
            if(tileHoverPoint != null) {
                int x = tileHoverPoint.x;
                int y = tileHoverPoint.y;
                int index = getTileIndex(x, y);
                TileInfo info = new TileInfo(x, y, index, tileMap);
                tileHoverListener.accept(info);
            } else {
                tileHoverListener.accept(null);
            }
        }
        
        repaint();
    }
    
    @Override
    protected void onPixelSelectionChanged(Rectangle pixelArea) {
        if(pixelArea == null) {
            tileSelectionRect = null;
            pixelSelectionRect = null;
            repaint();
            return;
        }
        
        int x = pixelArea.x / Tile.WIDTH;
        int y = pixelArea.y / Tile.HEIGHT;
        int pixelX = x * Tile.WIDTH;
        int pixelY = y * Tile.HEIGHT;
        int offsetX = pixelArea.x - pixelX;
        int offsetY = pixelArea.y - pixelY;
        int width = Math.min(tilesWidth - x, (pixelArea.width + offsetX) / Tile.WIDTH + 1);
        int height = Math.min(tilesHeight - y, (pixelArea.height + offsetY) / Tile.HEIGHT + 1);
        tileSelectionRect = new Rectangle(x, y, width, height);
        pixelSelectionRect = new Rectangle(pixelX, pixelY, width * Tile.WIDTH, height * Tile.HEIGHT);
        repaint();
    }
    
    @Override
    protected void onPixelSelection(Rectangle pixelArea) {
        if(pixelArea == null) {
            tileSelectionRect = null;
            pixelSelectionRect = null;
        }
        
        updateTileSelection();
    }
    
    @Override
    public void setImage(BufferedImage image) {
        throw new UnsupportedOperationException();
    }
    
    public void setImage(int tilesWidth, int tilesHeight, TileSet tileSet, PaletteTable paletteTable, TileMap tileMap) {
        this.tilesWidth = tilesWidth;
        this.tilesHeight = tilesHeight;
        this.tileSet = tileSet;
        this.paletteTable = paletteTable;
        this.tileMap = tileMap;
        updateImage();
    }
    
    public void updateImage() {
        super.setImage(TileRenderer.drawTiles(tilesWidth * Tile.WIDTH, tilesHeight * Tile.HEIGHT, tileSet, tileMap, paletteTable));
    }
    
    private void updateTileSelection() {
        if(tileSelectionRect == null) {
            tileSelection = null;
            return;
        }
        
        List<Integer> indices = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        int x = tileSelectionRect.x;
        int y = tileSelectionRect.y;
        int width = tileSelectionRect.width;
        int height = tileSelectionRect.height;
        
        for(int j = y; j < y + height; j++) {
            for(int i = x; i < x + width; i++) {
                int index = getTileIndex(i, j);
                indices.add(index);
                values.add(tileMap.getIndex(index));
            }
        }
        
        tileSelection = new TileSelection(x, y, width, height, indices, values);
    }
    
    private void pasteTiles() {
        if(readOnly || clipboard == null) {
            return;
        }
        
        int x = tileSelection.x();
        int y = tileSelection.y();
        int width = Math.min(clipboard.width(), tilesWidth - x);
        int height = Math.min(clipboard.height(), tilesHeight - y);
        
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                tileMap.setIndex(getTileIndex(x + i, y + j), clipboard.getValue((j * clipboard.width() + i)));
            }
        }
        
        updateTileSelection();
        updateImage();
    }
    
    private void deleteSelectedTiles() {
        if(readOnly || tileSelection == null) {
            return;
        }
        
        for(int i = 0; i < tileSelection.getIndexCount(); i++) {
            tileMap.setIndex(tileSelection.getIndex(i), 0);
        }
        
        clearTileSelection();
        updateImage();
    }
    
    private void flipSelectedTiles(boolean flipX, boolean flipY) {
        if(readOnly || tileSelection == null) {
            return;
        }
        
        int width = tileSelection.width();
        int height = tileSelection.height();
        
        for(int i = 0; i < tileSelection.getIndexCount(); i++) {
            int x = i % width;
            int y = i / width;
            
            if(flipX) {
                x = width - 1 - x;
            }
            
            if(flipY) {
                y = height - 1 - y;
            }
            
            int index = tileSelection.getIndex(y * width + x);
            tileMap.setIndex(index, tileSelection.getValue(i));
            
            if(flipX) {
                tileMap.setFlipX(index, !tileMap.getFlipX(index));
            }
            
            if(flipY) {
                tileMap.setFlipY(index, !tileMap.getFlipY(index));
            }
        }
        
        updateTileSelection();
        updateImage();
    }
    
    private void setSelectedTilesPalette(int paletteIndex) {
        if(readOnly || tileSelection == null) {
            return;
        }
        
        for(int index : tileSelection.indices()) {
            tileMap.setPaletteIndex(index, paletteIndex);
        }
        
        updateTileSelection();
        updateImage();
    }
    
    private void clearTileSelection() {
        tileSelectionRect = null;
        updateTileSelection();
        repaint();
    }
    
    private int getTileIndex(int x, int y) {
        return y * tilesWidth + x;
    }
    
    public void setPaletteName(int index, String name) {
        paletteNames[index] = name;
    }
    
    public void setTileHoverListener(Consumer<TileInfo> tileHoverListener) {
        this.tileHoverListener = tileHoverListener;
    }
    
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    public boolean isReadOnly() {
        return readOnly;
    }
}
