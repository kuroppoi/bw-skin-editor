package io.github.kuroppoi.bwse;

import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import io.github.kuroppoi.bwse.graphics.CGearRenderer;
import io.github.kuroppoi.bwse.gui.MainView;
import io.github.kuroppoi.bwse.gui.icon.CopyIcon;
import io.github.kuroppoi.bwse.gui.icon.CurvedArrowIcon;
import io.github.kuroppoi.bwse.gui.icon.DeleteIcon;
import io.github.kuroppoi.bwse.gui.icon.FlipIcon;
import io.github.kuroppoi.bwse.gui.icon.PasteIcon;
import io.github.kuroppoi.bwse.util.SwingUtils;

public class Main {
    
    public static void main(String[] args) {
        new Main();
    }
    
    public Main() {
        FlatOneDarkIJTheme.setup();
        UIManager.put("ToolBar.undoIcon", new CurvedArrowIcon());
        UIManager.put("ToolBar.redoIcon", new CurvedArrowIcon(true));
        UIManager.put("ToolBar.copyIcon", new CopyIcon());
        UIManager.put("ToolBar.pasteIcon", new PasteIcon());
        UIManager.put("ToolBar.deleteIcon", new DeleteIcon());
        UIManager.put("ToolBar.flipHorizontallyIcon", new FlipIcon());
        UIManager.put("ToolBar.flipVerticallyIcon", new FlipIcon(true));
        
        try {
            CGearRenderer.init();
        } catch(Exception e) {
            SwingUtils.showExceptionInfo(null, "Failed to initialize C-Gear renderer.", e);
            System.exit(-1);
            return; 
        }
        
        new MainView();
    }
}
