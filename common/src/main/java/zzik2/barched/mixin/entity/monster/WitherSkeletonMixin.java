package zzik2.barched.mixin.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import zzik2.barched.Barched;

@Mixin(WitherSkeleton.class)
public abstract class WitherSkeletonMixin extends AbstractSkeleton {

    protected WitherSkeletonMixin(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return !stack.is(Barched.ItemTags.WITHER_SKELETON_DISLIKED_WEAPONS) && super.canHoldItem(stack);
    }
}
