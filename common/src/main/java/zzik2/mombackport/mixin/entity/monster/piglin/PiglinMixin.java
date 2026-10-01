package zzik2.mombackport.mixin.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.mombackport.MomBackport;
import zzik2.zreflex.mixin.ModifyAccess;

import java.util.ArrayList;
import java.util.List;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends AbstractPiglin {

    @ModifyAccess(access = Opcodes.ACC_PUBLIC, removeFinal = true)
    @Shadow protected static ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mombackport$clinit(CallbackInfo ci) {
        List<MemoryModuleType<?>> modifiedList = new ArrayList<>(MEMORY_TYPES);
        modifiedList.addAll(List.of(MomBackport.MemoryModuleType.SPEAR_FLEEING_TIME, MomBackport.MemoryModuleType.SPEAR_FLEEING_POSITION, MomBackport.MemoryModuleType.SPEAR_CHARGE_POSITION, MomBackport.MemoryModuleType.SPEAR_ENGAGE_TIME, MomBackport.MemoryModuleType.SPEAR_STATUS));
        MEMORY_TYPES = ImmutableList.copyOf(modifiedList);
    }

    public PiglinMixin(EntityType<? extends AbstractPiglin> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyArg(method = "createSpawnWeapon", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V", ordinal = 1))
    private ItemLike mombackport$Item(ItemLike arg) {
        return this.random.nextInt(10) == 0 ? MomBackport.Items.GOLDEN_SPEAR : arg;
    }

    @Inject(method = "createSpawnWeapon", at = @At("RETURN"), cancellable = true)
    private void mombackport$createSpawnWeapon(CallbackInfoReturnable<ItemStack> cir) {
        float overrideChance = MomBackport.getConfig().getPiglinOverrideSpearSpawnChanceAsFloat();
        if (overrideChance > 0 && this.random.nextFloat() < overrideChance) {
            cir.setReturnValue(new ItemStack(MomBackport.Items.GOLDEN_SPEAR));
        }
    }
}
