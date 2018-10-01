/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration;

import c4.consecration.common.events.EventHandlerCommon;
import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.config.ConfigHandler;
import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.common.init.ConsecrationDamageSources;
import c4.consecration.common.init.ConsecrationItems;
import c4.consecration.common.init.ConsecrationPotions;
import c4.consecration.common.trading.ListPotionForEmeralds;
import c4.consecration.common.util.ConfigHelper;
import c4.consecration.common.util.UndeadHelper;
import c4.consecration.common.util.UndeadRegistry;
import c4.consecration.integrations.ModuleCompatibility;
import c4.consecration.proxy.IProxy;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(	modid = Consecration.MODID,
		name = Consecration.MODNAME,
		version = Consecration.MODVER,
		dependencies = "required-after:forge@[14.23.4.2705,)",
        acceptedMinecraftVersions = "[1.12.2, 1.13)",
        certificateFingerprint = "5d5b8aee896a4f5ea3f3114784742662a67ad32f")
public class Consecration {

    public static final String MODID = "consecration";
    public static final String MODNAME = "Consecration";
    public static final String MODVER = "0.0.10";

    @SidedProxy(clientSide = "c4.consecration.proxy.ClientProxy", serverSide = "c4.consecration.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static Consecration instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        proxy.preInit(evt);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        CapabilityUndying.register();
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ConsecrationItems.fireBomb, new BehaviorProjectileDispense()
        {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
            {
                return new EntityFireBomb(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        for (String modid : ModuleCompatibility.compatDeps.keySet()) {
            if (Loader.isModLoaded(modid)) {
                try {
                    ModuleCompatibility.compatDeps.get(modid).newInstance();
                } catch (Exception e) {
                    Consecration.logger.log(Level.ERROR, "Error loading compatibility module " + ModuleCompatibility.compatDeps.get(modid));
                }
            }
        }
        proxy.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        VillagerRegistry.VillagerProfession priest = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:priest"));
        if (priest != null) {
            VillagerRegistry.VillagerCareer priestCareer = priest.getCareer(0);
            priestCareer.addTrade(2, new ListPotionForEmeralds(ConsecrationPotions.HOLY, new EntityVillager.PriceInfo(4, 6)));
            priestCareer.addTrade(3, new ListPotionForEmeralds(ConsecrationPotions.STRONG_HOLY, new EntityVillager.PriceInfo(6, 9)));
        }
        ConfigHelper.parseDimensionConfigs();
        ConfigHelper.registerConfigs();
        for (ModuleCompatibility module : ModuleCompatibility.loadedMods.values()) {
            module.register();
        }
        proxy.postInit(evt);
    }

    @Mod.EventHandler
    public void onMessageReceived(FMLInterModComms.IMCEvent evt) {
        UndeadRegistry.processIMC(evt);
    }

    @Mod.EventHandler
    public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
        FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
