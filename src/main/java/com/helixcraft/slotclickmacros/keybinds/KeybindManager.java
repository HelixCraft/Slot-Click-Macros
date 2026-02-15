package com.helixcraft.slotclickmacros.keybinds;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages custom keybinds including support for modifier key combinations.
 */
public class KeybindManager {
    private static final String CATEGORY = "slot-click-macros.general";
    
    private final Map<String, KeyMapping> macroKeybinds = new HashMap<>();
    
    // Keybinding for opening config screen (only this one in Minecraft settings)
    private final KeyMapping openConfigKey;
    
    public KeybindManager() {
        // Only register config screen keybind in Minecraft settings
        // Record keybind is now managed through config screen
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.slot-click-macros.open_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O, // Default to 'O' key
            CATEGORY
        ));
        
        SlotClickMacros.LOGGER.info("Registered config keybind");
    }
    
    /**
     * Registers a keybind for a specific macro.
     * 
     * @param macroName The name of the macro
     * @param keybindString The keybind string (e.g., "Ctrl+1", "R", "Shift+Q")
     */
    public void registerMacroKeybind(String macroName, String keybindString) {
        // Unregister existing keybind if present
        unregisterMacroKeybind(macroName);
        
        // Parse keybind string
        int keyCode = parseKeybind(keybindString);
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            SlotClickMacros.LOGGER.warn("Invalid keybind string: {}", keybindString);
            return;
        }
        
        // Create and register the keybind
        KeyMapping keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.slotclickmacros.macro." + macroName,
            InputConstants.Type.KEYSYM,
            keyCode,
            CATEGORY
        ));
        
        macroKeybinds.put(macroName, keyMapping);
        SlotClickMacros.LOGGER.info("Registered keybind {} for macro '{}'", keybindString, macroName);
    }
    
    /**
     * Unregisters a macro's keybind.
     * 
     * @param macroName The name of the macro
     */
    public void unregisterMacroKeybind(String macroName) {
        macroKeybinds.remove(macroName);
        // Note: Fabric doesn't provide a way to fully unregister keybinds
        // They will remain until the game restarts
    }
    
    /**
     * Parses a keybind string into a GLFW key code.
     * Currently supports simple key names. Modifier combinations (Ctrl+X) will need
     * special handling in the tick event.
     * 
     * @param keybindString The keybind string
     * @return The GLFW key code, or GLFW_KEY_UNKNOWN if invalid
     */
    private int parseKeybind(String keybindString) {
        // Remove modifier prefixes for now (we'll handle them in isKeybindPressed)
        String keyName = keybindString.toUpperCase()
            .replace("CTRL+", "")
            .replace("SHIFT+", "")
            .replace("ALT+", "")
            .trim();
        
        return getKeyCodeFromName(keyName);
    }
    
    /**
     * Checks if a keybind (with modifiers) is currently pressed.
     * 
     * @param keybindString The keybind string (e.g., "Ctrl+R")
     * @param keyMapping The KeyMapping to check
     * @return true if the keybind is pressed with the correct modifiers
     */
    public boolean isKeybindPressed(String keybindString, KeyMapping keyMapping) {
        if (!keyMapping.isDown()) {
            return false;
        }
        
        // Check for modifiers
        boolean needsCtrl = keybindString.toUpperCase().contains("CTRL+");
        boolean needsShift = keybindString.toUpperCase().contains("SHIFT+");
        boolean needsAlt = keybindString.toUpperCase().contains("ALT+");
        
        // Get window handle
        long window = Minecraft.getInstance().getWindow().window;
        
        boolean hasCtrl = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                          GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
        boolean hasShift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                           GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        boolean hasAlt = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS ||
                         GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
        
        // All required modifiers must match
        return (needsCtrl == hasCtrl) && (needsShift == hasShift) && (needsAlt == hasAlt);
    }
    
    /**
     * Checks if a keybind string is currently pressed.
     * This checks the raw keyboard state, not KeyMapping objects.
     * 
     * @param keybindString The keybind string (e.g., "R", "Ctrl+A")
     * @return true if the keybind is currently pressed
     */
    public boolean isKeybindStringPressed(String keybindString) {
        if (keybindString == null || keybindString.isEmpty()) {
            return false;
        }
        
        // Get window handle
        long window = Minecraft.getInstance().getWindow().window;
        
        // Parse modifiers
        boolean needsCtrl = keybindString.toUpperCase().contains("CTRL+");
        boolean needsShift = keybindString.toUpperCase().contains("SHIFT+");
        boolean needsAlt = keybindString.toUpperCase().contains("ALT+");
        
        // Check modifiers
        boolean hasCtrl = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                          GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
        boolean hasShift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                           GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        boolean hasAlt = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS ||
                         GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
        
        // Modifiers must match exactly
        if (needsCtrl != hasCtrl || needsShift != hasShift || needsAlt != hasAlt) {
            return false;
        }
        
        // Extract main key
        String keyName = keybindString.toUpperCase()
            .replace("CTRL+", "")
            .replace("SHIFT+", "")
            .replace("ALT+", "")
            .trim();
        
        // Get key code using the same logic as parseKeybind
        int keyCode = getKeyCodeFromName(keyName);
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return false;
        }
        
        // Check if main key is pressed
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }
    
    /**
     * Gets GLFW key code from key name string.
     */
    private int getKeyCodeFromName(String keyName) {
        return switch (keyName) {
            case "A" -> GLFW.GLFW_KEY_A;
            case "B" -> GLFW.GLFW_KEY_B;
            case "C" -> GLFW.GLFW_KEY_C;
            case "D" -> GLFW.GLFW_KEY_D;
            case "E" -> GLFW.GLFW_KEY_E;
            case "F" -> GLFW.GLFW_KEY_F;
            case "G" -> GLFW.GLFW_KEY_G;
            case "H" -> GLFW.GLFW_KEY_H;
            case "I" -> GLFW.GLFW_KEY_I;
            case "J" -> GLFW.GLFW_KEY_J;
            case "K" -> GLFW.GLFW_KEY_K;
            case "L" -> GLFW.GLFW_KEY_L;
            case "M" -> GLFW.GLFW_KEY_M;
            case "N" -> GLFW.GLFW_KEY_N;
            case "O" -> GLFW.GLFW_KEY_O;
            case "P" -> GLFW.GLFW_KEY_P;
            case "Q" -> GLFW.GLFW_KEY_Q;
            case "R" -> GLFW.GLFW_KEY_R;
            case "S" -> GLFW.GLFW_KEY_S;
            case "T" -> GLFW.GLFW_KEY_T;
            case "U" -> GLFW.GLFW_KEY_U;
            case "V" -> GLFW.GLFW_KEY_V;
            case "W" -> GLFW.GLFW_KEY_W;
            case "X" -> GLFW.GLFW_KEY_X;
            case "Y" -> GLFW.GLFW_KEY_Y;
            case "Z" -> GLFW.GLFW_KEY_Z;
            case "0" -> GLFW.GLFW_KEY_0;
            case "1" -> GLFW.GLFW_KEY_1;
            case "2" -> GLFW.GLFW_KEY_2;
            case "3" -> GLFW.GLFW_KEY_3;
            case "4" -> GLFW.GLFW_KEY_4;
            case "5" -> GLFW.GLFW_KEY_5;
            case "6" -> GLFW.GLFW_KEY_6;
            case "7" -> GLFW.GLFW_KEY_7;
            case "8" -> GLFW.GLFW_KEY_8;
            case "9" -> GLFW.GLFW_KEY_9;
            case "F1" -> GLFW.GLFW_KEY_F1;
            case "F2" -> GLFW.GLFW_KEY_F2;
            case "F3" -> GLFW.GLFW_KEY_F3;
            case "F4" -> GLFW.GLFW_KEY_F4;
            case "F5" -> GLFW.GLFW_KEY_F5;
            case "F6" -> GLFW.GLFW_KEY_F6;
            case "F7" -> GLFW.GLFW_KEY_F7;
            case "F8" -> GLFW.GLFW_KEY_F8;
            case "F9" -> GLFW.GLFW_KEY_F9;
            case "F10" -> GLFW.GLFW_KEY_F10;
            case "F11" -> GLFW.GLFW_KEY_F11;
            case "F12" -> GLFW.GLFW_KEY_F12;
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "ENTER" -> GLFW.GLFW_KEY_ENTER;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "LEFT" -> GLFW.GLFW_KEY_LEFT;
            case "RIGHT" -> GLFW.GLFW_KEY_RIGHT;
            case "UP" -> GLFW.GLFW_KEY_UP;
            case "DOWN" -> GLFW.GLFW_KEY_DOWN;
            default -> GLFW.GLFW_KEY_UNKNOWN;
        };
    }
    
    public KeyMapping getOpenConfigKey() {
        return openConfigKey;
    }
    
    public KeyMapping getMacroKeybind(String macroName) {
        return macroKeybinds.get(macroName);
    }
    
    public Map<String, KeyMapping> getAllMacroKeybinds() {
        return new HashMap<>(macroKeybinds);
    }
}
