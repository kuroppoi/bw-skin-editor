package io.github.kuroppoi.bwse.gui.edit;

import io.github.kuroppoi.bwse.graphics.TileMap;
import io.github.kuroppoi.bwse.gui.model.TileArea;

@SuppressWarnings("serial")
public abstract class TileMapEdit extends Edit {
    
    protected TileMap tileMap;
    protected TileArea targetArea;
    
    public TileMapEdit(TileMap tileMap, TileArea targetArea) {
        this.tileMap = tileMap;
        this.targetArea = targetArea;
    }
    
    @Override
    public void undo() {
        super.undo();
        
        // To undo the edit we will simply restore all of the values to their original state
        for(int i = 0; i < targetArea.getIndexCount(); i++) {
            tileMap.setIndex(targetArea.getIndex(i), targetArea.getValue(i));
        }
    }
    
    @Override
    public void redo() {
        super.redo();
        perform();
    }
}
