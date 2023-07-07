package com.illusivesoulworks.consecration.mixin;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class ExampleMixin {
    
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        
        ConsecrationConstants.LOG.info("This line is printed by an example mod mixin from Fabric!");
        ConsecrationConstants.LOG.info("MC Version: {}", Minecraft.getInstance().getVersionType());
        ConsecrationConstants.LOG.info("Classloader: {}", this.getClass().getClassLoader());
    }
}