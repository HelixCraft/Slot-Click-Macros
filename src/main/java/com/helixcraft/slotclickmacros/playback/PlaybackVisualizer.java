package com.helixcraft.slotclickmacros.playback;

import com.helixcraft.slotclickmacros.data.MacroAction;

/**
 * Manages visual feedback during macro playback.
 * Tracks which slot is currently being interacted with.
 */
public class PlaybackVisualizer {
    private static PlaybackVisualizer instance;
    
    private int currentSlotId = -1;
    private long highlightStartTime = 0;
    private static final long HIGHLIGHT_DURATION = 100; // milliseconds
    
    private PlaybackVisualizer() {}
    
    public static PlaybackVisualizer getInstance() {
        if (instance == null) {
            instance = new PlaybackVisualizer();
        }
        return instance;
    }
    
    /**
     * Highlights a slot that is being clicked.
     * 
     * @param action The action being executed
     */
    public void highlightSlot(MacroAction action) {
        this.currentSlotId = action.slotId();
        this.highlightStartTime = System.currentTimeMillis();
    }
    
    /**
     * Checks if a slot should be highlighted.
     * 
     * @param slotId The slot ID to check
     * @return true if the slot should be highlighted
     */
    public boolean shouldHighlight(int slotId) {
        if (currentSlotId == -1) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - highlightStartTime;
        if (elapsed > HIGHLIGHT_DURATION) {
            currentSlotId = -1;
            return false;
        }
        
        return slotId == currentSlotId;
    }
    
    /**
     * Clears any active highlight.
     */
    public void clearHighlight() {
        currentSlotId = -1;
    }
}
