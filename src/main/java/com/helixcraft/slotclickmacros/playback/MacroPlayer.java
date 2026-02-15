package com.helixcraft.slotclickmacros.playback;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.helixcraft.slotclickmacros.data.Macro;
import com.helixcraft.slotclickmacros.data.MacroAction;
import com.helixcraft.slotclickmacros.io.MacroFileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages macro playback, including queueing and execution.
 */
public class MacroPlayer {
    private static MacroPlayer instance;
    
    private final Queue<Macro> playbackQueue = new ConcurrentLinkedQueue<>();
    private final MacroFileManager fileManager;
    private int defaultDelay = 100; // milliseconds (changed from 50ms for anti-cheat safety)
    private int randomDelayRange = 50; // 0-100ms random addition (default: 50ms)
    private final java.util.Random random = new java.util.Random();
    
    private boolean isPlaying = false;
    
    private MacroPlayer(MacroFileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    public static void initialize(MacroFileManager fileManager) {
        instance = new MacroPlayer(fileManager);
    }
    
    public static MacroPlayer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MacroPlayer not initialized!");
        }
        return instance;
    }
    
    /**
     * Queues a macro for playback by name.
     * The macro will be executed when the player opens the next container.
     * 
     * @param macroName The name of the macro to play
     * @return true if the macro was queued, false if it doesn't exist or an error occurred
     */
    public boolean queueMacro(String macroName) {
        try {
            Macro macro = fileManager.loadMacro(macroName);
            playbackQueue.offer(macro);
            
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage(
                    Component.literal("§a[Macro] Queued '" + macroName + "' - open a container to play"),
                    true
                );
            }
            
            SlotClickMacros.LOGGER.info("Queued macro '{}' for playback", macroName);
            return true;
        } catch (IOException e) {
            SlotClickMacros.LOGGER.error("Failed to queue macro '{}'", macroName, e);
            
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage(
                    Component.literal("§c[Macro] Failed to load macro: " + macroName),
                    false
                );
            }
            return false;
        }
    }
    
    /**
     * Called when a container screen is opened.
     * If there's a queued macro, it will be played.
     * 
     * @param containerMenu The container menu that was opened
     */
    public void onContainerOpened(AbstractContainerMenu containerMenu) {
        if (playbackQueue.isEmpty() || isPlaying) {
            return;
        }
        
        Macro macro = playbackQueue.poll();
        if (macro != null) {
            playMacro(macro, containerMenu);
        }
    }
    
    /**
     * Plays a macro in the given container.
     * 
     * @param macro The macro to play
     * @param containerMenu The initial container (may change during playback)
     */
    private void playMacro(Macro macro, AbstractContainerMenu containerMenu) {
        isPlaying = true;
        
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(
                Component.literal("§e[Macro] Playing '" + macro.getName() + "' (" + macro.size() + " actions)"),
                true
            );
        }
        
        SlotClickMacros.LOGGER.info("Playing macro '{}' with {} actions", macro.getName(), macro.size());
        
        // Execute actions with delay
        new Thread(() -> {
            try {
                // Use array to allow modification in lambda
                final int[] previousContainerId = {containerMenu.containerId};
                
                for (int i = 0; i < macro.getActions().size(); i++) {
                    MacroAction action = macro.getActions().get(i);
                    final int actionIndex = i;
                    
                    // Execute action on the main thread
                    // Get the current container menu dynamically for each action
                    Minecraft.getInstance().execute(() -> {
                        LocalPlayer currentPlayer = Minecraft.getInstance().player;
                        if (currentPlayer != null) {
                            AbstractContainerMenu currentContainer = currentPlayer.containerMenu;
                            
                            // Log container changes for debugging
                            if (currentContainer != null && currentContainer.containerId != previousContainerId[0]) {
                                SlotClickMacros.LOGGER.info("Container changed: {} -> {} (action {})", 
                                    previousContainerId[0], currentContainer.containerId, actionIndex);
                                previousContainerId[0] = currentContainer.containerId;
                            }
                            
                            executeAction(action, currentPlayer);
                        }
                    });
                    
                    // Calculate delay with randomness for natural behavior
                    int actualDelay = calculateDelay();
                    SlotClickMacros.LOGGER.debug("Action {} delay: {}ms (base: {}ms, random range: {}ms)", 
                        actionIndex, actualDelay, defaultDelay, randomDelayRange);
                    
                    // Wait for the calculated delay
                    Thread.sleep(actualDelay);
                    
                    // Additional wait if container changed (give server time to send new container)
                    // Check if container ID changed after the action
                    LocalPlayer checkPlayer = Minecraft.getInstance().player;
                    if (checkPlayer != null && checkPlayer.containerMenu != null) {
                        int currentId = checkPlayer.containerMenu.containerId;
                        if (currentId != previousContainerId[0]) {
                            // Container changed! Wait extra time for server synchronization
                            SlotClickMacros.LOGGER.debug("Detected container change, waiting extra 100ms for sync");
                            Thread.sleep(100); // Extra delay for container sync
                            previousContainerId[0] = currentId;
                        }
                    }
                }
                
                // Playback complete
                Minecraft.getInstance().execute(() -> {
                    if (player != null) {
                        player.displayClientMessage(
                            Component.literal("§a[Macro] Playback complete"),
                            true
                        );
                    }
                    isPlaying = false;
                    SlotClickMacros.LOGGER.info("Macro playback complete");
                });
                
            } catch (InterruptedException e) {
                SlotClickMacros.LOGGER.error("Macro playback interrupted", e);
                isPlaying = false;
            }
        }, "MacroPlayback").start();
    }
    
    /**
     * Executes a single macro action by calling the game mode's click handler.
     * This properly sends packets to the server for all click types.
     * Gets the current container menu dynamically to handle server-side container changes.
     * 
     * @param action The action to execute
     * @param player The player
     */
    private void executeAction(MacroAction action, LocalPlayer player) {
        if (player == null) {
            SlotClickMacros.LOGGER.warn("Player is null, skipping action");
            return;
        }
        
        var minecraft = Minecraft.getInstance();
        var gameMode = minecraft.gameMode;
        
        if (gameMode == null) {
            SlotClickMacros.LOGGER.error("GameMode is null, cannot execute slot click!");
            return;
        }
        
        // Get the CURRENT container menu (important for server-side container changes!)
        AbstractContainerMenu containerMenu = player.containerMenu;
        
        if (containerMenu == null) {
            SlotClickMacros.LOGGER.warn("No container menu open, skipping action");
            return;
        }
        
        int slotId = action.slotId();
        
        // Validate slot ID against CURRENT container
        if (slotId < 0 || slotId >= containerMenu.slots.size()) {
            SlotClickMacros.LOGGER.warn("Invalid slot ID {} in macro (current container has {} slots)", 
                                         slotId, containerMenu.slots.size());
            return;
        }
        
        // Trigger visual highlight
        PlaybackVisualizer.getInstance().highlightSlot(action);
        
        try {
            // Convert our MacroClickType to Minecraft's ClickType
            net.minecraft.world.inventory.ClickType clickType = action.clickType().toMinecraftClickType();
            
            // Use the recorded button data
            // This is critical for PICKUP clicks where button 0 = left click, button 1 = right click
            int buttonData = action.button();
            
            // For hotbar swaps, override with the hotbar slot index
            if (action.clickType().name().startsWith("HOTBAR_")) {
                buttonData = action.clickType().getButtonData();
            }
            
            // Get the CURRENT container ID (changes when server opens new container!)
            int containerId = containerMenu.containerId;
            
            // Use handleInventoryMouseClick which properly sends the ServerboundContainerClickPacket
            // This method handles all the packet construction and sending internally
            gameMode.handleInventoryMouseClick(
                containerId,    // Current container sync ID
                slotId,        // Slot ID to click
                buttonData,    // Button data (0 for left click, 1 for right, etc.)
                clickType,     // Type of click action
                player         // The player performing the action
            );
            
            SlotClickMacros.LOGGER.debug("Executed action: containerId={}, slot={}, button={}, type={}", 
                                          containerId, slotId, buttonData, clickType);
                                          
        } catch (Exception e) {
            SlotClickMacros.LOGGER.error("Error executing macro action for slot {}", slotId, e);
        }
    }
    
    /**
     * Sets the default delay between actions in milliseconds.
     * 
     * @param delay The delay in milliseconds
     */
    public void setDefaultDelay(int delay) {
        this.defaultDelay = Math.max(0, Math.min(1000, delay)); // Clamp between 0-1000ms
    }
    
    public int getDefaultDelay() {
        return defaultDelay;
    }
    
    /**
     * Sets the random delay range for anti-detection.
     * 
     * @param range The maximum random delay to add (0-100ms)
     */
    public void setRandomDelayRange(int range) {
        this.randomDelayRange = Math.max(0, Math.min(100, range));
    }
    
    public int getRandomDelayRange() {
        return randomDelayRange;
    }
    
    /**
     * Calculates the actual delay with randomness for natural behavior.
     * 
     * @return The delay in milliseconds (base + random)
     */
    private int calculateDelay() {
        if (randomDelayRange == 0) {
            return defaultDelay;
        }
        // Add random delay between 0 and randomDelayRange
        int randomAddition = random.nextInt(randomDelayRange + 1);
        return defaultDelay + randomAddition;
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public boolean hasQueuedMacros() {
        return !playbackQueue.isEmpty();
    }
    
    /**
     * Clears all queued macros.
     */
    public void clearQueue() {
        playbackQueue.clear();
        SlotClickMacros.LOGGER.info("Cleared macro playback queue");
    }
    
    /**
     * Stops all macro playback immediately.
     * Clears the queue and stops any currently playing macro.
     * 
     * @return true if something was stopped, false if nothing was running
     */
    public boolean stopAll() {
        boolean hadActivity = !playbackQueue.isEmpty() || isPlaying;
        
        // Clear queue
        playbackQueue.clear();
        
        // Stop current playback
        if (isPlaying) {
            isPlaying = false;
            SlotClickMacros.LOGGER.info("Stopped macro playback");
        }
        
        if (hadActivity) {
            SlotClickMacros.LOGGER.info("Stopped all macro activity (queue cleared, playback stopped)");
        }
        
        return hadActivity;
    }
}
