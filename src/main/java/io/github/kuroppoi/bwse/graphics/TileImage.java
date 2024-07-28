package io.github.kuroppoi.bwse.graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TileImage {
    
    private final Tile[] tiles;
    private final Palette[] palettes;
    private final TileMap tileMap;
    
    public TileImage(Tile[] tiles, Palette palette, TileMap tileMap) {
        this(tiles, new Palette[] { palette }, tileMap);
    }
    
    public TileImage(Tile[] tiles, Palette[] palettes, TileMap tileMap) {
        this.tiles = tiles;
        this.palettes = palettes;
        this.tileMap = tileMap;
    }
    
    public static TileImage fromImage(BufferedImage image, boolean optimizeTiles) {
        List<Tile> tileList = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        int tileCount = width * height / Tile.SIZE;
        Palette palette = new Palette();
        TileMap tileMap = new TileMap(tileCount);
        
        // Chop image into a list of tiles
        for(int i = 0; i < tileCount; i++) {
            int tileX = i * Tile.WIDTH % width;
            int tileY = i * Tile.WIDTH / width * Tile.HEIGHT;
            Tile tile = new Tile();
            
            for(int j = 0; j < Tile.SIZE; j++) {
                int pixelX = tileX + j % Tile.WIDTH;
                int pixelY = tileY + j / Tile.WIDTH;
                int color = image.getRGB(pixelX, pixelY) & 0xFFFFFF;
                int colorIndex = palette.tryAddColor(color);
                tile.setPixel(j, colorIndex);
            }
            
            int tileIndex = tileList.size();
            int flipBits = -1;
            
            if(optimizeTiles) {
                for(int j = 0; j < tileList.size(); j++) {
                    flipBits = tile.getFlipBits(tileList.get(j));
                    
                    if(flipBits != -1) {
                        // Found a flippable tile!
                        tileIndex = j;
                        break;
                    }
                }
            }
            
            if(flipBits == -1) {
                tileList.add(tile);
                tileMap.setIndex(i, tileIndex, 0, 0);
            } else {
                tileMap.setIndex(i, tileIndex, flipBits, 0);
            }
        }
        
        Tile[] tiles = new Tile[tileList.size()];
        tileList.toArray(tiles);
        return new TileImage(tiles, palette, tileMap);
    }
    
    public int getTileCount() {
        return tiles.length;
    }
    
    public Tile[] getTiles() {
        return tiles;
    }
    
    public int getPaletteCount() {
        return palettes.length;
    }
    
    public Palette getPalette() {
        return palettes[0];
    }
    
    public Palette[] getPalettes() {
        return palettes;
    }
    
    public TileMap getTileMap() {
        return tileMap;
    }
}
