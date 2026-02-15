package com.helixcraft.slotclickmacros.data;

import com.helixcraft.slotclickmacros.recording.MacroClickType;

/**
 * Represents a single action in a macro (slot click).
 * Stores the slot ID, the type of click performed, and the button data.
 */
public record MacroAction(int slotId, MacroClickType clickType, int button) {
    
    /**
     * Serializes this action to a simple text format: "slotId,clickType,button"
     * 
     * @return String representation for file storage
     */
    public String serialize() {
        return slotId + "," + clickType.name() + "," + button;
    }
    
    /**
     * Deserializes a MacroAction from a text line.
     * 
     * @param line The line to parse (format: "slotId,clickType,button" or legacy "slotId,clickType")
     * @return The parsed MacroAction
     * @throws IllegalArgumentException if the format is invalid
     */
    public static MacroAction deserialize(String line) {
        String[] parts = line.trim().split(",");
        
        // Support legacy format (without button) for backwards compatibility
        if (parts.length == 2) {
            try {
                int slotId = Integer.parseInt(parts[0]);
                MacroClickType clickType = MacroClickType.valueOf(parts[1]);
                // Default to button 0 (left click) for legacy macros
                return new MacroAction(slotId, clickType, 0);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid slot ID in macro action: " + parts[0], e);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid click type in macro action: " + parts[1], e);
            }
        }
        
        // New format with button data
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid macro action format: " + line);
        }
        
        try {
            int slotId = Integer.parseInt(parts[0]);
            MacroClickType clickType = MacroClickType.valueOf(parts[1]);
            int button = Integer.parseInt(parts[2]);
            return new MacroAction(slotId, clickType, button);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in macro action: " + line, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid click type in macro action: " + parts[1], e);
        }
    }
}
