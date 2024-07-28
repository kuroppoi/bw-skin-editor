package io.github.kuroppoi.bwse.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TileRenderer {
    
    private static final BufferedImage TILE_IMAGE = new BufferedImage(Tile.WIDTH, Tile.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    
    public static BufferedImage drawTiles(int width, int height, TileSet tileSet, TileMap tileMap, PaletteTable paletteTable) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.createGraphics();
        drawTiles(graphics, 0, 0, width, height, tileSet, tileMap, paletteTable);
        return image;
    }
    
    public static void drawTiles(Graphics graphics, int x, int y, int width, int height, TileSet tileSet, TileMap tileMap, PaletteTable paletteTable) {
        for(int i = 0; i < tileMap.getIndexCount(); i++) {
            Tile tile = tileSet.getTile(tileMap.getTileIndex(i));
            
            if(tile == null) {
                continue;
            }
            
            int tileX = x + i * Tile.WIDTH % width;
            int tileY = y + i * Tile.WIDTH / width * Tile.HEIGHT;
            boolean flipX = tileMap.getFlipX(i);
            boolean flipY = tileMap.getFlipY(i);
            Palette palette = paletteTable.getPalette(tileMap.getPaletteIndex(i));
            drawTile(graphics, tileX, tileY, tile, flipX, flipY, palette);
        }
    }
    
    public static void drawTile(Graphics graphics, int x, int y, Tile tile, boolean flipX, boolean flipY, Palette palette) {
        drawTile(TILE_IMAGE, tile, flipX, flipY, palette);
        graphics.drawImage(TILE_IMAGE, x, y, null);
    }
    
    public static BufferedImage drawTile(Tile tile, boolean flipX, boolean flipY, Palette palette) {
        BufferedImage image = new BufferedImage(Tile.WIDTH, Tile.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        drawTile(image, tile, flipX, flipY, palette);
        return image;
    }
    
    private static void drawTile(BufferedImage output, Tile tile, boolean flipX, boolean flipY, Palette palette) {
        for(int i = 0; i < Tile.SIZE; i++) {
            int pixelX = i % Tile.WIDTH;
            int pixelY = i / Tile.WIDTH;
            int outputX = pixelX;
            int outputY = pixelY;
            
            if(flipX) {
                pixelX = Tile.WIDTH - 1 - pixelX; // Flip horizontally
            }
            
            if(flipY) {
                pixelY = Tile.HEIGHT - 1 - pixelY; // Flip vertically
            }
                
            int pixel = tile.getPixel(pixelY * Tile.WIDTH + pixelX);
            int color = palette.getColor(pixel);
            
            if(pixel != 0) {
                color |= 0xFF000000;
            } else {
                color = 0; // Treat color at index 0 as transparent
            }
            
            output.setRGB(outputX, outputY, color);
        }
    }
}
