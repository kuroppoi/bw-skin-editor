package io.github.kuroppoi.bwse.gui.model;

import java.util.List;

public record TileSelection(int x, int y, int width, int height, List<Integer> indices, List<Integer> values) {
    
    @Override
    public String toString() {
        return "X: %s, Y: %s, Width: %s, Height: %s".formatted(x, y, width, height);
    }
    
    public int getIndexCount() {
        return width * height;
    }
    
    public int getIndex(int index) {
        return indices.get(index);
    }
    
    public int getValue(int index) {
        return values.get(index);
    }
}
