package io.github.kuroppoi.bwse.gui.edit;

import javax.swing.undo.AbstractUndoableEdit;

@SuppressWarnings("serial")
public abstract class Edit extends AbstractUndoableEdit {
    
    public abstract void perform();
}
