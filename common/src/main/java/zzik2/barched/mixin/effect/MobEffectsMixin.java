package zzik2.barched.mixin.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import zzik2.barched.effect.BackportedMobEffect;
import zzik2.zreflex.mixin.ModifyAccess;

@Mixin(MobEffects.class)
public abstract class MobEffectsMixin {
    @Shadow
    private static Holder<MobEffect> register(String id, MobEffect effect) {
        return null;
    }

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Holder<MobEffect> BREATH_OF_THE_NAUTILUS =
            register("breath_of_the_nautilus", new BackportedMobEffect(MobEffectCategory.BENEFICIAL, 65518));
}
