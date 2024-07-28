package io.github.kuroppoi.bwse;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import io.github.kuroppoi.bwse.graphics.CGearRenderer;
import io.github.kuroppoi.bwse.gui.MainView;
import io.github.kuroppoi.bwse.util.SwingUtils;

public class Main {
    
    public static void main(String[] args) {
        new Main();
    }
    
    public Main() {
        FlatOneDarkIJTheme.setup();
        
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
