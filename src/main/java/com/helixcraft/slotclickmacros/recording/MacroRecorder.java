package com.helixcraft.slotclickmacros.recording;

import com.helixcraft.slotclickmacros.SlotClickMacros;
import com.helixcraft.slotclickmacros.data.Macro;
import com.helixcraft.slotclickmacros.data.MacroAction;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Manages the recording state and captures slot click actions.
 */
public class MacroRecorder {
    private static MacroRecorder instance;
    
    private RecordingState state = RecordingState.IDLE;
    private Macro currentMacro;

    
    private MacroRecorder() {}
    
    public static MacroRecorder getInstance() {
        if (instance == null) {
            instance = new MacroRecorder();
        }
        return instance;
    }
    
    /**
     * Starts recording a new macro.
     */
    public void startRecording() {
        if (state != RecordingState.IDLE) {
            SlotClickMacros.LOGGER.warn("Cannot start recording: already recording or saving");
            return;
        }
        
        currentMacro = new Macro("temp_macro");
        state = RecordingState.RECORDING;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(
                Component.literal("§a[Macro] Recording started"), 
                true // Display as action bar
            );
        }
        
        SlotClickMacros.LOGGER.info("Started recording macro");
    }
    
    /**
     * Stops recording and triggers the save dialog.
     */
    public void stopRecording(Consumer<Macro> onComplete) {
        if (state != RecordingState.RECORDING) {
            SlotClickMacros.LOGGER.warn("Cannot stop recording: not currently recording");
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(
                Component.literal("§e[Macro] Recording stopped. Actions recorded: " + currentMacro.size()), 
                true
            );
        }
        
        SlotClickMacros.LOGGER.info("Stopped recording macro with {} actions", currentMacro.size());
        
        // Only open save dialog if we actually recorded something
        if (currentMacro.size() > 0) {
            state = RecordingState.SAVING;
            
            // Trigger save dialog (will be opened by client)
            if (onComplete != null) {
                onComplete.accept(currentMacro);
            }
        } else {
            // No actions recorded, just reset
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    Component.literal("§7[Macro] No actions recorded, not saving"), 
                    true
                );
            }
            finishRecording();
        }
    }
    
    /**
     * Records a slot click action.
     * This is called by the mixin when a slot is clicked.
     * 
     * @param slotId The ID of the clicked slot
     * @param clickType The Minecraft click type
     * @param button The button/key pressed
     */
    public void recordAction(int slotId, net.minecraft.world.inventory.ClickType clickType, int button) {
        if (state != RecordingState.RECORDING) {
            return; // Not recording, ignore
        }
        
        // Handle drag actions specially
        if (isDragAction(clickType, button)) {
            recordDragAction(slotId, clickType, button);
        } else {
            // Normal click action
            MacroClickType macroClickType = MacroClickType.fromMinecraftClickType(clickType, button);
            MacroAction action = new MacroAction(slotId, macroClickType, button);
            
            currentMacro.addAction(action);
            
            SlotClickMacros.LOGGER.debug("Recorded action: slot={}, type={}, button={}", slotId, macroClickType, button);
        }
    }
    
    /**
     * Checks if this is a drag action that needs special handling.
     */
    private boolean isDragAction(net.minecraft.world.inventory.ClickType clickType, int button) {
        // Right-click drag (distribution) and left-click drag (splitting)
        // These are handled differently in Minecraft
        return clickType == net.minecraft.world.inventory.ClickType.PICKUP && 
               (button == 1 || button == 0); // Both left and right can be drag
    }
    
    /**
     * Records drag actions with proper handling for distribution and splitting.
     */
    private void recordDragAction(int slotId, net.minecraft.world.inventory.ClickType clickType, int button) {
        // For drag actions, we need to record multiple PICKUP actions
        // to simulate the drag behavior during playback
        MacroClickType macroClickType = MacroClickType.fromMinecraftClickType(clickType, button);
        MacroAction action = new MacroAction(slotId, macroClickType, button);
        
        currentMacro.addAction(action);
        
        SlotClickMacros.LOGGER.debug("Recorded drag action: slot={}, type={}, button={}", slotId, macroClickType, button);
    }
    
    /**
     * Toggles recording on/off.
     */
    public void toggleRecording(Consumer<Macro> onComplete) {
        if (state == RecordingState.RECORDING) {
            stopRecording(onComplete);
        } else if (state == RecordingState.IDLE) {
            startRecording();
        }
    }
    
    /**
     * Cancels the current recording without saving.
     */
    public void cancelRecording() {
        if (state == RecordingState.IDLE) {
            return;
        }
        
        SlotClickMacros.LOGGER.info("Cancelled recording");
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(
                Component.literal("§c[Macro] Recording cancelled"), 
                true
            );
        }
        
        finishRecording();
    }
    
    /**
     * Resets the recorder state after completing a save.
     */
    public void finishRecording() {
        state = RecordingState.IDLE;
        currentMacro = null;
    }
    
    /**
     * Gets the current macro being recorded.
     * 
     * @return The current macro, or null if not recording
     */
    public Macro getCurrentMacro() {
        return currentMacro;
    }
    
    public RecordingState getState() {
        return state;
    }
    
    public boolean isRecording() {
        return state == RecordingState.RECORDING;
    }
    
    public boolean isSaving() {
        return state == RecordingState.SAVING;
    }
    
    public enum RecordingState {
        IDLE,
        RECORDING,
        SAVING
    }
}
