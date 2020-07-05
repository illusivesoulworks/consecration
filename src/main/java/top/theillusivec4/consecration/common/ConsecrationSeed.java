/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Consecration, a mod made for Minecraft.
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common;

import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.InterModComms.IMCMessage;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationApi.IMC;
import top.theillusivec4.consecration.common.ConsecrationConfig.Config;

public class ConsecrationSeed {

  public static void imc(Stream<IMCMessage> stream) {
    stream.forEach(imcMessage -> {
      Object message = imcMessage.getMessageSupplier().get();
      String method = imcMessage.getMethod();

      if (message instanceof String) {
        String content = (String) message;

        switch (method) {
          case IMC.UNDEAD:
            ConsecrationApi.addUndead(content);
            break;
          case IMC.HOLY_ENTITY:
            ConsecrationApi.addHolyEntity(content);
            break;
          case IMC.HOLY_EFFECT:
            ConsecrationApi.addHolyEffect(content);
            break;
          case IMC.HOLY_ITEM:
            ConsecrationApi.addHolyItem(content);
            break;
          case IMC.HOLY_ENCHANTMENT:
            ConsecrationApi.addHolyEnchantment(content);
            break;
          case IMC.HOLY_MATERIAL:
            ConsecrationApi.addHolyMaterial(content);
            break;
          case IMC.HOLY_DAMAGE:
            ConsecrationApi.addHolyDamage(content);
            break;
        }
      } else if (message instanceof BiFunction) {

        switch (method) {
          case IMC.HOLY_ATTACK:
            ConsecrationApi
                .addHolyAttack((BiFunction<LivingEntity, DamageSource, Boolean>) message);
            break;
          case IMC.HOLY_PROTECTION:
            ConsecrationApi
                .addHolyProtection((BiFunction<LivingEntity, DamageSource, Integer>) message);
            break;
        }
      }
    });
  }

  public static void config() {
    Config config = ConsecrationConfig.CONFIG;
    config.undeadList.get().forEach(ConsecrationApi::addUndead);
    config.holyEntities.get().forEach(ConsecrationApi::addHolyEntity);
    config.holyEffects.get().forEach(ConsecrationApi::addHolyEffect);
    config.holyItems.get().forEach(ConsecrationApi::addHolyItem);
    config.holyEnchantments.get().forEach(ConsecrationApi::addHolyEnchantment);
    config.holyDamage.get().forEach(ConsecrationApi::addHolyDamage);
    config.holyMaterials.get().forEach(ConsecrationApi::addHolyMaterial);
  }
}
