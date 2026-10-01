package zzik2.mombackport.mixin.entity.attributes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.monster.Parched;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zzik2.mombackport.MomBackport;

@Mixin(DefaultAttributes.class)
public class DefaultAttributesMixin {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"))
    private static ImmutableMap<EntityType<? extends LivingEntity>, AttributeSupplier> mombackport$build(ImmutableMap.Builder instance) {
        instance.put(MomBackport.EntityType.PARCHED, Parched.createAttributes().build());
        instance.put(MomBackport.EntityType.CAMEL_HUSK, Camel.createAttributes().build());
        return instance.build();
    }
}
