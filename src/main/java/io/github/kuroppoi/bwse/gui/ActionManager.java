package io.github.kuroppoi.bwse.gui;

import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;

import io.github.kuroppoi.bwse.gui.component.TileMapCanvas;
import io.github.kuroppoi.bwse.util.SwingUtils;

/**
 * Responsible for keeping action states synced & tracking the current action target.
 * This whole thing is being held together by hopes, dreams and a lot of duct tape.
 */
@SuppressWarnings("unchecked")
public class ActionManager {
    
    public static final String UNDO = "Undo";
    public static final String REDO = "Redo";
    public static final String COPY = "Copy Tiles";
    public static final String PASTE = "Paste Tiles";
    public static final String DELETE = "Delete Tiles";
    public static final String FLIP_X = "Flip Horizontally";
    public static final String FLIP_Y = "Flip Vertically";
    public static final String SELECT_ALL = "Select All";
    public static final String DESELECT = "Deselect";
    public static final Map<String, Action> ACTION_MAP = Map.of(
        UNDO, createAction(UNDO, UIManager.getIcon("ToolBar.undoIcon"), TileMapCanvas.class, TileMapCanvas::undo),
        REDO, createAction(REDO, UIManager.getIcon("ToolBar.redoIcon"), TileMapCanvas.class, TileMapCanvas::redo),
        COPY, createAction(COPY, UIManager.getIcon("ToolBar.copyIcon"), TileMapCanvas.class, TileMapCanvas::copyTiles),
        PASTE, createAction(PASTE, UIManager.getIcon("ToolBar.pasteIcon"), TileMapCanvas.class, TileMapCanvas::pasteTiles),
        DELETE, createAction(DELETE, UIManager.getIcon("ToolBar.deleteIcon"), TileMapCanvas.class, TileMapCanvas::deleteTiles),
        FLIP_X, createAction(FLIP_X, UIManager.getIcon("ToolBar.flipHorizontallyIcon"), TileMapCanvas.class, TileMapCanvas::flipTilesX),
        FLIP_Y, createAction(FLIP_Y, UIManager.getIcon("ToolBar.flipVerticallyIcon"), TileMapCanvas.class, TileMapCanvas::flipTilesY),
        SELECT_ALL, createAction(SELECT_ALL, TileMapCanvas.class, TileMapCanvas::selectAllTiles),
        DESELECT, createAction(DESELECT, TileMapCanvas.class, TileMapCanvas::clearTileSelection)
    );
    
    private static ActionTarget actionTarget;
    
    public static void setActionTarget(ActionTarget target) {
        if(actionTarget == target) {
            return;
        } else if(actionTarget != null) {
            actionTarget.onFocusLost();
        }
        
        actionTarget = target;
        
        if(target != null) {
            target.onFocusGained();
        } else {
            disableAllActions();
        }
    }
    
    public static void removeActionTarget(ActionTarget target) {
        if(isActionTarget(target)) {
            setActionTarget(null);
        }
    }
    
    public static boolean isActionTarget(ActionTarget target) {
        return actionTarget == target;
    }
    
    public static <T extends ActionTarget> void getActionTarget(Class<T> expectedType, Consumer<T> handler) {  
        if(actionTarget != null && actionTarget.getClass().isAssignableFrom(expectedType)) {
            handler.accept((T)actionTarget);
        }
    }
    
    public static ActionTarget getActionTarget() {
        return actionTarget;
    }
    
    public static void enableAllActions() {
        setAllActionsEnabled(true);
    }
    
    public static void disableAllActions() {
        setAllActionsEnabled(false);
    }
    
    public static void setAllActionsEnabled(boolean enabled) {
        ACTION_MAP.values().forEach(action -> action.setEnabled(enabled));
    }
    
    public static void disableAction(String key) {
        setActionEnabled(key, false);
    }
    
    public static void enableAction(String key) {
        setActionEnabled(key, true);
    }
    
    public static boolean isActionEnabled(String key) {
        Action action = getAction(key);
        return action != null && action.isEnabled();
    }
    
    public static void setActionEnabled(String key, boolean enabled) {
        Action action = getAction(key);
        
        if(action != null) {
            action.setEnabled(enabled);
        }
    }
    
    private static <T extends ActionTarget> Action createAction(String name, Class<T> targetType, Consumer<T> handler) {
        return createAction(name, null, targetType, handler);
    }
    
    private static <T extends ActionTarget> Action createAction(String name, Icon icon, Class<T> targetType, Consumer<T> handler) {
        Action action = SwingUtils.createAction(name, icon, () -> getActionTarget(targetType, handler));
        action.setEnabled(false); // Disable action by default
        return action;
    }
    
    public static Action getAction(String key) {
        return ACTION_MAP.get(key);
    }
}
