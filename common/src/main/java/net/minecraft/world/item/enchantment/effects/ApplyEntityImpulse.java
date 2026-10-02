package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;
import zzik2.barched.bridge.Vec3Bridge;
import zzik2.barched.bridge.entity.PlayerBridge;

public record ApplyEntityImpulse(Vec3 direction, Vec3 coordinateScale, LevelBasedValue magnitude) implements EnchantmentEntityEffect {

   public static final MapCodec<ApplyEntityImpulse> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3.CODEC.fieldOf("direction").forGetter(ApplyEntityImpulse::direction), Vec3.CODEC.fieldOf("coordinate_scale").forGetter(ApplyEntityImpulse::coordinateScale), LevelBasedValue.CODEC.fieldOf("magnitude").forGetter(ApplyEntityImpulse::magnitude)).apply(instance, ApplyEntityImpulse::new);
   });
   private static final int POST_IMPULSE_CONTEXT_RESET_GRACE_TIME_TICKS = 10;

   public ApplyEntityImpulse(Vec3 direction, Vec3 coordinateScale, LevelBasedValue magnitude) {
      this.direction = direction;
      this.coordinateScale = coordinateScale;
      this.magnitude = magnitude;
   }

   @Override
   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
      Vec3 vec32 = entity.getLookAngle();
      Vec3 vec33 = ((Vec3Bridge) vec32).addLocalCoordinates(this.direction).multiply(this.coordinateScale).scale((double)this.magnitude.calculate(i));
      entity.addDeltaMovement(vec33);

      // 26.3 sends the impulse to the lunging player immediately instead of
      // waiting for the normal entity tracker. For other entities, the old
      // 1.21.1 hasImpulse flag is the equivalent of modern syncVelocity.
      if (entity instanceof ServerPlayer serverPlayer) {
         serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
         entity.hurtMarked = false;
         entity.hasImpulse = false;
         ((PlayerBridge) serverPlayer).applyPostImpulseGraceTime(10);
      } else {
         entity.hasImpulse = true;
      }

   }

   @Override
   public MapCodec<ApplyEntityImpulse> codec() {
      return CODEC;
   }

   @Override
   public Vec3 direction() {
      return this.direction;
   }

   @Override
   public Vec3 coordinateScale() {
      return this.coordinateScale;
   }

   @Override
   public LevelBasedValue magnitude() {
      return this.magnitude;
   }
}