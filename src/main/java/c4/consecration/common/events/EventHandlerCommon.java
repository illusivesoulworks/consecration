/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.events;

import c4.consecration.Consecration;
import c4.consecration.common.blocks.BlockBlessedTrail;
import c4.consecration.common.blocks.BlockHolyWater;
import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.capabilities.IUndying;
import c4.consecration.common.config.ConfigHandler;
import c4.consecration.common.init.ConsecrationBlocks;
import c4.consecration.common.init.ConsecrationFluids;
import c4.consecration.common.init.ConsecrationItems;
import c4.consecration.common.util.UndeadHelper;
import c4.consecration.integrations.ModuleCompatibility;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;

public class EventHandlerCommon {

    private static final Field CONVERSION_TIME = ReflectionHelper.findField(EntityZombieVillager.class,
            "conversionTime", "field_82234_d");

    @SubscribeEvent
    public void onZombieVillagerUpdate(LivingEvent.LivingUpdateEvent evt) {
        EntityLivingBase living = evt.getEntityLiving();

        if (!living.world.isRemote && living instanceof EntityZombieVillager) {
            EntityZombieVillager zombieVillager = (EntityZombieVillager)living;

            if (zombieVillager.isConverting()) {
                int conversionTime = getConversionTime(zombieVillager);
                conversionTime -= getConversionSpeed(zombieVillager);
                setConversionTime(zombieVillager, conversionTime);
            }
        }
    }

    private int getConversionSpeed(EntityZombieVillager zombieVillager) {
        int speed = 0;

        if (zombieVillager.world.rand.nextFloat() < 0.01F)
        {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k = (int)zombieVillager.posX - 4; k < (int)zombieVillager.posX + 4 && j < 14; ++k)
            {
                for (int l = (int)zombieVillager.posY - 4; l < (int)zombieVillager.posY + 4 && j < 14; ++l)
                {
                    for (int i1 = (int)zombieVillager.posZ - 4; i1 < (int)zombieVillager.posZ + 4 && j < 14; ++i1)
                    {
                        Block block = zombieVillager.world.getBlockState(blockpos$mutableblockpos.setPos(k, l, i1)).getBlock();

                        if (block instanceof BlockHolyWater || block instanceof BlockBlessedTrail)
                        {
                            if (zombieVillager.world.rand.nextFloat() < 0.3F)
                            {
                                speed += 3;
                            }

                            j++;
                        }
                    }
                }
            }
        }

        return speed;
    }

    private int getConversionTime(EntityZombieVillager zombieVillager) {

        try {
            return CONVERSION_TIME.getInt(zombieVillager);
        } catch (IllegalAccessException e) {
            Consecration.logger.log(Level.ERROR, "Error getting conversionTime for Zombie Villager " + zombieVillager);
        }
        return 0;
    }

    private void setConversionTime(EntityZombieVillager zombieVillager, int conversionTime) {

        try {
            CONVERSION_TIME.setInt(zombieVillager, conversionTime);
        } catch (IllegalAccessException e) {
            Consecration.logger.log(Level.ERROR, "Error setting conversionTime for Zombie Villager " + zombieVillager);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onSpawnCheck(LivingSpawnEvent.CheckSpawn evt) {
        World world = evt.getWorld();
        BlockPos pos = evt.getEntityLiving().getPosition();

        if (world.getBlockState(pos).getBlock() == ConsecrationBlocks.blessedTrail
                || world.getBlockState(pos.down(2)).getBlock() == ConsecrationBlocks.blessedTrail) {

            if (UndeadHelper.isUndead(evt.getEntityLiving())) {
                evt.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onPriestInteract(PlayerInteractEvent.EntityInteract evt) {
        Entity target = evt.getTarget();

        if (!target.world.isRemote && target instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager)target;
            VillagerRegistry.VillagerProfession profession = villager.getProfessionForge();

            if (profession.getRegistryName().toString().equalsIgnoreCase("minecraft:priest")) {
                ItemStack stack = evt.getItemStack();
                Item item = stack.getItem();
                EntityPlayer player = evt.getEntityPlayer();
                ItemStack output = ItemStack.EMPTY;
                String messageKey = "consecration.benediction.";

                if (item == Items.GLOWSTONE_DUST) {
                    output = new ItemStack(ConsecrationItems.blessedDust, stack.getCount());
                    messageKey += "dust";
                } else if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                    IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler
                            .FLUID_HANDLER_ITEM_CAPABILITY, null);
                    FluidStack fluidStack = FluidUtil.getFluidContained(stack);

                    if (fluidStack != null && fluidStack.getFluid() == FluidRegistry.WATER) {

                        if (item == Items.WATER_BUCKET) {
                            output = FluidUtil.getFilledBucket(new FluidStack(ConsecrationFluids.HOLY_WATER,
                                    Fluid.BUCKET_VOLUME));
                        } else {
                            fluidStack = fluidHandler.drain(fluidStack, true);
                            fluidHandler.fill(new FluidStack(ConsecrationFluids.HOLY_WATER, fluidStack.amount), true);
                            output = stack.copy();
                        }
                        messageKey += "water";
                    }
                }

                if (!output.isEmpty()) {
                    if (player.experienceLevel < 1 && !player.capabilities.isCreativeMode) {
                        messageKey = "consecration.benediction.power";
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_VILLAGER_NO,
                                SoundCategory.NEUTRAL, 1.0F,
                                (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F + 1.0F);
                    } else {
                        player.onEnchant(output, ConfigHandler.blessingCost);
                        stack.shrink(output.getCount());
                        ItemHandlerHelper.giveItemToPlayer(player, output, player.inventory.currentItem);
                        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                                SoundCategory.BLOCKS, 2.0F, player.world.rand.nextFloat() * 0.5f + 1.5f);
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_VILLAGER_YES,
                                SoundCategory.NEUTRAL, 1.0F,
                                (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F + 1.0F);
                    }
                    player.sendStatusMessage(new TextComponentTranslation(messageKey), true);
                    evt.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent evt) {
        EntityLivingBase entityLivingBase = evt.getEntityLiving();
        DamageSource source = evt.getSource();

        if (!entityLivingBase.getEntityWorld().isRemote && source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            IUndying undying = CapabilityUndying.getUndying(attacker);

            if (undying != null) {
                int level = 0;

                for (ItemStack stack : entityLivingBase.getArmorInventoryList()) {
                    if (!stack.isEmpty()) {
                        boolean flag = UndeadHelper.isHolyArmor(stack);

                        if (!flag) {

                            for (ModuleCompatibility module : ModuleCompatibility.getLoadedMods().values()) {

                                if (module.processArmor(entityLivingBase, stack, source)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }

                        if (flag) {
                            level++;
                        }
                    }
                }

                if (level > 0 && entityLivingBase.getEntityWorld().rand.nextFloat() < 0.15F * (float)level ) {
                    undying.setSmite(ConfigHandler.holy.smiteDuration * 20);
                }
            }
        }
    }
}
