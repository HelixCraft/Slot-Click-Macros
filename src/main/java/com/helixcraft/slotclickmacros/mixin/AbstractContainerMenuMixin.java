package com.helixcraft.slotclickmacros.mixin;

import com.helixcraft.slotclickmacros.recording.MacroRecorder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept slot click events for macro recording.
 * Targets AbstractContainerMenu's doClick method which handles all slot interactions.
 */
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    
    /**
     * Intercepts slot clicks and records them if recording is active.
     * 
     * @param slotId The ID of the clicked slot
     * @param button The button/key pressed (for SWAP, this is the hotbar slot 0-8)
     * @param clickType The type of click performed
     * @param player The player who clicked
     * @param ci Callback info
     */
    @Inject(
        method = "doClick",
        at = @At("HEAD")
    )
    private void onSlotClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        // Only record on the client side
        if (player.level().isClientSide()) {
            MacroRecorder recorder = MacroRecorder.getInstance();
            if (recorder.isRecording()) {
                recorder.recordAction(slotId, clickType, button);
            }
        }
    }
}
