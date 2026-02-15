package com.helixcraft.slotclickmacros.mixin;

import com.helixcraft.slotclickmacros.playback.PlaybackVisualizer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to render red highlight over slots during macro playback.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenRenderMixin {
    
    /**
     * Renders after the slot is drawn, adding our red highlight if needed.
     */
    @Inject(
        method = "renderSlot",
        at = @At("TAIL")
    )
    private void onRenderSlot(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        if (slot == null) return;
        
        PlaybackVisualizer visualizer = PlaybackVisualizer.getInstance();
        
        if (visualizer.shouldHighlight(slot.index)) {
            // Push pose and translate to high Z-level to draw on top of items
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 400.0F); // z=400 to ensure visibility over items
            
            // Draw red semi-transparent overlay (50% opacity)
            int x = slot.x;
            int y = slot.y;
            
            // Red color with 50% alpha (0x80 = 128/255)
            graphics.fill(x, y, x + 16, y + 16, 0x80FF0000);
            
            graphics.pose().popPose();
        }
    }
}
