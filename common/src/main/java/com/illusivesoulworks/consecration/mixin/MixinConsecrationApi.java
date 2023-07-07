package com.illusivesoulworks.consecration.mixin;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.common.impl.ConsecrationApiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConsecrationApi.class)
public class MixinConsecrationApi {

  @Inject(at = @At("HEAD"), method = "getInstance", remap = false, cancellable = true)
  private static void consecration$getInstance(CallbackInfoReturnable<ConsecrationApi> cir) {
    cir.setReturnValue(ConsecrationApiImpl.INSTANCE);
  }
}
