package com.helixcraft.slotclickmacros.io;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.helixcraft.slotclickmacros.data.Macro;
import com.helixcraft.slotclickmacros.data.MacroAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages reading and writing macro files to disk.
 */
public class MacroFileManager {
    private static final String CONFIG_DIR = "config/slotclickmacros";
    private final Path macrosDir;
    
    public MacroFileManager(Path gameDir) {
        this.macrosDir = gameDir.resolve(CONFIG_DIR);
        createDirectoryIfNeeded();
    }
    
    /**
     * Creates the macros directory if it doesn't exist.
     */
    private void createDirectoryIfNeeded() {
        try {
            if (!Files.exists(macrosDir)) {
                Files.createDirectories(macrosDir);
                SlotClickMacros.LOGGER.info("Created macros directory at: {}", macrosDir);
            }
        } catch (IOException e) {
            SlotClickMacros.LOGGER.error("Failed to create macros directory", e);
        }
    }
    
    /**
     * Saves a macro to a file.
     * 
     * @param macro The macro to save
     * @throws IOException if the file cannot be written
     */
    public void saveMacro(Macro macro) throws IOException {
        Path macroFile = macrosDir.resolve(macro.getFileName());
        
        List<String> lines = new ArrayList<>();
        for (MacroAction action : macro.getActions()) {
            lines.add(action.serialize());
        }
        
        Files.write(macroFile, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        SlotClickMacros.LOGGER.info("Saved macro '{}' with {} actions to {}", 
                                     macro.getName(), macro.size(), macroFile);
    }
    
    /**
     * Loads a macro from a file.
     * 
     * @param name The name of the macro
     * @return The loaded macro
     * @throws IOException if the file cannot be read
     */
    public Macro loadMacro(String name) throws IOException {
        Macro macro = new Macro(name);
        Path macroFile = macrosDir.resolve(macro.getFileName());
        
        if (!Files.exists(macroFile)) {
            throw new IOException("Macro file not found: " + macroFile);
        }
        
        List<String> lines = Files.readAllLines(macroFile);
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue; // Skip empty lines and comments
            }
            
            try {
                MacroAction action = MacroAction.deserialize(line);
                macro.addAction(action);
            } catch (IllegalArgumentException e) {
                SlotClickMacros.LOGGER.warn("Skipping invalid macro action: {}", line, e);
            }
        }
        
        SlotClickMacros.LOGGER.info("Loaded macro '{}' with {} actions from {}", 
                                     name, macro.size(), macroFile);
        return macro;
    }
    
    /**
     * Deletes a macro file.
     * 
     * @param name The name of the macro to delete
     * @return true if the file was deleted, false otherwise
     */
    public boolean deleteMacro(String name) {
        Macro macro = new Macro(name);
        Path macroFile = macrosDir.resolve(macro.getFileName());
        
        try {
            boolean deleted = Files.deleteIfExists(macroFile);
            if (deleted) {
                SlotClickMacros.LOGGER.info("Deleted macro file: {}", macroFile);
            }
            return deleted;
        } catch (IOException e) {
            SlotClickMacros.LOGGER.error("Failed to delete macro file: {}", macroFile, e);
            return false;
        }
    }
    
    /**
     * Lists all macro files in the macros directory.
     * 
     * @return List of macro names (without .txt extension)
     */
    public List<String> listMacros() {
        List<String> macroNames = new ArrayList<>();
        
        try (Stream<Path> files = Files.list(macrosDir)) {
            files.filter(path -> path.toString().endsWith(".txt"))
                 .forEach(path -> {
                     String fileName = path.getFileName().toString();
                     String macroName = fileName.substring(0, fileName.length() - 4); // Remove .txt
                     macroNames.add(macroName);
                 });
        } catch (IOException e) {
            SlotClickMacros.LOGGER.error("Failed to list macro files", e);
        }
        
        return macroNames;
    }
    
    /**
     * Checks if a macro file exists.
     * 
     * @param name The name of the macro
     * @return true if the file exists, false otherwise
     */
    public boolean macroExists(String name) {
        Macro macro = new Macro(name);
        Path macroFile = macrosDir.resolve(macro.getFileName());
        return Files.exists(macroFile);
    }
    
    /**
     * Imports a macro from an external file.
     * 
     * @param sourcePath The path to the macro file to import
     * @param newName The name for the imported macro
     * @throws IOException if the file cannot be imported
     */
    public void importMacro(Path sourcePath, String newName) throws IOException {
        Macro macro = new Macro(newName);
        Path destPath = macrosDir.resolve(macro.getFileName());
        
        Files.copy(sourcePath, destPath);
        SlotClickMacros.LOGGER.info("Imported macro from {} to {}", sourcePath, destPath);
    }
    
    /**
     * Gets the full path to the macros directory.
     * 
     * @return The macros directory path
     */
    public Path getMacrosDirectory() {
        return macrosDir;
    }
}
