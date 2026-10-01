package zzik2.mombackport.mixin.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import zzik2.mombackport.bridge.DamageSourcesBridge;
import zzik2.mombackport.bridge.item.ItemBridge;

@Mixin(MaceItem.class)
public abstract class MaceItemMixin implements ItemBridge {

    @Shadow
    public static boolean canSmashAttack(LivingEntity arg) {
        return false;
    }

    @Override
    public @Nullable DamageSource getItemDamageSource(LivingEntity livingEntity) {
        return canSmashAttack(livingEntity) ? ((DamageSourcesBridge) livingEntity.damageSources()).mace(livingEntity) : this.super$getItemDamageSource(livingEntity);
    }
}
