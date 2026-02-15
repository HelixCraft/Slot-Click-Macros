package com.helixcraft.slotclickmacros.mixin;

import com.helixcraft.slotclickmacros.playback.IAbstractContainerMenuAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Accessor mixin to expose protected doClick method.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuAccessorMixin implements IAbstractContainerMenuAccessor {
    
    @Shadow
    protected abstract void doClick(int slotId, int button, ClickType clickType, Player player);
    
    @Override
    public void slotClickMacros$doClick(int slotId, int button, ClickType clickType, Player player) {
        this.doClick(slotId, button, clickType, player);
    }
}
