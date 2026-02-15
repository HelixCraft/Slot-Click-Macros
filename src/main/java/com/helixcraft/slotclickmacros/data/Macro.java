package com.helixcraft.slotclickmacros.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a saved macro with its metadata and actions.
 */
public class Macro {
    private final String name;
    private final List<MacroAction> actions;
    private String keybind; // Optional keybind (e.g., "Ctrl+R")
    
    public Macro(String name) {
        this(name, new ArrayList<>());
    }
    
    public Macro(String name, List<MacroAction> actions) {
        this.name = name;
        this.actions = new ArrayList<>(actions);
        this.keybind = null;
    }
    
    public String getName() {
        return name;
    }
    
    public List<MacroAction> getActions() {
        return new ArrayList<>(actions);
    }
    
    public void addAction(MacroAction action) {
        actions.add(action);
    }
    
    public void clearActions() {
        actions.clear();
    }
    
    public int size() {
        return actions.size();
    }
    
    public String getKeybind() {
        return keybind;
    }
    
    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }
    
    public boolean hasKeybind() {
        return keybind != null && !keybind.isEmpty();
    }
    
    /**
     * Gets the file name for this macro (sanitized).
     * 
     * @return The file name (e.g., "my_macro.txt")
     */
    public String getFileName() {
        // Sanitize the name for file system
        String sanitized = name.replaceAll("[^a-zA-Z0-9_-]", "_");
        return sanitized + ".txt";
    }
}
