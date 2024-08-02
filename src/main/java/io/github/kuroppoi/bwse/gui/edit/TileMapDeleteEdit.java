package io.github.kuroppoi.bwse.gui.edit;

import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.gui.model.TileArea;

@SuppressWarnings("serial")
public class TileMapDeleteEdit extends TileMapEdit {
    
    public TileMapDeleteEdit(TileMap tileMap, TileArea targetArea) {
        super(tileMap, targetArea);
    }

    @Override
    public void perform() {
        for(int i = 0; i < targetArea.getIndexCount(); i++) {
            tileMap.setIndex(targetArea.getIndex(i), 0);
        }
    }
}
