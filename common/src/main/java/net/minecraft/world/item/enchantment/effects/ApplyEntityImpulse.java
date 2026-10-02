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
import org.joml.Quaternionf;
import org.joml.Vector3d;
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
      // 26.3 rotates the configured local impulse by the entity's look
      // quaternion directly. Recreate Entity#getLookQuaternion here because
      // 1.21.1 does not expose that helper.
      float radians = ((float)Math.PI / 180.0F);
      Quaternionf look = new Quaternionf()
              .rotationY(-entity.getYRot() * radians)
              .rotateX(entity.getXRot() * radians);
      Vector3d direction = look.transform(this.direction.x, this.direction.y, this.direction.z, new Vector3d())
              .mul(this.coordinateScale.x, this.coordinateScale.y, this.coordinateScale.z)
              .mul(this.magnitude.calculate(i));
      entity.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));

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