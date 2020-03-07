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
import top.theillusivec4.consecration.api.ConsecrationAPI;
import top.theillusivec4.consecration.api.ConsecrationAPI.IMC;
import top.theillusivec4.consecration.common.ConsecrationConfig.Server;

public class ConsecrationSeed {

  public static void imc(Stream<IMCMessage> stream) {
    stream.forEach(imcMessage -> {
      Object message = imcMessage.getMessageSupplier().get();
      String method = imcMessage.getMethod();

      if (message instanceof String) {
        String content = (String) message;

        switch (method) {
          case IMC.UNDEAD:
            ConsecrationAPI.addUndead(content);
            break;
          case IMC.HOLY_ENTITY:
            ConsecrationAPI.addHolyEntity(content);
            break;
          case IMC.HOLY_EFFECT:
            ConsecrationAPI.addHolyEffect(content);
            break;
          case IMC.HOLY_ITEM:
            ConsecrationAPI.addHolyItem(content);
            break;
          case IMC.HOLY_ENCHANTMENT:
            ConsecrationAPI.addHolyEnchantment(content);
            break;
          case IMC.HOLY_MATERIAL:
            ConsecrationAPI.addHolyMaterial(content);
            break;
          case IMC.HOLY_DAMAGE:
            ConsecrationAPI.addHolyDamage(content);
            break;
        }
      } else if (message instanceof BiFunction) {
        BiFunction<LivingEntity, DamageSource, Boolean> content = (BiFunction<LivingEntity, DamageSource, Boolean>) message;

        switch (method) {
          case IMC.HOLY_ATTACK:
            ConsecrationAPI.addHolyAttack(content);
            break;
          case IMC.HOLY_PROTECTION:
            ConsecrationAPI.addHolyProtection(content);
            break;
        }
      }
    });
  }

  public static void config() {
    Server config = ConsecrationConfig.SERVER;
    config.undeadList.get().forEach(ConsecrationAPI::addUndead);
    config.holyEntities.get().forEach(ConsecrationAPI::addHolyEntity);
    config.holyEffects.get().forEach(ConsecrationAPI::addHolyEffect);
    config.holyItems.get().forEach(ConsecrationAPI::addHolyItem);
    config.holyEnchantments.get().forEach(ConsecrationAPI::addHolyEnchantment);
    config.holyDamage.get().forEach(ConsecrationAPI::addHolyDamage);
    config.holyMaterials.get().forEach(ConsecrationAPI::addHolyMaterial);
  }
}
