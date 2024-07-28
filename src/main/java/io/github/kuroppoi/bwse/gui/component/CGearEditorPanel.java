package io.github.kuroppoi.bwse.gui.component;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import io.github.kuroppoi.bwse.graphics.CGearRenderer;
import io.github.kuroppoi.bwse.graphics.Palette;
import io.github.kuroppoi.bwse.graphics.Tile;
import io.github.kuroppoi.bwse.graphics.TileImage;
import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.graphics.TileSet;
import io.github.kuroppoi.bwse.gui.model.TileInfo;

@SuppressWarnings("serial")
public class CGearEditorPanel extends JPanel {
    
    private final CGearRenderer renderer = new CGearRenderer();
    private final TileMapCanvas skinPanel;
    private final TileMapCanvas tilePanel;
    private final CGearCanvas previewPanel;
    private final JScrollPane skinScrollPane;
    private boolean previewMode;
    
    public CGearEditorPanel() {        
        // Skin panel
        skinPanel = new TileMapCanvas();
        skinPanel.setPaletteName(0, "Background Palette");
        skinPanel.setPaletteName(1, "Button Palette (Bright)");
        skinPanel.setPaletteName(2, "Button Palette (Less bright)");
        skinPanel.setPaletteName(3, "Button Palette (Dark)");
        skinPanel.setPaletteName(4, "Button Palette (Darker)");
        skinPanel.setPaletteName(5, "Button Palette (Darkest)");
        skinPanel.setPaletteName(7, "Button Palette (Off)");
        skinPanel.setPaletteName(8, "Top Bar Palette");
        skinPanel.setPaletteName(CGearRenderer.CGEAR_SKIN_PALETTE_INDEX, "Skin Palette");
        skinPanel.setImage(32, 24, renderer.getTileSet(), renderer.getPaletteTable(), renderer.getSkinMap());
        skinPanel.setScale(2);
         
        // Tileset panel
        tilePanel = new TileMapCanvas();
        tilePanel.setImage(32, 32, renderer.getTileSet(), renderer.getPaletteTable(), createTileSetMap());
        tilePanel.setReadOnly(true);
        
        // Preview panel
        previewPanel = new CGearCanvas(renderer);
        previewPanel.setScale(2);
        previewPanel.setChangeListener(this::updateImages);
        
        // Create center panel
        skinScrollPane = new JScrollPane(skinPanel);
        skinScrollPane.setBorder(BorderFactory.createTitledBorder("C-Gear Skin"));
        JScrollPane tileScrollPane = new JScrollPane(tilePanel);
        tileScrollPane.setBorder(BorderFactory.createTitledBorder("Tileset (Read-only)"));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.add(skinScrollPane);
        splitPane.add(tileScrollPane);
        
        // Create info footer
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel tileInfoLabel = new JLabel("Hover over a tile to view its properties.", JLabel.CENTER);
        Consumer<TileInfo> tileHoverListener = info -> tileInfoLabel.setText(info == null ? "Hover over a tile to view its properties." : info.toString());
        skinPanel.setTileHoverListener(tileHoverListener);
        tilePanel.setTileHoverListener(tileHoverListener);
        String shortcutKeyName = InputEvent.getModifiersExText(Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        infoPanel.add(new JLabel("Adjust zoom level with %s + Mouse Wheel".formatted(shortcutKeyName)), BorderLayout.LINE_START);
        infoPanel.add(tileInfoLabel);
        
        // Add components
        setLayout(new BorderLayout());
        add(splitPane);
        add(infoPanel, BorderLayout.PAGE_END);
    }
    
    private void updateImages() {
        tilePanel.updateImage();
        skinPanel.updateImage();
        previewPanel.updateImage();
    }
    
    public void togglePreview() {
        previewMode = !previewMode;
        skinScrollPane.setViewportView(previewMode ? previewPanel : skinPanel);
        
        if(previewMode) {
            previewPanel.updateImage();
        }
    }
    
    public void toggleColorScheme() {
        renderer.toggleColorScheme();
        updateImages();
    }
    
    public void cycleButtonStyle() {
        renderer.cycleButtonStyle();
        updateImages();
    }
    
    public void setSkin(TileImage skin, boolean transform) {
        renderer.setSkin(skin, transform);
        updateImages();
    }
    
    public TileImage getSkin() {
        return renderer.getSkin();
    }
    
    public void setSkinTiles(Tile[] tiles) {
        renderer.setSkinTiles(tiles);
        updateImages();
    }
    
    public void setSkinPalette(Palette palette) {
        renderer.setSkinPalette(palette);
        updateImages();
    }
    
    public BufferedImage drawCGearSkin() {
        return renderer.drawCGearSkin();
    }
    
    public BufferedImage drawCGearSkinTiles() {
        return renderer.drawCGearSkinTiles();
    }
    
    private static TileMap createTileSetMap() {
        TileMap tileMap = new TileMap(TileSet.TILE_COUNT);
        
        for(int i = 0; i < tileMap.getIndexCount(); i++) {
            tileMap.setIndex(i, i);
        }
        
        // Top bar tiles
        tileMap.setPaletteIndex(10, 8);
        tileMap.setPaletteIndex(11, 8);
        tileMap.setPaletteIndex(42, 8);
        tileMap.setPaletteIndex(43, 8);

        // Button tiles
        for(int index : CGearRenderer.CGEAR_BUTTON_TILE_INDICES) {
            tileMap.setPaletteIndex(index, 1);
        }
        
        // Skin tiles
        for(int index : CGearRenderer.CGEAR_SKIN_TILE_INDICES) {
            tileMap.setPaletteIndex(index, CGearRenderer.CGEAR_SKIN_PALETTE_INDEX);
        }
        
        return tileMap;
    }
}
