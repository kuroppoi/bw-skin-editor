package io.github.kuroppoi.bwse.gui.edit;

import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.gui.model.TileArea;

@SuppressWarnings("serial")
public class TileMapFlipEdit extends TileMapEdit {
    
    private final boolean flipX;
    private final boolean flipY;
    
    public TileMapFlipEdit(TileMap tileMap, TileArea targetArea, boolean flipX, boolean flipY) {
        super(tileMap, targetArea);
        this.flipX = flipX;
        this.flipY = flipY;
    }
    
    @Override
    public void perform() {
        int width = targetArea.width();
        int height = targetArea.height();
        
        for(int i = 0; i < targetArea.getIndexCount(); i++) {
            int x = i % width;
            int y = i / width;
            
            if(flipX) {
                x = width - 1 - x;
            }
            
            if(flipY) {
                y = height - 1 - y;
            }
            
            int index = targetArea.getIndex(y * width + x);
            tileMap.setIndex(index, targetArea.getValue(i));
            
            if(flipX) {
                tileMap.setFlipX(index, !tileMap.getFlipX(index));
            }
            
            if(flipY) {
                tileMap.setFlipY(index, !tileMap.getFlipY(index));
            }
        }
    }
}
