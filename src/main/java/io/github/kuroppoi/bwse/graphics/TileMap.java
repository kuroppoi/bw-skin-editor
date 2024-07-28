package io.github.kuroppoi.bwse.graphics;

/**
 * Contains data for constructing an image out of tiles in a {@link TileSet}.
 */
public class TileMap {
    
    public static final int TILE_INDEX_MASK = 0x3FF;
    public static final int FLIP_MASK_X = 0x400;
    public static final int FLIP_MASK_Y = 0x800;
    public static final int FLIP_MASK = FLIP_MASK_X | FLIP_MASK_Y;
    public static final int PALETTE_MASK = 0xF000;
    public static final int PALETTE_SHIFT = 12;
    private final int[] indices;
    
    public TileMap(int size) {
        indices = new int[size];
    }
    
    public TileMap(int... indices) {
        this(indices.length);
        System.arraycopy(indices, 0, this.indices, 0, indices.length);
    }
    
    public void setTileIndex(int index, int tileIndex) {
        indices[index] &= ~TILE_INDEX_MASK;
        indices[index] |= tileIndex & TILE_INDEX_MASK;
    }
    
    public int getTileIndex(int index) {
        return indices[index] & TILE_INDEX_MASK;
    }
    
    private void setFlip(int index, int mask, boolean value) {
        if(value) {
            indices[index] |= mask;
        } else {
            indices[index] &= ~mask;
        }
    }
    
    public void setFlipX(int index, boolean value) {
        setFlip(index, FLIP_MASK_X, value);
    }
    
    public boolean getFlipX(int index) {
        return (indices[index] & FLIP_MASK_X) == FLIP_MASK_X;
    }
    
    public void setFlipY(int index, boolean value) {
        setFlip(index, FLIP_MASK_Y, value);
    }
    
    public boolean getFlipY(int index) {
        return (indices[index] & FLIP_MASK_Y) == FLIP_MASK_Y;
    }
    
    public int getFlipBits(int index) {
        return indices[index] & FLIP_MASK;
    }
    
    public void setPaletteIndex(int paletteIndex) {
        for(int i = 0; i < indices.length; i++) {
            setPaletteIndex(i, paletteIndex);
        }
    }
    
    public void setPaletteIndex(int index, int paletteIndex) {
        indices[index] &= ~PALETTE_MASK;
        indices[index] |= (paletteIndex << PALETTE_SHIFT) & PALETTE_MASK;
    }
    
    public int getPaletteIndex(int index) {
        return (indices[index] & PALETTE_MASK) >> PALETTE_SHIFT;
    }
    
    public int getIndexCount() {
        return indices.length;
    }
    
    public void setIndex(int index, int tileIndex, int flipBits, int paletteIndex) {
        indices[index] = (tileIndex & TILE_INDEX_MASK) | (flipBits & FLIP_MASK) | ((paletteIndex << PALETTE_SHIFT) & PALETTE_MASK);
    }
    
    public void setIndex(int index, int value) {
        indices[index] = value;
    }
    
    public int getIndex(int index) {
        return indices[index];
    }
}
