package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
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
      // Match the proven 1.21.1 Backported-Spears approach: derive the
      // local impulse from the entity's actual look vector instead of
      // reconstructing it from raw yaw/pitch fields. This avoids the
      // off-axis correction seen when those rotations disagree.
      Vec3 look = entity.getLookAngle();
      Vec3 impulse = ((Vec3Bridge) look)
              .addLocalCoordinates(this.direction)
              .multiply(this.coordinateScale)
              .scale((double)this.magnitude.calculate(i));
      entity.addDeltaMovement(impulse);

      // Use 1.21.1's native motion-sync flags instead of sending an
      // immediate velocity packet. This mirrors how Vanilla Backport adapts
      // Lunge to the older networking model and avoids a second off-axis
      // correction being applied to the local player.
      entity.hurtMarked = true;
      entity.hasImpulse = true;
      if (entity instanceof net.minecraft.world.entity.player.Player player) {
         ((PlayerBridge) player).applyPostImpulseGraceTime(10);
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