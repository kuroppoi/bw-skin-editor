package io.github.kuroppoi.bwse.graphics;

import java.util.Arrays;

public class Tile {
    
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public static final int SIZE = WIDTH * HEIGHT;
    private final int[] pixels = new int[SIZE];
    
    public Tile() {}
    public Tile(int... pixels) {
        System.arraycopy(pixels, 0, this.pixels, 0, Math.min(pixels.length, SIZE));
    }
    
    public Tile copy() {
        return new Tile(pixels);
    }
    
    public void clear() {
        Arrays.fill(pixels, 0);
    }
    
    public void setPixel(int index, int value) {
        pixels[index] = value;
    }
    
    public int getPixel(int index) {
        return pixels[index];
    }
    
    public int getFlipBits(Tile tile) {
        boolean equal = true;
        boolean flipX = true;
        boolean flipY = true;
        boolean flipXY = true;
        
        for(int i = 0; i < SIZE; i++) {
            int pixel = getPixel(i);
            equal &= pixel == tile.getPixel(i);
            flipX &= pixel == tile.getPixel((WIDTH * (i / WIDTH) + WIDTH) - i % WIDTH - 1);
            flipY &= pixel == tile.getPixel(SIZE - (WIDTH * (i / WIDTH) + WIDTH) + i % WIDTH);
            flipXY &= pixel == tile.getPixel(SIZE - i - 1);
            
            if(!equal && !flipX && !flipY && !flipXY) {
                return -1;
            }
        }
        
        return equal ? 0 : flipX ? TileMap.FLIP_MASK_X : flipY ? TileMap.FLIP_MASK_Y : TileMap.FLIP_MASK;
    }
}
