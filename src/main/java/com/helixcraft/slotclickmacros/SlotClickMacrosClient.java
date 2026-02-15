package com.helixcraft.slotclickmacros;

import com.helixcraft.slotclickmacros.gui.ModConfigScreen;
import com.helixcraft.slotclickmacros.gui.SaveMacroScreen;
import com.helixcraft.slotclickmacros.io.MacroFileManager;
import com.helixcraft.slotclickmacros.keybinds.KeybindManager;
import com.helixcraft.slotclickmacros.playback.MacroPlayer;
import com.helixcraft.slotclickmacros.recording.MacroRecorder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.Map;

/**
 * Client-side initialization for the SlotClickMacros mod.
 * Handles keybind registration and client-side event listeners.
 */
public class SlotClickMacrosClient implements ClientModInitializer {
    
    private static SlotClickMacrosClient instance;
    
    private KeybindManager keybindManager;
    private MacroFileManager fileManager;
    private com.helixcraft.slotclickmacros.config.ModConfig config;
    
    // Debounce tracking for keybinds
    private boolean recordKeyWasPressed = false;
    private boolean stopKeyWasPressed = false;
    private final java.util.Set<String> macroKeysPressed = new java.util.HashSet<>();
    
    @Override
    public void onInitializeClient() {
        instance = this;
        SlotClickMacros.LOGGER.info("Initializing Slot Click Macros Client");
        
        // Get game directory
        Path gameDir = Minecraft.getInstance().gameDirectory.toPath();
        
        // Initialize managers
        config = new com.helixcraft.slotclickmacros.config.ModConfig(gameDir);
        fileManager = new MacroFileManager(gameDir);
        keybindManager = new KeybindManager();
        MacroPlayer.initialize(fileManager);
        MacroPlayer.getInstance().setDefaultDelay(config.getDefaultDelay());
        MacroPlayer.getInstance().setRandomDelayRange(config.getRandomDelayRange());
        
        // Register commands
        new com.helixcraft.slotclickmacros.commands.SlotMacroCommand(fileManager, MacroPlayer.getInstance()).register();
        
        // Register client tick event for keybind handling
        registerKeybindListener();
        
        SlotClickMacros.LOGGER.info("Slot Click Macros Client initialized successfully");
    }
    
    /**
     * Registers the client tick event listener for keybind handling.
     */
    private void registerKeybindListener() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MacroRecorder recorder = MacroRecorder.getInstance();
            
            // Check if config screen key was pressed
            while (keybindManager.getOpenConfigKey().consumeClick()) {
                Screen configScreen = ModConfigScreen.create(
                    client.screen,
                    config,
                    fileManager
                );
                client.setScreen(configScreen);
            }
            
            // Check record keybind from config (using raw key state)
            String recordKeybind = config.getRecordKey();
            if (!recordKeybind.isEmpty() && keybindManager.isKeybindStringPressed(recordKeybind)) {
                // Debounce: only trigger once per press
                if (!recordKeyWasPressed) {
                    recordKeyWasPressed = true;
                    
                    if (recorder.isRecording()) {
                        //Stop recording and open save dialog if actions > 0
                        recorder.stopRecording(macro -> {
                            if (macro.size() > 0) {
                                // Open save dialog
                                Minecraft.getInstance().execute(() -> {
                                    client.setScreen(new SaveMacroScreen(
                                        null,
                                        macro,
                                        fileManager,
                                        name -> {
                                            SlotClickMacros.LOGGER.info("Macro saved as: {}", name);
                                        }
                                    ));
                                });
                            }
                        });
                    } else {
                        // Start recording
                        recorder.startRecording();
                    }
                }
            } else {
                recordKeyWasPressed = false;
            }
            
            // Check stop keybind from config
            String stopKeybind = config.getStopKey();
            if (!stopKeybind.isEmpty() && keybindManager.isKeybindStringPressed(stopKeybind)) {
                // Debounce: only trigger once per press
                if (!stopKeyWasPressed) {
                    stopKeyWasPressed = true;
                    
                    // Stop all macro playback
                    if (MacroPlayer.getInstance().stopAll()) {
                        if (client.player != null) {
                            client.player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§c[Macro] Stopped all playback"),
                                true
                            );
                        }
                        SlotClickMacros.LOGGER.info("Stop keybind pressed - stopped all macros");
                    } else {
                        if (client.player != null) {
                            client.player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§7[Macro] No macros running"),
                                true
                            );
                        }
                    }
                }
            } else {
                stopKeyWasPressed = false;
            }
            
            // Handle macro playback keybinds from config
            for (Map.Entry<String, String> entry : config.getMacroKeybinds().entrySet()) {
                String macroName = entry.getKey();
                String keybindString = entry.getValue();
                
                if (!keybindString.isEmpty() && keybindManager.isKeybindStringPressed(keybindString)) {
                    // Debounce
                    if (!macroKeysPressed.contains(macroName)) {
                        macroKeysPressed.add(macroName);
                        
                        // Queue the macro for playback
                        MacroPlayer.getInstance().queueMacro(macroName);
                        SlotClickMacros.LOGGER.debug("Macro keybind pressed: {}", macroName);
                    }
                } else {
                    macroKeysPressed.remove(macroName);
                }
            }
        });
    }
    
    public KeybindManager getKeybindManager() {
        return keybindManager;
    }
    
    public MacroFileManager getFileManager() {
        return fileManager;
    }
    
    public com.helixcraft.slotclickmacros.config.ModConfig getConfig() {
        return config;
    }
    
    public static SlotClickMacrosClient getInstance() {
        return instance;
    }
}
