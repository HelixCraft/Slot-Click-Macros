package com.helixcraft.slotclickmacros.gui;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.helixcraft.slotclickmacros.config.ModConfig;
import com.helixcraft.slotclickmacros.io.MacroFileManager;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.events.KeyEvent;
import net.minecraft.client.gui.components.events.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Custom list entry for displaying a macro with name, keybind button, and delete button.
 * Each macro gets one row with all controls.
 */
public class MacroListEntry extends AbstractConfigListEntry<String> {
    private final String macroName;
    private final ModConfig config;
    private final MacroFileManager fileManager;
    private final java.util.function.Consumer<String> onDelete;
    
    private KeybindButton keybindButton;
    private Button deleteButton;
    
    public MacroListEntry(String macroName, ModConfig config, MacroFileManager fileManager, java.util.function.Consumer<String> onDelete) {
        super(Component.literal(macroName), false);
        this.macroName = macroName;
        this.config = config;
        this.fileManager = fileManager;
        this.onDelete = onDelete;
    }
    
    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        
        // Render macro name
        graphics.drawString(
            Minecraft.getInstance().font,
            "§e" + macroName,
            x + 5,
            y + 6,
            0xFFFFFF,
            false
        );
        
        // Initialize buttons if needed
        if (keybindButton == null) {
            String currentKeybind = config.getMacroKeybind(macroName);
            
            // Keybind button (positioned in the middle)
            keybindButton = new KeybindButton(
                x + entryWidth - 180,
                y + 2,
                120,
                20,
                currentKeybind,
                newKeybind -> {
                    if (newKeybind.isEmpty()) {
                        config.removeMacroKeybind(macroName);
                        SlotClickMacros.LOGGER.info("Removed keybind for macro: {}", macroName);
                    } else {
                        config.setMacroKeybind(macroName, newKeybind);
                        SlotClickMacros.LOGGER.info("Set keybind '{}' for macro: {}", newKeybind, macroName);
                    }
                }
            );
            
            // Delete button (X button on the right)
            deleteButton = Button.builder(
                Component.literal("§c✖"),
                button -> {
                    boolean deleted = fileManager.deleteMacro(macroName);
                    if (deleted) {
                        config.removeMacroKeybind(macroName);
                        SlotClickMacros.LOGGER.info("Deleted macro: {}", macroName);
                        
                        if (Minecraft.getInstance().player != null) {
                            Minecraft.getInstance().player.displayClientMessage(
                                Component.literal("§c[Macro] Deleted: " + macroName),
                                false
                            );
                        }
                        
                        // Notify parent to refresh
                        if (onDelete != null) {
                            onDelete.accept(macroName);
                        }
                    }
                }
            )
            .bounds(x + entryWidth - 55, y + 2, 50, 20)
            .build();
        } else {
            // Update button positions
            keybindButton.setX(x + entryWidth - 180);
            keybindButton.setY(y + 2);
            deleteButton.setX(x + entryWidth - 55);
            deleteButton.setY(y + 2);
        }
        
        // Render buttons
        keybindButton.render(graphics, mouseX, mouseY, delta);
        deleteButton.render(graphics, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        
        if (keybindButton != null && keybindButton.mouseClicked(event, consumed)) {
            return true;
        }
        if (deleteButton != null && deleteButton.mouseClicked(event, consumed)) {
            return true;
        }
        return super.mouseClicked(event, consumed);
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (keybindButton != null && keybindButton.isListening()) {
            return keybindButton.keyPressed(event);
        }
        return super.keyPressed(event);
    }
    
    @Override
    public String getValue() {
        return macroName;
    }
    
    @Override
    public Optional<String> getDefaultValue() {
        return Optional.of(macroName);
    }
    
    @Override
    public void save() {
        // Saving is handled by the buttons directly
    }
    
    @Override
    public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
        return Collections.emptyList();
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        if (keybindButton != null && deleteButton != null) {
            return java.util.Arrays.asList(keybindButton, deleteButton);
        }
        return Collections.emptyList();
    }
    
    @Override
    public int getItemHeight() {
        return 24; // Height of one row
    }
}
