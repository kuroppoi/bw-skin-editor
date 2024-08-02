package io.github.kuroppoi.bwse.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.undo.UndoManager;

import io.github.kuroppoi.bwse.graphics.PaletteTable;
import io.github.kuroppoi.bwse.graphics.Tile;
import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.graphics.TileRenderer;
import io.github.kuroppoi.bwse.graphics.TileSet;
import io.github.kuroppoi.bwse.gui.ActionManager;
import io.github.kuroppoi.bwse.gui.GuiConstants;
import io.github.kuroppoi.bwse.gui.edit.TileMapDeleteEdit;
import io.github.kuroppoi.bwse.gui.edit.TileMapEdit;
import io.github.kuroppoi.bwse.gui.edit.TileMapFlipEdit;
import io.github.kuroppoi.bwse.gui.edit.TileMapPaletteEdit;
import io.github.kuroppoi.bwse.gui.edit.TileMapPasteEdit;
import io.github.kuroppoi.bwse.gui.model.TileArea;
import io.github.kuroppoi.bwse.gui.model.TileInfo;
import io.github.kuroppoi.bwse.util.SwingUtils;

@SuppressWarnings("serial")
public class TileMapCanvas extends ImageCanvas {
    
    private static TileArea clipboard; // Kinda shit but it does the job :)
    private final UndoManager undoManager = new UndoManager();
    private final JPopupMenu popupMenu;
    private final JMenu paletteMenu;
    private int tilesWidth;
    private int tilesHeight;
    private TileSet tileSet;
    private PaletteTable paletteTable;
    private TileMap tileMap;
    private Point tileHoverPoint;
    private Rectangle tileSelection;
    private Consumer<TileInfo> tileHoverListener;
    private boolean readOnly;
    
    public TileMapCanvas() {
        // Create palette menu
        paletteMenu = new JMenu("Set Palette");
        
        for(int i = 0; i < PaletteTable.PALETTE_COUNT; i++) {
            int paletteIndex = i;
            paletteMenu.add(SwingUtils.createAction("%s - %s".formatted(i, GuiConstants.BW_PALETTE_NAMES.get(i)), () -> setTilesPalette(paletteIndex)));
        }
        
        // Create context menu
        popupMenu = new JPopupMenu();
        popupMenu.add(ActionManager.getAction(ActionManager.UNDO));
        popupMenu.add(ActionManager.getAction(ActionManager.REDO));
        popupMenu.addSeparator();
        popupMenu.add(ActionManager.getAction(ActionManager.COPY));
        popupMenu.add(ActionManager.getAction(ActionManager.PASTE));
        popupMenu.add(ActionManager.getAction(ActionManager.DELETE));
        popupMenu.addSeparator();
        popupMenu.add(ActionManager.getAction(ActionManager.FLIP_X));
        popupMenu.add(ActionManager.getAction(ActionManager.FLIP_Y));
        popupMenu.addSeparator();
        popupMenu.add(paletteMenu);
        popupMenu.addSeparator();
        popupMenu.add(ActionManager.getAction(ActionManager.SELECT_ALL));
        popupMenu.add(ActionManager.getAction(ActionManager.DESELECT));
        SwingUtils.fixDisabledIcons(popupMenu);
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D)graphics;
        
        // Draw selection area
        if(tileSelection != null) {
            Rectangle rectangle = getDrawSpaceRect(new Rectangle(tileSelection.x * Tile.WIDTH, tileSelection.y * Tile.HEIGHT, tileSelection.width * Tile.WIDTH, tileSelection.height * Tile.HEIGHT));
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
        
        if(tileSelection == null || !tileSelection.contains(tileHoverPoint)) {
            setTileSelection(new Rectangle(x, y, 1, 1));
            repaint();
        }
        
        // Show context menu
        paletteMenu.setEnabled(tileSelection != null && !readOnly);
        popupMenu.show(this, point.x, point.y);
    }
    
    @Override
    public void onPixelClicked(Point pixelPoint) {        
        if(pixelPoint == null) {
            onPixelSelectionChanged(null);
            return;
        }
        
        int x = pixelPoint.x / Tile.WIDTH;
        int y = pixelPoint.y / Tile.HEIGHT;
        setTileSelection(new Rectangle(x, y, 1, 1));
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
            setTileSelection(null);
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
        setTileSelection(new Rectangle(x, y, width, height));
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
    
    private void setTileSelection(Rectangle tileSelection) {
        this.tileSelection = tileSelection;
        updateActions();
        repaint();
    }
    
    public void updateActions() {
        if(!ActionManager.isActionTarget(this)) {
            return; // Do nothing if this component doesn't have focus
        }
        
        ActionManager.setActionEnabled(ActionManager.UNDO, undoManager.canUndo() && !readOnly);
        ActionManager.setActionEnabled(ActionManager.REDO, undoManager.canRedo() && !readOnly);
        ActionManager.setActionEnabled(ActionManager.COPY, tileSelection != null);
        ActionManager.setActionEnabled(ActionManager.PASTE, tileSelection != null && !readOnly);
        ActionManager.setActionEnabled(ActionManager.DELETE, tileSelection != null && !readOnly);
        ActionManager.setActionEnabled(ActionManager.FLIP_X, tileSelection != null && !readOnly);
        ActionManager.setActionEnabled(ActionManager.FLIP_Y, tileSelection != null && !readOnly);
        ActionManager.enableAction(ActionManager.SELECT_ALL);
        ActionManager.setActionEnabled(ActionManager.DESELECT, tileSelection != null);
    }
    
    public void undo() {
        if(!undoManager.canUndo()) {
            return;
        }
        
        undoManager.undo();
        updateActions();
        updateImage();
    }
    
    public void redo() {
        if(!undoManager.canRedo()) {
            return;
        }
        
        undoManager.redo();
        updateActions();
        updateImage();
    }
    
    public void clearEdits() {
        undoManager.discardAllEdits();
        updateActions();
    }
    
    private void performEdit(TileMapEdit edit) {
        edit.perform();
        undoManager.addEdit(edit);
        updateActions();
        updateImage();
    }
    
    public void copyTiles() {
        clipboard = TileArea.createTileArea(tileMap, tilesWidth, tilesHeight, tileSelection);
        updateActions();
    }
    
    public void pasteTiles() {
        if(readOnly || clipboard == null) {
            return;
        }
        
        TileArea pasteArea = TileArea.createTileArea(tileMap, tilesWidth, tilesHeight, tileSelection.x, tileSelection.y, clipboard.width(), clipboard.height());
        performEdit(new TileMapPasteEdit(tileMap, pasteArea, clipboard));
    }
    
    public void deleteTiles() {
        if(readOnly || tileSelection == null) {
            return;
        }
        
        TileArea targetArea = TileArea.createTileArea(tileMap, tilesWidth, tilesHeight, tileSelection);
        performEdit(new TileMapDeleteEdit(tileMap, targetArea));
        clearTileSelection();
    }
    
    public void flipTilesX() {
        flipTiles(true, false);
    }
    
    public void flipTilesY() {
        flipTiles(false, true);
    }
    
    public void flipTiles(boolean flipX, boolean flipY) {
        if(readOnly || tileSelection == null) {
            return;
        }
        
        TileArea targetArea = TileArea.createTileArea(tileMap, tilesWidth, tilesHeight, tileSelection);
        performEdit(new TileMapFlipEdit(tileMap, targetArea, flipX, flipY));
    }
    
    public void setTilesPalette(int paletteIndex) {
        if(readOnly || tileSelection == null) {
            return;
        }
        
        TileArea targetArea = TileArea.createTileArea(tileMap, tilesWidth, tilesHeight, tileSelection);
        performEdit(new TileMapPaletteEdit(tileMap, targetArea, paletteIndex));
    }
    
    public void selectAllTiles() {
        setTileSelection(new Rectangle(tilesWidth, tilesHeight));
    }
    
    public void clearTileSelection() {
        setTileSelection(null);
    }
    
    public boolean hasSelection() {
        return tileSelection != null;
    }
    
    private int getTileIndex(int x, int y) {
        return y * tilesWidth + x;
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
