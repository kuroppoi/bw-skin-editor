package io.github.kuroppoi.bwse.gui;

public interface ActionTarget {
    
    public default void onFocusGained() {}
    public default void onFocusLost() {}
}
