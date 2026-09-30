package zzik2.barched.mixin.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.Barched;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {
    @Inject(method = "bootStrap", at = @At("TAIL"))
    private static void barched$registerNautilusEquipmentBehaviors(CallbackInfo ci) {
        OptionalDispenseItemBehavior behavior = new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(direction);

                for (AbstractNautilus nautilus : source.level().getEntitiesOfClass(AbstractNautilus.class, new AABB(targetPos))) {
                    if (nautilus.canUseSlot(EquipmentSlot.BODY) && nautilus.getBodyArmorItem().isEmpty()) {
                        nautilus.setItemSlot(EquipmentSlot.BODY, stack.split(1));
                        nautilus.setPersistenceRequired();
                        this.setSuccess(true);
                        return stack;
                    }
                }

                return super.execute(source, stack);
            }
        };

        DispenserBlock.registerBehavior(Barched.Items.COPPER_NAUTILUS_ARMOR, behavior);
        DispenserBlock.registerBehavior(Barched.Items.IRON_NAUTILUS_ARMOR, behavior);
        DispenserBlock.registerBehavior(Barched.Items.GOLDEN_NAUTILUS_ARMOR, behavior);
        DispenserBlock.registerBehavior(Barched.Items.DIAMOND_NAUTILUS_ARMOR, behavior);
        DispenserBlock.registerBehavior(Barched.Items.NETHERITE_NAUTILUS_ARMOR, behavior);

        OptionalDispenseItemBehavior saddleBehavior = new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(direction);

                for (LivingEntity target : source.level().getEntitiesOfClass(
                        LivingEntity.class,
                        new AABB(targetPos),
                        entity -> entity instanceof Saddleable saddleable
                                && !saddleable.isSaddled()
                                && saddleable.isSaddleable())) {
                    ((Saddleable) target).equipSaddle(stack.split(1), SoundSource.BLOCKS);
                    if (target instanceof Mob mob) {
                        mob.setPersistenceRequired();
                    }
                    this.setSuccess(true);
                    return stack;
                }

                return super.execute(source, stack);
            }
        };

        DispenserBlock.registerBehavior(Items.SADDLE, saddleBehavior);
    }
}
