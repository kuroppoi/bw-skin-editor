package io.github.kuroppoi.bwse.gui.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.bwse.graphics.TileMap;

public record TileArea(int x, int y, int width, int height, List<Integer> indices, List<Integer> values) {
    
    public static TileArea createTileArea(TileMap tileMap, int mapWidth, int mapHeight, Rectangle rectangle) {
        return createTileArea(tileMap, mapWidth, mapHeight, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public static TileArea createTileArea(TileMap tileMap, int mapWidth, int mapHeight, int x, int y, int width, int height) {
        int totalWidth = Math.min(width, mapWidth - x);
        int totalHeight = Math.min(height, mapHeight - y);
        List<Integer> indices = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        
        for(int j = y; j < y + totalHeight; j++) {
            for(int i = x; i < x + totalWidth; i++) {
                int index = j * mapWidth + i;
                indices.add(index);
                values.add(tileMap.getIndex(index));
            }
        }
        
        return new TileArea(x, y, totalWidth, totalHeight, indices, values);
    }
    
    @Override
    public String toString() {
        return "X: %s, Y: %s, Width: %s, Height: %s".formatted(x, y, width, height);
    }
    
    public int getIndexCount() {
        return indices.size();
    }
    
    public int getIndex(int index) {
        return indices.get(index);
    }
    
    public int getValue(int index) {
        return values.get(index);
    }
}
