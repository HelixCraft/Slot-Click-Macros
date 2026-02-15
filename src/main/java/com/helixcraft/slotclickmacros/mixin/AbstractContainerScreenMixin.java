package com.helixcraft.slotclickmacros.mixin;

import com.helixcraft.slotclickmacros.playback.MacroPlayer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to detect when a container screen is opened,
 * triggering macro playback if a macro is queued.
 */
@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    
    @Shadow
    protected AbstractContainerMenu menu;
    
    /**
     * Called when the container screen is initialized (opened).
     * This triggers macro playback if there's a queued macro.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void onScreenOpened(CallbackInfo ci) {
        MacroPlayer player = MacroPlayer.getInstance();
        if (player.hasQueuedMacros()) {
            player.onContainerOpened(menu);
        }
    }
}
