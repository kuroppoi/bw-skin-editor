package io.github.kuroppoi.bwse.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.kuroppoi.bwse.graphics.Palette;
import io.github.kuroppoi.bwse.graphics.Tile;
import io.github.kuroppoi.bwse.graphics.TileImage;
import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.util.ColorUtils;

public class TileImageIO {
    
    public static TileImage readTileImage(InputStream inputStream, int tileCount, int paletteCount, int indexCount) throws IOException {
        return readTileImage(new LEInputStream(inputStream), tileCount, paletteCount, indexCount);
    }
    
    public static TileImage readTileImage(LEInputStream inputStream, int tileCount, int paletteCount, int indexCount) throws IOException {
        Tile[] tiles = new Tile[tileCount];
        Palette[] palettes = new Palette[paletteCount];
        TileMap tileMap = new TileMap(indexCount);
        
        // Read tile data
        for(int i = 0; i < tileCount; i++) {
            tiles[i] = readTile(inputStream);
        }
        
        // Read palette data
        for(int i = 0; i < paletteCount; i++) {
            Palette palette = new Palette();
            
            for(int j = 0; j < Palette.COLOR_COUNT; j++) {
                palette.setColor(j, ColorUtils.toRGB888(inputStream.readShort()));
            }
            
            palettes[i] = palette;
        }
        
        // Read tilemap data
        for(int i = 0; i < indexCount; i++) {
            tileMap.setIndex(i, inputStream.readShort());
        }
        
        return new TileImage(tiles, palettes, tileMap);
    }
    
    public static Tile readTile(InputStream inputStream) throws IOException {
        return readTile(new LEInputStream(inputStream));
    }
    
    public static Tile readTile(LEInputStream inputStream) throws IOException {
        Tile tile = new Tile();
        
        for(int i = 0; i < Tile.SIZE; i += 2) {
            int pixel = inputStream.read();
            tile.setPixel(i, pixel & 0xF);
            tile.setPixel(i + 1, (pixel >> 4) & 0xF);
        }
        
        return tile;
    }
    
    public static void writeTileImage(OutputStream outputStream, TileImage image) throws IOException {
        writeTileImage(new LEOutputStream(outputStream), image);
    }
    
    public static void writeTileImage(LEOutputStream outputStream, TileImage image) throws IOException {
        // Write tile data
        for(Tile tile : image.getTiles()) {
            for(int i = 0; i < Tile.SIZE; i += 2) {
                outputStream.write(tile.getPixel(i) | (tile.getPixel(i + 1) << 4));
            }
        }
        
        // Write palette data
        for(Palette palette : image.getPalettes()) {
            for(int i = 0; i < Palette.COLOR_COUNT; i++) {
                outputStream.writeShort(ColorUtils.toBGR555(palette.getColor(i)));
            }
        }
        
        // Write tilemap data
        TileMap tileMap = image.getTileMap();
        
        for(int i = 0; i < tileMap.getIndexCount(); i++) {
            outputStream.writeShort(tileMap.getIndex(i));
        }
    } 
}
