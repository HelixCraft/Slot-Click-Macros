package com.helixcraft.slotclickmacros.gui;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.helixcraft.slotclickmacros.data.Macro;
import com.helixcraft.slotclickmacros.io.MacroFileManager;
import com.helixcraft.slotclickmacros.recording.MacroRecorder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Save dialog screen for naming and saving recorded macros.
 * Similar to Replay Mod's save dialog style.
 */
public class SaveMacroScreen extends Screen {
    private final Screen parent;
    private final Macro macro;
    private final MacroFileManager fileManager;
    private final Consumer<String> onSave;
    
    private EditBox nameField;
    private Button saveButton;
    private Button cancelButton;
    
    private String errorMessage = "";
    
    public SaveMacroScreen(Screen parent, Macro macro, MacroFileManager fileManager, Consumer<String> onSave) {
        super(Component.literal("Save Macro"));
        this.parent = parent;
        this.macro = macro;
        this.fileManager = fileManager;
        this.onSave = onSave;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Name input field
        nameField = new EditBox(
            this.font,
            centerX - 100,
            centerY - 30,
            200,
            20,
            Component.literal("Macro Name")
        );
        nameField.setMaxLength(32);
        nameField.setValue("macro_" + System.currentTimeMillis() / 1000);
        nameField.setHint(Component.literal("Enter macro name..."));
        addRenderableWidget(nameField);
        setInitialFocus(nameField);
        
        // Save button
        saveButton = Button.builder(
            Component.literal("Save"),
            button -> saveMacro()
        )
        .bounds(centerX - 100, centerY + 10, 95, 20)
        .build();
        addRenderableWidget(saveButton);
        
        // Cancel button
        cancelButton = Button.builder(
            Component.literal("Cancel"),
            button -> {
                MacroRecorder.getInstance().finishRecording();
                this.minecraft.setScreen(parent);
            }
        )
        .bounds(centerX + 5, centerY + 10, 95, 20)
        .build();
        addRenderableWidget(cancelButton);
    }
    
    private void saveMacro() {
        String name = nameField.getValue().trim();
        
        // Validate name
        if (name.isEmpty()) {
            errorMessage = "§cName cannot be empty!";
            return;
        }
        
        if (!name.matches("[a-zA-Z0-9_-]+")) {
            errorMessage = "§cOnly letters, numbers, _ and - allowed!";
            return;
        }
        
        // Check if macro already exists
        if (fileManager.listMacros().contains(name)) {
            errorMessage = "§cMacro '" + name + "' already exists!";
            return;
        }
        
        // Save the macro
        try {
            // Create a new macro with the correct name
            Macro namedMacro = new Macro(name, macro.getActions());
            fileManager.saveMacro(namedMacro);
            
            SlotClickMacros.LOGGER.info("Saved macro '{}' with {} actions", name, namedMacro.size());
            
            // Notify callback
            if (onSave != null) {
                onSave.accept(name);
            }
            
            // Show success message
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(
                    Component.literal("§a[Macro] Saved: " + name + " (" + namedMacro.size() + " actions)"),
                    false
                );
            }
            
            // Finish recording and close screen
            MacroRecorder.getInstance().finishRecording();
            this.minecraft.setScreen(parent);
            
        } catch (Exception e) {
            SlotClickMacros.LOGGER.error("Failed to save macro", e);
            errorMessage = "§cFailed to save: " + e.getMessage();
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // In 1.21.6+, we need to render a simple background without blur
        // to avoid "Can only blur once per frame" error
        // Use renderTransparentBackground instead of renderBackground
        this.renderTransparentBackground(graphics);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Title
        graphics.drawCenteredString(
            this.font,
            "§6Save Macro",
            centerX,
            centerY - 60,
            0xFFFFFF
        );
        
        // Info text
        graphics.drawCenteredString(
            this.font,
            "§7Recorded " + macro.size() + " actions",
            centerX,
            centerY - 45,
            0xAAAAAA
        );
        
        // Label for name field
        graphics.drawString(
            this.font,
            "Macro Name:",
            centerX - 100,
            centerY - 42,
            0xFFFFFF
        );
        
        // Error message
        if (!errorMessage.isEmpty()) {
            graphics.drawCenteredString(
                this.font,
                errorMessage,
                centerX,
                centerY + 35,
                0xFF5555
            );
        }
        
        // Render widgets
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter key saves
        if (keyCode == 257 || keyCode == 335) { // ENTER or NUMPAD_ENTER
            saveMacro();
            return true;
        }
        
        // Escape cancels
        if (keyCode == 256) { // ESCAPE
            MacroRecorder.getInstance().finishRecording();
            this.minecraft.setScreen(parent);
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void onClose() {
        MacroRecorder.getInstance().finishRecording();
        this.minecraft.setScreen(parent);
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
