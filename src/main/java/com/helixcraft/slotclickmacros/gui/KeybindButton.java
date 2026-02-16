package com.helixcraft.slotclickmacros.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Custom button for keybind input with modifier key support.
 * Shows "Set Keybind" when empty, "Press any key..." when listening,
 * and the actual keybind when set.
 */
public class KeybindButton extends AbstractButton {
    private String keybind;
    private boolean listening = false;
    private final Consumer<String> onChange;
    private final List<Integer> pressedKeys = new ArrayList<>();
    
    public KeybindButton(int x, int y, int width, int height, String initialKeybind, Consumer<String> onChange) {
        super(x, y, width, height, Component.literal(initialKeybind.isEmpty() ? "Set Keybind" : initialKeybind));
        this.keybind = initialKeybind;
        this.onChange = onChange;
    }
    
    @Override
    public void onPress(InputWithModifiers input) {
        if (listening) {
            // Cancel listening
            listening = false;
            pressedKeys.clear();
            updateMessage();
        } else {
            // Start listening
            listening = true;
            pressedKeys.clear();
            setMessage(Component.literal("§ePress any key..."));
        }
    }
    
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.key();
        
        if (!listening) {
            return super.keyPressed(keyEvent);
        }
        
        // Escape clears keybind (sets to empty)
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            keybind = "";
            listening = false;
            pressedKeys.clear();
            onChange.accept("");
            updateMessage();
            return true;
        }
        
        // Backspace/Delete clears keybind
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
            keybind = "";
            listening = false;
            pressedKeys.clear();
            onChange.accept("");
            updateMessage();
            return true;
        }
        
        // Ignore modifier keys alone
        if (isModifierKey(keyCode)) {
            return true;
        }
        
        // Build keybind string
        StringBuilder keybindBuilder = new StringBuilder();
        
        // Get window handle
        long window = Minecraft.getInstance().getWindow().handle();
        
        // Check modifiers
        boolean hasCtrl = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                          GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
        boolean hasShift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                           GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        boolean hasAlt = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS ||
                         GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
        
        if (hasCtrl) keybindBuilder.append("Ctrl+");
        if (hasShift) keybindBuilder.append("Shift+");
        if (hasAlt) keybindBuilder.append("Alt+");
        
        // Add the main key
        String keyName = getKeyName(keyCode);
        keybindBuilder.append(keyName);
        
        keybind = keybindBuilder.toString();
        listening = false;
        pressedKeys.clear();
        
        // Notify change
        onChange.accept(keybind);
        updateMessage();
        
        return true;
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        if (!listening) {
            return super.mouseClicked(event, consumed);
        }
        
        // Right click cancels
        if (event.button() == 1) {
            listening = false;
            pressedKeys.clear();
            updateMessage();
            return true;
        }
        
        return super.mouseClicked(event, consumed);
    }
    
    private void updateMessage() {
        if (keybind.isEmpty()) {
            setMessage(Component.literal("§7Set Keybind"));
        } else {
            setMessage(Component.literal("§f" + keybind));
        }
    }
    
    private boolean isModifierKey(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL ||
               keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT ||
               keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
    }
    
    private String getKeyName(int keyCode) {
        // Map GLFW key codes to readable names
        return switch (keyCode) {
            case GLFW.GLFW_KEY_A -> "A";
            case GLFW.GLFW_KEY_B -> "B";
            case GLFW.GLFW_KEY_C -> "C";
            case GLFW.GLFW_KEY_D -> "D";
            case GLFW.GLFW_KEY_E -> "E";
            case GLFW.GLFW_KEY_F -> "F";
            case GLFW.GLFW_KEY_G -> "G";
            case GLFW.GLFW_KEY_H -> "H";
            case GLFW.GLFW_KEY_I -> "I";
            case GLFW.GLFW_KEY_J -> "J";
            case GLFW.GLFW_KEY_K -> "K";
            case GLFW.GLFW_KEY_L -> "L";
            case GLFW.GLFW_KEY_M -> "M";
            case GLFW.GLFW_KEY_N -> "N";
            case GLFW.GLFW_KEY_O -> "O";
            case GLFW.GLFW_KEY_P -> "P";
            case GLFW.GLFW_KEY_Q -> "Q";
            case GLFW.GLFW_KEY_R -> "R";
            case GLFW.GLFW_KEY_S -> "S";
            case GLFW.GLFW_KEY_T -> "T";
            case GLFW.GLFW_KEY_U -> "U";
            case GLFW.GLFW_KEY_V -> "V";
            case GLFW.GLFW_KEY_W -> "W";
            case GLFW.GLFW_KEY_X -> "X";
            case GLFW.GLFW_KEY_Y -> "Y";
            case GLFW.GLFW_KEY_Z -> "Z";
            case GLFW.GLFW_KEY_0 -> "0";
            case GLFW.GLFW_KEY_1 -> "1";
            case GLFW.GLFW_KEY_2 -> "2";
            case GLFW.GLFW_KEY_3 -> "3";
            case GLFW.GLFW_KEY_4 -> "4";
            case GLFW.GLFW_KEY_5 -> "5";
            case GLFW.GLFW_KEY_6 -> "6";
            case GLFW.GLFW_KEY_7 -> "7";
            case GLFW.GLFW_KEY_8 -> "8";
            case GLFW.GLFW_KEY_9 -> "9";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_TAB -> "Tab";
            case GLFW.GLFW_KEY_LEFT -> "Left";
            case GLFW.GLFW_KEY_RIGHT -> "Right";
            case GLFW.GLFW_KEY_UP -> "Up";
            case GLFW.GLFW_KEY_DOWN -> "Down";
            default -> InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString();
        };
    }
    
    public String getKeybind() {
        return keybind;
    }
    
    public void setKeybind(String keybind) {
        this.keybind = keybind;
        updateMessage();
    }
    
    public boolean isListening() {
        return listening;
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
    
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Custom rendering with highlight when listening
        if (listening) {
            // Yellow border when listening
            graphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, 0xFFFFAA00);
        }
        
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }
}
