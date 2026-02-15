package com.helixcraft.slotclickmacros.integration;

import com.helixcraft.slotclickmacros.SlotClickMacrosClient;
import com.helixcraft.slotclickmacros.gui.ModConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu integration to provide config screen access.
 * This class is only loaded if Mod Menu is present.
 */
public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            SlotClickMacrosClient client = SlotClickMacrosClient.getInstance();
            if (client != null) {
                return ModConfigScreen.create(
                    parent,
                    client.getConfig(),
                    client.getFileManager()
                );
            }
            return null;
        };
    }
}
