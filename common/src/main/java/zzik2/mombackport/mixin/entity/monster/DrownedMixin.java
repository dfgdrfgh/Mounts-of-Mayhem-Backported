package zzik2.mombackport.mixin.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import zzik2.mombackport.MomBackport;

@Mixin(Drowned.class)
public abstract class DrownedMixin extends Zombie {

    public DrownedMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        return itemStack.is(MomBackport.ItemTags.SPEARS) ? false : super.wantsToPickUp(itemStack);
    }
}
