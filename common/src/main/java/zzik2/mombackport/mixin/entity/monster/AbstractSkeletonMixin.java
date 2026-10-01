package zzik2.mombackport.mixin.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zzik2.mombackport.MomBackport;
import zzik2.zreflex.mixin.ModifyAccess;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Monster {

    protected AbstractSkeletonMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    @Shadow
    SoundEvent getStepSound() {
        return null;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/AbstractSkeleton;isSunBurnTick()Z"))
    private boolean mombackport$isSunBurnTick(AbstractSkeleton instance) {
        if (instance.getType() == MomBackport.EntityType.PARCHED) {
            return false;
        }
        return this.isSunBurnTick();
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        return itemStack.is(MomBackport.ItemTags.SPEARS) ? false : super.wantsToPickUp(itemStack);
    }
}
