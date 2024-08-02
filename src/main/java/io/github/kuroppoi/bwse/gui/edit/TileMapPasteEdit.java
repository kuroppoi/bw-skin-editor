package io.github.kuroppoi.bwse.gui.edit;

import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.gui.model.TileArea;

@SuppressWarnings("serial")
public class TileMapPasteEdit extends TileMapEdit {
    
    private TileArea sourceArea;
    
    public TileMapPasteEdit(TileMap tileMap, TileArea targetArea, TileArea sourceArea) {
        super(tileMap, targetArea);
        this.sourceArea = sourceArea;
    }

    @Override
    public void perform() {
        int index = 0;
        
        for(int y = 0; y < targetArea.height(); y++) {
            for(int x = 0; x < targetArea.width(); x++) {
                tileMap.setIndex(targetArea.getIndex(index++), sourceArea.getValue(y * sourceArea.width() + x));
            }
        }
    }
}
