package com.helixcraft.slotclickmacros.recording;

import net.minecraft.world.inventory.ClickType;

/**
 * Simplified click type enum that represents the various ways a player can interact with slots.
 * This enum maps to Minecraft's ClickType for both recording and playback.
 */
public enum MacroClickType {
    PICKUP,           // Normal left/right click
    QUICK_MOVE,       // Shift-click
    SWAP,             // Number key (when not a hotbar swap)
    CLONE,            // Middle-click (creative mode)
    THROW,            // Q key or click outside
    QUICK_CRAFT,      // Drag crafting
    PICKUP_ALL,       // Double-click to gather matching items
    
    // Hotbar slot swaps (keys 1-9)
    HOTBAR_1,
    HOTBAR_2,
    HOTBAR_3,
    HOTBAR_4,
    HOTBAR_5,
    HOTBAR_6,
    HOTBAR_7,
    HOTBAR_8,
    HOTBAR_9;
    
    /**
     * Converts Minecraft's ClickType to our MacroClickType.
     * Note: SWAP actions need special handling to detect hotbar key presses.
     * 
     * @param clickType The Minecraft ClickType
     * @param button The button/key pressed (used for SWAP to determine hotbar slot)
     * @return The corresponding MacroClickType
     */
    public static MacroClickType fromMinecraftClickType(ClickType clickType, int button) {
        return switch (clickType) {
            case PICKUP -> PICKUP;
            case QUICK_MOVE -> QUICK_MOVE;
            case SWAP -> {
                // button 0-8 corresponds to hotbar slots 1-9
                if (button >= 0 && button <= 8) {
                    yield MacroClickType.values()[HOTBAR_1.ordinal() + button];
                }
                yield SWAP;
            }
            case CLONE -> CLONE;
            case THROW -> THROW;
            case QUICK_CRAFT -> QUICK_CRAFT;
            case PICKUP_ALL -> PICKUP_ALL;
        };
    }
    
    /**
     * Converts this MacroClickType back to Minecraft's ClickType for playback.
     * For InteractionManager.clickSlot(), we need to use ClickType enum.
     * 
     * @return The Minecraft ClickType
     */
    public ClickType toMinecraftClickType() {
        return switch (this) {
            case PICKUP -> ClickType.PICKUP;
            case QUICK_MOVE -> ClickType.QUICK_MOVE;
            case SWAP -> ClickType.SWAP;
            case CLONE -> ClickType.CLONE;
            case THROW -> ClickType.THROW;
            case QUICK_CRAFT -> ClickType.QUICK_CRAFT;
            case PICKUP_ALL -> ClickType.PICKUP_ALL;
            case HOTBAR_1, HOTBAR_2, HOTBAR_3, HOTBAR_4, HOTBAR_5, 
                 HOTBAR_6, HOTBAR_7, HOTBAR_8, HOTBAR_9 -> ClickType.SWAP;
        };
    }
    
    /**
     * Gets the button/key data for playback.
     * For hotbar slots, returns the slot index (0-8).
     * 
     * @return The button data
     */
    public int getButtonData() {
        if (this.ordinal() >= HOTBAR_1.ordinal() && this.ordinal() <= HOTBAR_9.ordinal()) {
            return this.ordinal() - HOTBAR_1.ordinal(); // Returns 0-8
        }
        return 0;
    }
}
