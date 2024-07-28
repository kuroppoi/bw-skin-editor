package io.github.kuroppoi.bwse.graphics;

import java.util.Arrays;

public class PaletteTable {
    
    public static final int PALETTE_COUNT = 16;
    public static final int COLOR_COUNT = PALETTE_COUNT * Palette.COLOR_COUNT;
    private final Palette[] palettes = new Palette[PALETTE_COUNT];
    
    public PaletteTable() {
        Arrays.setAll(palettes, i -> new Palette());
    }
    
    public PaletteTable(int... colors) {
        this();
        insertColors(0, colors);
    }
    
    public void setPalette(int index, int... colors) {
        palettes[index].setColors(colors);
    }
    
    public void setPalette(int index, Palette palette) {
        if(palette == null) {
            throw new NullPointerException();
        }
        
        palettes[index] = palette;
    }
    
    public Palette getPalette(int index) {
        return palettes[index];
    }
    
    public void insertColors(int index, int... colors) {
        for(int i = 0; i < colors.length; i++) {
            setColor(index + i, colors[i]);
        }
    }
    
    public int getColor(int index) {
        return getColor(index / PALETTE_COUNT, index % PALETTE_COUNT);
    }
    
    public void setColor(int index, int color) {
        setColor(index / PALETTE_COUNT, index % PALETTE_COUNT, color);
    }
    
    public int getColor(int paletteIndex, int colorIndex) {
        return palettes[paletteIndex].getColor(colorIndex);
    }
    
    public void setColor(int paletteIndex, int colorIndex, int color) {
        palettes[paletteIndex].setColor(colorIndex, color);
    }
}
