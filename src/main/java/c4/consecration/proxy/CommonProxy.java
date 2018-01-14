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
import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.common.trading.ListPotionForEmeralds;
import c4.consecration.init.ModBlocks;
import c4.consecration.init.ModItems;
import c4.consecration.init.ModPotions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent evt) {
    }

    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.fireBomb, new BehaviorProjectileDispense()
        {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
            {
                return new EntityFireBomb(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
    }

    public void postInit(FMLPostInitializationEvent evt) {
        VillagerRegistry.VillagerProfession priest = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:priest"));
        if (priest != null) {
            new VillagerRegistry.VillagerCareer(priest, "paladin");
            VillagerRegistry.VillagerCareer priestCareer = priest.getCareer(0);
            priestCareer.addTrade(2, new ListPotionForEmeralds(ModPotions.HOLY, new EntityVillager.PriceInfo(4, 6)));
            priestCareer.addTrade(3, new ListPotionForEmeralds(ModPotions.STRONG_HOLY, new EntityVillager.PriceInfo(6, 9)));
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
        evt.getRegistry().register(entry);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        evt.getRegistry().register(ModBlocks.hallowedGrounds);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().register(new ItemBlock(ModBlocks.hallowedGrounds).setRegistryName(ModBlocks.hallowedGrounds.getRegistryName()));
        evt.getRegistry().register(ModItems.blessedDust);
        evt.getRegistry().register(ModItems.fireStick);
        evt.getRegistry().register(ModItems.fireBomb);
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> evt) {
        evt.getRegistry().register(ModPotions.HOLY);
        evt.getRegistry().register(ModPotions.STRONG_HOLY);
        evt.getRegistry().register(ModPotions.ULTIMATE_HOLY);
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> evt) {
        evt.getRegistry().register(ModPotions.HOLY_POTION);
        evt.getRegistry().register(ModPotions.SMITE_POTION);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 0)), ModPotions.HOLY);
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 1)), ModPotions.ULTIMATE_HOLY);
        PotionHelper.addMix(ModPotions.HOLY, Items.GLOWSTONE_DUST, ModPotions.STRONG_HOLY);
    }
}
