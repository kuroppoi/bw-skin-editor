package io.github.kuroppoi.bwse.graphics;

import java.util.Arrays;

/**
 * Contains data for 1024 tiles.
 */
public class TileSet {
    
    public static final int TILE_COUNT = 1024;
    private final Tile[] tiles = new Tile[TILE_COUNT];
    
    public TileSet() {
        Arrays.setAll(tiles, i -> new Tile());
    }
    
    public void setTile(int index, Tile tile) {
        if(tile == null) {
            tiles[index].clear();
            return;
        }
        
        tiles[index] = tile;
    }
    
    public Tile getTile(int index) {
        return tiles[index];
    }
}
