/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package top.theillusivec4.consecration;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.consecration.common.capability.UndyingCapability;

@Mod(Consecration.MODID)
public class Consecration {

  public static final String MODID = "consecration";

  public Consecration() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    UndyingCapability.register();
  }
}
