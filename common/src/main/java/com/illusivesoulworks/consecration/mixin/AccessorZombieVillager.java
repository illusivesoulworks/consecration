package com.illusivesoulworks.consecration.mixin;

import java.util.UUID;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ZombieVillager.class)
public interface AccessorZombieVillager {

  @Invoker
  void callStartConverting(UUID source, int conversionTime);
}
