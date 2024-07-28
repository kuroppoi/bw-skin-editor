package io.github.kuroppoi.bwse.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.extras.components.FlatTabbedPane;
import com.formdev.flatlaf.extras.components.FlatTabbedPane.TabType;

import io.github.kuroppoi.bwse.graphics.Palette;
import io.github.kuroppoi.bwse.graphics.TileImage;
import io.github.kuroppoi.bwse.gui.component.CGearEditorPanel;
import io.github.kuroppoi.bwse.io.LEOutputStream;
import io.github.kuroppoi.bwse.io.TileImageIO;
import io.github.kuroppoi.bwse.util.Crc16;
import io.github.kuroppoi.bwse.util.SwingUtils;

public class MainView {
    
    public static final FileFilter IMAGE_FILE_FILTER = new FileNameExtensionFilter("Image Files (*.png, *.bmp, *.jpg, *.jpeg)", "png", "bmp", "jpg", "jpeg");
    public static final FileFilter PNG_FILE_FILTER = new FileNameExtensionFilter("Image Files (*.png)", "png");
    public static final FileFilter SKIN_FILE_FILTER = new FileNameExtensionFilter("C-Gear Skin Files (*.bin, *.cgb, *.psk)", "bin", "cgb", "psk");
    private final JFileChooser fileChooser = new JFileChooser(".");
    private final FlatTabbedPane tabbedPane;
    private final JFrame frame;
    private CGearEditorPanel currentView;
    private int tabNumber;
    
    public MainView() {        
        frame = new JFrame("C-Gear Skin Editor");
        
        // Create actions
        Action newFileAction = SwingUtils.createAction("New", UIManager.getIcon("FileView.fileIcon"), this::createNewTab);
        Action openFileAction = SwingUtils.createAction("Open", UIManager.getIcon("FileView.directoryIcon"), () -> showFileOpenDialog(SKIN_FILE_FILTER, this::openSkinFile));
        Action closeFileAction = SwingUtils.createAction("Close", this::closeCurrentTab);
        Action saveFileAction = SwingUtils.createAction("Save As...", UIManager.getIcon("FileView.floppyDriveIcon"), () -> showFileSaveDialog(SKIN_FILE_FILTER, "bin", this::saveSkinFile));
        Action importSkinImageAction = SwingUtils.createAction("Skin Image", () -> showImageOpenDialog(256, 192, this::importSkinImage));
        Action importSkinTilesAction = SwingUtils.createAction("Skin Tileset Image", () -> showImageOpenDialog(136, 120, this::importTileSetImage));
        Action exportSkinImageAction = SwingUtils.createAction("Skin Image", () -> showImageSaveDialog(currentView::drawCGearSkin));
        Action exportSkinTilesAction = SwingUtils.createAction("Skin Tileset Image", () -> showImageSaveDialog(currentView::drawCGearSkinTiles));
        Action exitAction = SwingUtils.createAction("Exit", this::showExitDialog);
        Action togglePreviewAction = SwingUtils.createAction("Toggle Preview", () -> currentView.togglePreview());
        Action toggleColorSchemeAction = SwingUtils.createAction("Toggle Color Scheme", () -> currentView.toggleColorScheme());
        Action cycleButtonStyleAction = SwingUtils.createAction("Cycle Button Style", () -> currentView.cycleButtonStyle());
        Action openRepositoryAction = SwingUtils.createAction("GitHub", () -> SwingUtils.openUrl("https://github.com/kuroppoi/bw-skin-editor"));
        
        // Create tabbed pane
        tabbedPane = new FlatTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabsClosable(true);
        tabbedPane.setTabCloseCallback((pane, index) -> closeTab(index));
        tabbedPane.setTabType(TabType.card);
        tabbedPane.setShowTabSeparators(true);
        tabbedPane.addChangeListener(event -> {            
            currentView = (CGearEditorPanel)tabbedPane.getSelectedComponent();
            tabbedPane.setTabsClosable(tabbedPane.getTabCount() > 1);
            closeFileAction.setEnabled(tabbedPane.getTabCount() > 1);
        });
        
        // Create toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(true);
        toolBar.add(newFileAction);
        toolBar.add(openFileAction);
        toolBar.add(saveFileAction);
        
        // Create menu bar
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(newFileAction).setAccelerator(KeyStroke.getKeyStroke('N', shortcutMask));
        fileMenu.add(openFileAction).setAccelerator(KeyStroke.getKeyStroke('O', shortcutMask));
        fileMenu.add(closeFileAction).setAccelerator(KeyStroke.getKeyStroke('W', shortcutMask));
        fileMenu.add(saveFileAction).setAccelerator(KeyStroke.getKeyStroke('S', shortcutMask));
        fileMenu.addSeparator();
        
        JMenu importMenu = new JMenu("Import");
        importMenu.add(importSkinImageAction);
        importMenu.add(importSkinTilesAction);
        fileMenu.add(importMenu);
        
        JMenu exportMenu = new JMenu("Export");
        exportMenu.add(exportSkinImageAction);
        exportMenu.add(exportSkinTilesAction);
        fileMenu.add(exportMenu);
        
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        menuBar.add(fileMenu);
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(togglePreviewAction).setAccelerator(KeyStroke.getKeyStroke("F1"));
        viewMenu.add(toggleColorSchemeAction).setAccelerator(KeyStroke.getKeyStroke("F2"));
        viewMenu.add(cycleButtonStyleAction).setAccelerator(KeyStroke.getKeyStroke("F3"));
        menuBar.add(viewMenu);
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(openRepositoryAction);
        menuBar.add(helpMenu);
        
        // Finish setting up
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabbedPane);
        panel.add(toolBar, BorderLayout.PAGE_START);
        createNewTab();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                showExitDialog();
            }
        });
        frame.setJMenuBar(menuBar);
        frame.add(panel);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void showImageOpenDialog(int requiredWidth, int requiredHeight, Consumer<BufferedImage> handler) {
        showFileOpenDialog(IMAGE_FILE_FILTER, file -> {
            BufferedImage image = null;
            
            try {
                image = ImageIO.read(file);
            } catch(Exception e) {
                SwingUtils.showExceptionInfo(frame, "Failed to load image.", e);
                return;
            }
            
            if(image == null) {
                JOptionPane.showMessageDialog(frame, "Unknown image format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(image.getWidth() != requiredWidth || image.getHeight() != requiredHeight) {
                JOptionPane.showMessageDialog(frame, "Image must be %s x %s pixels.".formatted(requiredWidth, requiredHeight), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int colorCount = getUniqueColorCount(image);
            
            if(colorCount > Palette.COLOR_COUNT) {
                JOptionPane.showMessageDialog(frame, "Too many colors. Image has %s unique colors, but only %s are permitted.".formatted(colorCount, Palette.COLOR_COUNT), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            handler.accept(image);
        });
    }
    
    private void showImageSaveDialog(Supplier<BufferedImage> imageSupplier) {
        showFileSaveDialog(PNG_FILE_FILTER, "png", file -> {
            String type = getFileExtension(file);
            
            try {
                if(!ImageIO.write(imageSupplier.get(), type, file)) {
                    JOptionPane.showMessageDialog(frame, "Failed to save the image because the image format '%s' is unknown.".formatted(type), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JOptionPane.showMessageDialog(frame, "Image successfully saved to '%s'.".formatted(file.getPath()), "Attention", JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception e) {
                SwingUtils.showExceptionInfo(frame, "Failed to save image.", e);
            }
        });
    }
    
    private void showFileOpenDialog(FileFilter fileFilter, Consumer<File> handler) {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(fileFilter);
        
        if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            handler.accept(fileChooser.getSelectedFile());
        }
    }
    
    private void showFileSaveDialog(FileFilter fileFilter, String defaultExtension, Consumer<File> handler) {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(fileFilter);
        
        if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileExtension = getFileExtension(file);
            List<String> validExtensions = null;
            
            if(fileFilter instanceof FileNameExtensionFilter) {
                validExtensions = Arrays.asList(((FileNameExtensionFilter)fileFilter).getExtensions());
            }
            
            if(fileExtension == null || (validExtensions != null && !validExtensions.contains(fileExtension))) {
                file = new File(fileChooser.getCurrentDirectory(), "%s.%s".formatted(file.getName(), defaultExtension));
            }
            
            handler.accept(file);
        }
    }
    
    private void showExitDialog() {
        if(SwingUtils.showYesNoDialog(frame, "Are you sure you want to exit? Unsaved changes will be lost.")) {
            System.exit(0);
        }
    }
    
    private void closeCurrentTab() {
        closeTab(tabbedPane.getSelectedIndex());
    }
    
    private void closeTab(int index) {
        if(SwingUtils.showYesNoDialog(frame, "Are you sure you want to close '%s'?".formatted(tabbedPane.getTitleAt(index)))) {
            tabbedPane.removeTabAt(index);
        }
    }
    
    private void createNewTab() {
        // Load default skin file
        try(InputStream inputStream = getClass().getResourceAsStream("/cgear/default_skin.bin")) {
            openSkinFile("New Skin %s".formatted(++tabNumber), inputStream);
        } catch(Exception e) {
            SwingUtils.showExceptionInfo(frame, "Failed to load default skin data.", e);
        }
    }
    
    private void openSkinFile(File file) {
        long length = file.length();
        
        if(length != 9728 && length != 9730) {
            JOptionPane.showMessageDialog(frame, "Invalid file length. Expected 9728 or 9730 bytes, received %s.".formatted(length), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try(FileInputStream inputStream = new FileInputStream(file)) {
            openSkinFile(file.getName(), inputStream);
        } catch(Exception e) {
            SwingUtils.showExceptionInfo(frame, "Failed to load skin data.", e);
        }
    }
    
    private void openSkinFile(String name, InputStream inputStream) throws IOException {
        TileImage image = TileImageIO.readTileImage(inputStream, 255, 1, 768);
        CGearEditorPanel panel = new CGearEditorPanel();
        panel.setSkin(image, false);
        tabbedPane.add(name, panel);
        tabbedPane.setSelectedComponent(panel);
    }
    
    private void saveSkinFile(File file) {
        String extension = getFileExtension(file);
        boolean exportChecksum = switch(extension) {
            case "cgb", "psk" -> false;
            default -> true;
        };
        
        try(LEOutputStream outputStream = new LEOutputStream(new FileOutputStream(file))) {            
            if(exportChecksum) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                TileImageIO.writeTileImage(byteStream, currentView.getSkin());
                byte[] bytes = byteStream.toByteArray();
                outputStream.write(bytes);
                outputStream.writeShort(Crc16.calc(bytes));
            } else {
                TileImageIO.writeTileImage(outputStream, currentView.getSkin());
            }
            
            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
            JOptionPane.showMessageDialog(frame, "Skin successfully saved to '%s'.".formatted(file.getPath()), "Attention", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception e) {
            SwingUtils.showExceptionInfo(frame, "Failed to save skin data.", e);
        }
    }
    
    private void importSkinImage(BufferedImage image) {
        TileImage tileImage = TileImage.fromImage(image, true);
        int tileCount = tileImage.getTileCount();
        
        if(tileCount > 255) {
            JOptionPane.showMessageDialog(frame, "Too many tiles. Image has %s unique tiles, but only 255 are permitted."
                    .formatted(tileCount), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentView.setSkin(tileImage, true);
    }
    
    private void importTileSetImage(BufferedImage image) {
        TileImage tileImage = TileImage.fromImage(image, false);
        currentView.setSkinTiles(tileImage.getTiles());
        currentView.setSkinPalette(tileImage.getPalette());
    }
    
    private int getUniqueColorCount(BufferedImage image) {
        Set<Integer> colors = new HashSet<>();
        
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                colors.add(image.getRGB(x, y) & 0xFFFFFF);
            }
        }
        
        return colors.size();
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        
        if(index == -1 || index + 1 == name.length()) {
            return null;
        }
        
        return name.substring(index + 1).toLowerCase();
    }
    
    public void show() {
        frame.setVisible(true);
    }
}
