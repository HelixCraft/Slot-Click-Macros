package com.helixcraft.slotclickmacros.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Config entry that wraps a KeybindButton for use in Cloth Config.
 */
public class KeybindConfigEntry extends AbstractConfigListEntry<String> {
    private final KeybindButton button;
    private final Consumer<String> onChange;
    private String value;
    
    public KeybindConfigEntry(Component fieldName, String initialValue, Consumer<String> onChange) {
        super(fieldName, false);
        this.value = initialValue;
        this.onChange = onChange;
        this.button = new KeybindButton(0, 0, 150, 20, initialValue, newValue -> {
            this.value = newValue;
            if (onChange != null) {
                onChange.accept(newValue);
            }
        });
    }
    
    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        
        // Render field name
        graphics.drawString(
            net.minecraft.client.Minecraft.getInstance().font,
            getFieldName(),
            x,
            y + 6,
            0xFFFFFF,
            false
        );
        
        // Position and render button
        button.setX(x + entryWidth - 155);
        button.setY(y + 2);
        button.render(graphics, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        if (consumed) return false;
        
        if (this.button.mouseClicked(event, consumed)) {
            return true;
        }
        return super.mouseClicked(event, consumed);
    }
    
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (button.isListening()) {
            return button.keyPressed(keyEvent);
        }
        return super.keyPressed(keyEvent);
    }
    
    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public Optional<String> getDefaultValue() {
        return Optional.of("R");
    }
    
    @Override
    public void save() {
        if (onChange != null) {
            onChange.accept(value);
        }
    }
    
    @Override
    public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
        return Collections.emptyList();
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.singletonList(button);
    }
}
