/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.proxy;

import c4.consecration.Consecration;
import c4.consecration.common.EventHandlerCommon;
import c4.consecration.common.EventHandlerUndying;
import c4.consecration.common.blocks.BlockBlessedTrail;
import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.entities.EntityFireArrow;
import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.common.items.ItemBlessedDust;
import c4.consecration.common.items.ItemFireArrow;
import c4.consecration.common.items.ItemFireBomb;
import c4.consecration.common.items.ItemFireStick;
import c4.consecration.common.trading.ListPotionForEmeralds;
import c4.consecration.config.HandlerConfig;
import c4.consecration.init.HolderConsecration;
import c4.consecration.init.PotionsConsecration;
import c4.consecration.integrations.ModuleCompatibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Level;

@Mod.EventBusSubscriber
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent evt) {

    }

    @SuppressWarnings("ConstantConditions")
    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        MinecraftForge.EVENT_BUS.register(new EventHandlerUndying());
        CapabilityUndying.register();
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(HolderConsecration.fireBomb, new BehaviorProjectileDispense()
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
    }

    public void postInit(FMLPostInitializationEvent evt) {
        VillagerRegistry.VillagerProfession priest = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:priest"));
        if (priest != null) {
            new VillagerRegistry.VillagerCareer(priest, "paladin");
            VillagerRegistry.VillagerCareer priestCareer = priest.getCareer(0);
            priestCareer.addTrade(2, new ListPotionForEmeralds(PotionsConsecration.HOLY, new EntityVillager.PriceInfo(4, 6)));
            priestCareer.addTrade(3, new ListPotionForEmeralds(PotionsConsecration.STRONG_HOLY, new EntityVillager.PriceInfo(6, 9)));
        }
        parseDimensionConfigs();
    }

    private static void parseDimensionConfigs() {
        if (HandlerConfig.dimensionList.length > 0) {
            for (String s : HandlerConfig.dimensionList) {
                int dimension = Integer.parseInt(s);
                EventHandlerUndying.dimensions.add(dimension);
            }
        }
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> evt) {
        int id = 1;
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(EntityFireBomb.class)
                .id(new ResourceLocation(Consecration.MODID, "_entity_fire_bomb"), id++)
                .name("entity_fire_bomb")
                .tracker(64, 5, true)
                .build();
        EntityEntry entry2 = EntityEntryBuilder.create()
                .entity(EntityFireArrow.class)
                .id(new ResourceLocation(Consecration.MODID, "_entity_fire_arrow"), id++)
                .name("entity_fire_arrow")
                .tracker(64, 5, true)
                .build();
        evt.getRegistry().registerAll(entry, entry2);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        evt.getRegistry().register(new BlockBlessedTrail());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().registerAll(
                new ItemBlessedDust(),
                new ItemFireArrow(),
                new ItemFireBomb(),
                new ItemFireStick());
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> evt) {
        evt.getRegistry().registerAll(
                PotionsConsecration.HOLY,
                PotionsConsecration.STRONG_HOLY,
                PotionsConsecration.ULTIMATE_HOLY);
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> evt) {
        evt.getRegistry().register(PotionsConsecration.HOLY_POTION);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 0)), PotionsConsecration.HOLY);
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 1)), PotionsConsecration.ULTIMATE_HOLY);
        PotionHelper.addMix(PotionsConsecration.HOLY, Items.GLOWSTONE_DUST, PotionsConsecration.STRONG_HOLY);
    }
}
