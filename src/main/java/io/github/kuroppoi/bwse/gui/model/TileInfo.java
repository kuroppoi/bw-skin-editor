package io.github.kuroppoi.bwse.gui.model;

import io.github.kuroppoi.bwse.graphics.TileMap;

public record TileInfo(int x, int y, int index, int tileIndex, boolean flipX, boolean flipY, int palette) {
    
    public TileInfo(int x, int y, int index, TileMap tileMap) {
        this(x, y, index, tileMap.getTileIndex(index), tileMap.getFlipX(index), tileMap.getFlipY(index), tileMap.getPaletteIndex(index));
    }
    
    @Override
    public String toString() {
        return "X: %s, Y: %s, Index: 0x%01X, Tile Index: 0x%01X, Flip X: %s, Flip Y: %s, Palette: %s".formatted(x, y, index, tileIndex, flipX, flipY, palette);
    }
}
