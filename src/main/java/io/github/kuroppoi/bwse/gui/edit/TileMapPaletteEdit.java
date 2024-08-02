package io.github.kuroppoi.bwse.gui.edit;

import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.gui.model.TileArea;

@SuppressWarnings("serial")
public class TileMapPaletteEdit extends TileMapEdit {
    
    private final int paletteIndex;
    
    public TileMapPaletteEdit(TileMap tileMap, TileArea targetArea, int paletteIndex) {
        super(tileMap, targetArea);
        this.paletteIndex = paletteIndex;
    }
    
    @Override
    public void perform() {
        for(int i = 0; i < targetArea.getIndexCount(); i++) {
            tileMap.setPaletteIndex(targetArea.getIndex(i), paletteIndex);
        }
    }
}
