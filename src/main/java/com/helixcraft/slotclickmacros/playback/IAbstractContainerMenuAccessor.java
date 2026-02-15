package com.helixcraft.slotclickmacros.playback;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;

/**
 * Accessor interface to call protected methods in AbstractContainerMenu.
 * Implemented via mixin.
 */
public interface IAbstractContainerMenuAccessor {
    void slotClickMacros$doClick(int slotId, int button, ClickType clickType, Player player);
}
