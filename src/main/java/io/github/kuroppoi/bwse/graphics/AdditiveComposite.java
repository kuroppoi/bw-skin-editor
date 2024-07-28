package io.github.kuroppoi.bwse.graphics;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class AdditiveComposite implements Composite {
    
    public static final AdditiveComposite INSTANCE = new AdditiveComposite();
    
    private AdditiveComposite() {}
    
    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new Context(srcColorModel, dstColorModel);
    }
    
    private class Context implements CompositeContext {
        
        public static final int ALPHA_MASK = 0xFF000000;
        public static final int RED_MASK = 0x00FF0000;
        public static final int GREEN_MASK = 0x0000FF00;
        public static final int BLUE_MASK = 0x000000FF;
        private final ColorModel srcColorModel;
        private final ColorModel dstColorModel;
        
        public Context(ColorModel srcColorModel, ColorModel dstColorModel) {
            this.srcColorModel = srcColorModel;
            this.dstColorModel = dstColorModel;
        }
        
        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(src.getWidth(), dstIn.getWidth());
            int height = Math.min(src.getHeight(), dstIn.getHeight());
            
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    int srcPixel = srcColorModel.getRGB(src.getDataElements(x, y, null));
                    
                    if((srcPixel & ~ALPHA_MASK) == 0) {
                        continue; // Skip completely black pixels
                    }
                    
                    int dstPixel = dstColorModel.getRGB(dstIn.getDataElements(x, y, null));
                    int alpha = dstPixel & ALPHA_MASK;
                    int red = Math.min(RED_MASK, (dstPixel & RED_MASK) + (srcPixel & RED_MASK));
                    int green = Math.min(GREEN_MASK, (dstPixel & GREEN_MASK) + (srcPixel & GREEN_MASK));
                    int blue = Math.min(BLUE_MASK, (dstPixel & BLUE_MASK) + (srcPixel & BLUE_MASK));
                    dstPixel = alpha | red | green | blue;
                    dstOut.setDataElements(x, y, dstColorModel.getDataElements(dstPixel, null));
                }
            }
        }

        @Override
        public void dispose() {}
    }
}
