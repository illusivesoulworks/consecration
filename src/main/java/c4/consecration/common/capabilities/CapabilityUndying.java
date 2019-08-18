/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.capabilities;

import c4.consecration.Consecration;
import c4.consecration.common.config.ConfigHandler;
import c4.consecration.common.init.ConsecrationTriggers;
import c4.consecration.common.util.UndeadHelper;
import c4.consecration.common.util.UndeadRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CapabilityUndying {

    private static final Field TIPPED_ARROW_POTION = ReflectionHelper.findField(EntityTippedArrow.class,
            "potion", "field_184560_g", "g");

    private static final UUID SPEED_MOD = UUID.fromString("b812ef3d-0ef9-4368-845b-fad7003a1f4f");

    @CapabilityInject(IUndying.class)
    public static final Capability<IUndying> UNDYING_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Consecration.MODID, "undying");

    private static final String SMITE_TAG = "smite";

    public static void register() {
        CapabilityManager.INSTANCE.register(IUndying.class, new Capability.IStorage<IUndying>() {
            @Override
            public NBTBase writeNBT(Capability<IUndying> capability, IUndying instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger(SMITE_TAG, instance.getSmite());
                return compound;
            }

            @Override
            public void readNBT(Capability<IUndying> capability, IUndying instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setSmite(compound.getInteger(SMITE_TAG));
            }
        }, Undying::new);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static IUndying getUndying(final EntityLivingBase entityIn) {

        if (entityIn != null && entityIn.hasCapability(UNDYING_CAP, DEFAULT_FACING)) {
            return entityIn.getCapability(UNDYING_CAP, DEFAULT_FACING);
        }

        return null;
    }

    public static ICapabilityProvider createProvider(final IUndying undying) {
        return new Provider(undying, UNDYING_CAP, DEFAULT_FACING);
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<IUndying> capability;
        final EnumFacing facing;
        final IUndying instance;

        Provider(final IUndying instance, final Capability<IUndying> capability, @Nullable final EnumFacing facing) {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing) {
            return capability == getCapability();
        }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return capability == getCapability() ? getCapability().cast(this.instance) : null;
        }

        final Capability<IUndying> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final IUndying getInstance() {
            return instance;
        }

        @Override
        public NBTBase serializeNBT() {
            return getCapability().writeNBT(getInstance(), getFacing());
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            getCapability().readNBT(getInstance(), getFacing(), nbt);
        }
    }

    @Mod.EventBusSubscriber(modid = Consecration.MODID)
    public static class EventHandler {

        private static Set<Integer> dimensions = new HashSet<>();

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
            if (evt.getObject() instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase)evt.getObject();
                if (isAllowedDimension(living)) {
                    evt.addCapability(ID, createProvider(new Undying()));
                }
            }
        }

        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
            EntityLivingBase entitylivingbase = evt.getEntityLiving();

            if (!entitylivingbase.getEntityWorld().isRemote && UndeadHelper.isUndead(entitylivingbase)) {
                IUndying undying = CapabilityUndying.getUndying(entitylivingbase);

                if (undying != null) {
                    IAttributeInstance speedAttribute = entitylivingbase.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    speedAttribute.removeModifier(SPEED_MOD);

                    if (undying.isSmote()) {

                        if (entitylivingbase.ticksExisted % 10 == 0) {
                            WorldServer worldIn = (WorldServer) entitylivingbase.getEntityWorld();
                            worldIn.spawnParticle(EnumParticleTypes.SPELL_INSTANT, entitylivingbase.posX,
                                    entitylivingbase.posY + entitylivingbase.height / 2.0D, entitylivingbase.posZ, 2,
                                    entitylivingbase.width / 2.0D, entitylivingbase.height / 4.0D,
                                    entitylivingbase.width / 2.0D, 0.0D);
                        }
                        undying.decrementSmite();

                    } else if (UndeadHelper.hasHolyPotion(entitylivingbase)) {
                        undying.setSmite(ConfigHandler.holy.smiteDuration * 20);
                    } else if (entitylivingbase.isBurning() && !UndeadRegistry.getUnholyList().contains(EntityList.getKey(entitylivingbase))) {
                        undying.setSmite(ConfigHandler.holy.fireSmiteDuration * 20);
                    } else {

                        if (entitylivingbase.ticksExisted % 20 == 0 && entitylivingbase.getHealth() < entitylivingbase.getMaxHealth()) {
                            entitylivingbase.heal(ConfigHandler.undying.healthRegen);
                        }

                        if (ConfigHandler.undying.speedModifier > 0 && speedAttribute.getModifier(SPEED_MOD) == null) {
                            speedAttribute.applyModifier(new AttributeModifier(SPEED_MOD, "Undead speed", ConfigHandler.undying.speedModifier, 2));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onArrowImpact(ProjectileImpactEvent.Arrow evt) {
            EntityArrow arrow = evt.getArrow();

            if (arrow.shootingEntity instanceof EntityPlayerMP && arrow instanceof EntityTippedArrow) {
                PotionType potionType = null;

                try {
                    potionType = (PotionType)TIPPED_ARROW_POTION.get(arrow);
                } catch (IllegalAccessException e) {
                    Consecration.logger.log(Level.ERROR, "Error catching potion from EntityTippedArrow" + arrow);
                }

                if (potionType != null) {

                    for (PotionEffect effect : potionType.getEffects()) {
                        Potion potion = effect.getPotion();
                        if (UndeadRegistry.getHolyPotions().contains(potion)) {
                            ConsecrationTriggers.SMITE_KILLED.trigger((EntityPlayerMP)arrow.shootingEntity);
                            return;
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent evt) {
            EntityLivingBase entitylivingbase = evt.getEntityLiving();

            if (!entitylivingbase.getEntityWorld().isRemote && UndeadHelper.isUndead(entitylivingbase)) {
                DamageSource source = evt.getSource();

                //Check for "natural" damage
                if (source == DamageSource.OUT_OF_WORLD || source == DamageSource.CRAMMING
                        || source == DamageSource.IN_WALL) {
                    return;
                }
                IUndying undying = CapabilityUndying.getUndying(entitylivingbase);

                if (undying != null) {
                    if (UndeadHelper.doSmite(entitylivingbase, source)) {
                        if (source.isFireDamage()) {
                            undying.setSmite(ConfigHandler.holy.fireSmiteDuration * 20);
                        } else {
                            undying.setSmite(ConfigHandler.holy.smiteDuration * 20);
                        }
                        if (source.getTrueSource() instanceof EntityPlayerMP) {
                            ConsecrationTriggers.SMITE_KILLED.trigger((EntityPlayerMP) source.getTrueSource());
                        }
                    } else if (!source.isDamageAbsolute() && !undying.isSmote()) {

                        if ((source.getTrueSource() != null && (ConfigHandler.undying.reduceDamageVsMobs
                                || source.getTrueSource() instanceof EntityPlayer))) {
                            evt.setAmount(evt.getAmount() * (float) (1 - ConfigHandler.undying.damageReduction));
                        }
                    }
                }
            }
        }

        public static void addDimension(int dimension) {
            dimensions.add(dimension);
        }

        public static boolean isAllowedDimension(EntityLivingBase entity) {
            int dimension = entity.dimension;
            if (dimensions.isEmpty()) {
                return true;
            } else if (ConfigHandler.dimensionPermission == ConfigHandler.PermissionMode.BLACKLIST) {
                return !dimensions.contains(dimension);
            } else {
                return dimensions.contains(dimension);
            }
        }
    }
}
