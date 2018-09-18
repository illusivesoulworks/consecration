/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import c4.consecration.Consecration;
import c4.consecration.common.entities.EntityFireArrow;
import c4.consecration.common.entities.EntityFireBomb;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod.EventBusSubscriber(modid = Consecration.MODID)
public class ConsecrationEntities {

    @SubscribeEvent
    public static void init(RegistryEvent.Register<EntityEntry> evt) {
        int id = 1;
        evt.getRegistry().registerAll(
                EntityEntryBuilder.create()
                        .entity(EntityFireBomb.class)
                        .id(new ResourceLocation(Consecration.MODID, "_entity_fire_bomb"), id++)
                        .name("entity_fire_bomb")
                        .tracker(64, 5, true)
                        .build(),
                EntityEntryBuilder.create()
                        .entity(EntityFireArrow.class)
                        .id(new ResourceLocation(Consecration.MODID, "_entity_fire_arrow"), id++)
                        .name("entity_fire_arrow")
                        .tracker(64, 5, true)
                        .build()
        );
    }
}
