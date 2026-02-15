package com.helixcraft.slotclickmacros.gui;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.helixcraft.slotclickmacros.config.ModConfig;
import com.helixcraft.slotclickmacros.io.MacroFileManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Main configuration screen using Cloth Config API.
 * Provides settings for default delay, record keybind, and macro management.
 */
public class ModConfigScreen {
    
    /**
     * Creates the config screen.
     * 
     * @param parent Parent screen to return to
     * @param config Mod configuration
     * @param fileManager Macro file manager
     * @return The config screen
     */
    public static Screen create(Screen parent, ModConfig config, MacroFileManager fileManager) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("Slot Click Macros"))
            .transparentBackground()
            .setSavingRunnable(() -> {
                config.save();
                SlotClickMacros.LOGGER.info("Config auto-saved");
            });
        
        // Enable auto-save on close (no Save/Quit buttons)
        builder.setDoesConfirmSave(false);
        builder.setShouldListSmoothScroll(true);
        
        // General Settings Category
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        
        // Default Delay - Slider with integrated text field
        IntegerSliderEntry delaySlider = entryBuilder.startIntSlider(
            Component.literal("Default Delay (ms)"),
            config.getDefaultDelay(),
            10, 500
        )
        .setDefaultValue(100)
        .setTextGetter(value -> Component.literal(value + " ms"))
        .setTooltip(
            Component.literal("Delay between macro actions during playback"),
            Component.literal("§7Range: 10-500 milliseconds"),
            Component.literal("§a✓ Recommended: 100-150ms (safe for most servers)"),
            Component.literal("§e⚠ 80-100ms: Risky on strict servers"),
            Component.literal("§c⚠ Below 80ms: High ban risk!"),
            Component.literal("§7Lower = faster but more detectable")
        )
        .setSaveConsumer(delay -> {
            config.setDefaultDelay(delay);
            // Update MacroPlayer delay
            try {
                com.helixcraft.slotclickmacros.playback.MacroPlayer.getInstance().setDefaultDelay(delay);
            } catch (Exception e) {
                SlotClickMacros.LOGGER.warn("Could not update MacroPlayer delay", e);
            }
        })
        .build();
        
        general.addEntry(delaySlider);
        
        // Random Delay Range - Additional randomness for anti-detection
        IntegerSliderEntry randomDelaySlider = entryBuilder.startIntSlider(
            Component.literal("Random Delay Range (ms)"),
            config.getRandomDelayRange(),
            0, 100
        )
        .setDefaultValue(50)
        .setTextGetter(value -> {
            if (value == 0) {
                return Component.literal("Disabled");
            }
            return Component.literal("0-" + value + " ms");
        })
        .setTooltip(
            Component.literal("Adds random delay (0 to this value) to each action"),
            Component.literal("§7Helps avoid detection by anti-cheat systems"),
            Component.literal("§7Example: Base 100ms + Random 50ms = 100-150ms"),
            Component.literal("§a✓ Recommended: 50-70ms for good variance"),
            Component.literal("§70 = Disabled (not recommended for servers)")
        )
        .setSaveConsumer(range -> {
            config.setRandomDelayRange(range);
            // Update MacroPlayer random range
            try {
                com.helixcraft.slotclickmacros.playback.MacroPlayer.getInstance().setRandomDelayRange(range);
            } catch (Exception e) {
                SlotClickMacros.LOGGER.warn("Could not update MacroPlayer random range", e);
            }
        })
        .build();
        
        general.addEntry(randomDelaySlider);
        
        // Record Keybind with custom button
        general.addEntry(new KeybindConfigEntry(
            Component.literal("Record Keybind"),
            config.getRecordKey(),
            newKeybind -> {
                config.setRecordKey(newKeybind);
                SlotClickMacros.LOGGER.info("Set record keybind to: {}", newKeybind);
            }
        ));
        
        general.addEntry(entryBuilder.startTextDescription(
            Component.literal("§7Press this key to start/stop recording macros")
        ).build());
        
        // Stop Keybind with custom button
        general.addEntry(new KeybindConfigEntry(
            Component.literal("Stop Macros Keybind"),
            config.getStopKey(),
            newKeybind -> {
                config.setStopKey(newKeybind);
                SlotClickMacros.LOGGER.info("Set stop keybind to: {}", newKeybind);
            }
        ));
        
        general.addEntry(entryBuilder.startTextDescription(
            Component.literal("§7Press this key to stop all running/queued macros")
        ).build());
        
        // Macro Management Category
        ConfigCategory macros = builder.getOrCreateCategory(Component.literal("Macro Management"));
        
        List<String> macroList = fileManager.listMacros();
        
        if (macroList.isEmpty()) {
            macros.addEntry(entryBuilder.startTextDescription(
                Component.literal("§7No macros found.")
            ).build());
            
            macros.addEntry(entryBuilder.startTextDescription(
                Component.literal("§ePress your record keybind to start recording!")
            ).build());
        } else {
            macros.addEntry(entryBuilder.startTextDescription(
                Component.literal("§aFound " + macroList.size() + " macro(s)")
            ).build());
            
            macros.addEntry(entryBuilder.startTextDescription(
                Component.literal("§7Name | Keybind | Delete")
            ).build());
            
            // Add each macro as a list entry
            for (String macroName : macroList) {
                macros.addEntry(new MacroListEntry(
                    macroName,
                    config,
                    fileManager,
                    deletedMacro -> {
                        // Refresh screen after delete, staying on Macro Management tab
                        Minecraft.getInstance().execute(() -> {
                            Screen newScreen = create(parent, config, fileManager);
                            Minecraft.getInstance().setScreen(newScreen);
                            // Try to switch to Macro Management category
                            try {
                                if (newScreen instanceof me.shedaniel.clothconfig2.gui.ClothConfigScreen) {
                                    me.shedaniel.clothconfig2.gui.ClothConfigScreen clothScreen = 
                                        (me.shedaniel.clothconfig2.gui.ClothConfigScreen) newScreen;
                                    // Set selected category to index 1 (Macro Management)
                                    clothScreen.selectedCategoryIndex = 1;
                                }
                            } catch (Exception e) {
                                SlotClickMacros.LOGGER.warn("Could not switch to Macro Management tab", e);
                            }
                        });
                    }
                ));
            }
        }
        
        // Help Category
        ConfigCategory help = builder.getOrCreateCategory(Component.literal("Help"));
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§6How to use:")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§71. Press your record keybind to start recording")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§72. Perform actions in any container")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§73. Press record keybind again to stop")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§74. Save your macro with a name")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§75. Assign a keybind in Macro Management")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§76. Press keybind, then open container to play")
        ).build());
        
        // Anti-Cheat Safety Warning
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§c⚠ ANTI-CHEAT SAFETY:")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§7Recommended settings for safety:")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§a✓ Normal servers: 100ms + 50ms random")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§a✓ Strict servers: 150ms + 70ms random")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§e⚠ Below 80ms: High ban risk!")
        ).build());
        
        help.addEntry(entryBuilder.startTextDescription(
            Component.literal("§7Lower delays = faster but more detectable")
        ).build());
        
        return builder.build();
    }
}
