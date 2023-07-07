package com.illusivesoulworks.consecration;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;

public class ExampleMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        ConsecrationConstants.LOG.info("Hello Fabric world!");
        ConsecrationCommonMod.setup();
        
        // Some code like events require special initialization from the
        // loader specific code.
        ItemTooltipCallback.EVENT.register(ConsecrationCommonMod::onItemTooltip);
        EntityApiLookup.get()
    }
}
