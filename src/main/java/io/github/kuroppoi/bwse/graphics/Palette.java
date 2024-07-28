package io.github.kuroppoi.bwse.graphics;

import java.util.Arrays;

public class Palette {
    
    public static final int COLOR_COUNT = 16;
    private final int[] colors = new int[COLOR_COUNT];
    private int currentIndex;
    
    public Palette(int... colors) {
        System.arraycopy(colors, 0, this.colors, 0, Math.min(this.colors.length, colors.length));
        currentIndex = colors.length;
    }
    
    public void clear() {
        Arrays.fill(colors, 0);
        currentIndex = 0;
    }
    
    public int tryAddColor(int color) {
        if(currentIndex > 0) {
            int index = getColorIndex(color);
            
            if(index != -1) {
                return index;
            }
        }
        
        if(currentIndex >= colors.length) {
            throw new IllegalArgumentException("Palette has reached the maximum number of colors.");
        }
        
        colors[currentIndex] = color;
        return currentIndex++;
    }
    
    public void setColors(int... colors) {
        for(int i = 0; i < COLOR_COUNT; i++) {
            this.colors[i] = i < colors.length ? colors[i] : 0;
        }
    }
    
    public void setColor(int index, int color) {
        colors[index] = color;
    }
    
    public int getColor(int index) {
        if(index >= 0 && index < colors.length) {
            return colors[index];
        }
        
        return 0;
    }
    
    public int getColorIndex(int color) {
        for(int i = 0; i < currentIndex; i++) {
            if(colors[i] == color) {
                return i;
            }
        }
        
        return -1;
    }
}
