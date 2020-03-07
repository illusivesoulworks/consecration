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
