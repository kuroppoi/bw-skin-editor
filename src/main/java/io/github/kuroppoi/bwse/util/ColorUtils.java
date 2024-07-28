package io.github.kuroppoi.bwse.util;

public class ColorUtils {
    
    public static int toRGB888(int color) {
        int red = (color & 0x1F) << 3;
        int green = (color >> 5 & 0x1F) << 3;
        int blue = (color >> 10 & 0x1F) << 3;
        return ((red | red >> 5) << 16) | ((green | green >> 5) << 8) | (blue | blue >> 5);
    }
    
    public static int toBGR555(int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        return (red >> 3) | (green >> 3 << 5) | (blue >> 3 << 10);
    }
}
