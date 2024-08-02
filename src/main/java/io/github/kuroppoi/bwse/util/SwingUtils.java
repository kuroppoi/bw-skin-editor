package io.github.kuroppoi.bwse.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SwingUtils {
    
    public static void fixDisabledIcons(JMenu menu) {
        for(int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            
            if(item != null) {
                item.setDisabledIcon(item.getIcon());
            }
        }
    }
    
    public static void fixDisabledIcons(JPopupMenu menu) {
        for(int i = 0; i < menu.getComponentCount(); i++) {
            Component component = menu.getComponent(i);
            
            if(component instanceof JMenuItem) {
                JMenuItem item = (JMenuItem)component;
                item.setDisabledIcon(item.getIcon());
            }
        }
    }
    
    public static Action createAction(String name, Runnable handler) {
        return createAction(name, null, handler);
    }
    
    @SuppressWarnings("serial")
    public static Action createAction(String name, Icon icon, Runnable handler) {
        AbstractAction action = new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(ActionEvent event) {
                handler.run();
            }
        };
        
        if(icon != null) {
            action.putValue(Action.SHORT_DESCRIPTION, name);
        }
        
        return action;
    }
    
    public static boolean showYesNoDialog(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Attention", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    public static void openUrl(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        
        if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI(url);
                desktop.browse(uri);
            } catch(Exception e) {
                showExceptionInfo(null, "Failed to open URL.", e);
            }
        }
    }
    
    public static void showExceptionInfo(Component parentComponent, String message, Throwable throwable) {
        // Create stacktrace string
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        
        // Create text area
        JTextArea area = new JTextArea(writer.toString());
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setEditable(false);
        
        // Create scroll pane
        int height = Math.min(200, area.getFontMetrics(area.getFont()).getHeight() * area.getLineCount() + 10);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(600, height));
        scrollPane.setMaximumSize(scrollPane.getPreferredSize());
        
        // Create dialog
        String label = String.format("<html><b>%s</b><br>Exception details:<br><br></html>", message);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.PAGE_START);
        panel.add(scrollPane);
        JOptionPane.showMessageDialog(parentComponent, panel, "An error has occured", JOptionPane.ERROR_MESSAGE);
    }
}
