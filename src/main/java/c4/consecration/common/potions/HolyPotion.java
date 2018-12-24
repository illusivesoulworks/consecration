/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.potions;

import c4.consecration.Consecration;
import c4.consecration.common.init.ConsecrationDamageSources;
import c4.consecration.common.util.UndeadHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class HolyPotion extends Potion {

    private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class, "startConverting", "func_191991_a", UUID.class, Integer.TYPE);

    public HolyPotion() {
        super(false, 0xffffff);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource,
                             @Nonnull EntityLivingBase entityLivingBaseIn, int amplifier, double health) {

        if (entityLivingBaseIn instanceof EntityZombieVillager) {
            convertZombieVillager((EntityZombieVillager)entityLivingBaseIn, indirectSource, 1800 >> amplifier);
        } else if (UndeadHelper.isUndead(entityLivingBaseIn)) {
            entityLivingBaseIn.attackEntityFrom(ConsecrationDamageSources.HOLY, (float)(8 << amplifier));
        } else {
            entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, amplifier));
            entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 600, amplifier));
        }
    }

    private void convertZombieVillager(EntityZombieVillager zombieVillager, @Nullable Entity source,
                                       int conversionTime) {

        if (zombieVillager.isConverting()) {
            return;
        }

        UUID uuid = source != null && source instanceof EntityPlayer ? source.getUniqueID() : null;

        try {
            START_CONVERTING.invoke(zombieVillager, uuid, zombieVillager.world.rand.nextInt(200) + conversionTime);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Consecration.logger.log(Level.ERROR, "Error in startConverting for entity " + zombieVillager);
        }
    }
}
