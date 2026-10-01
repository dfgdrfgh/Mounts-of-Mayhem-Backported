package zzik2.mombackport.mixin.client.model;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackportClient;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow
    protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation arg);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1))
    private void mombackport$init(BlockColors blockColors, ProfilerFiller profilerFiller, Map map, Map map2, CallbackInfo ci) {
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.WOODEN_SPEAR_IN_HAND_MODEL);
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.STONE_SPEAR_IN_HAND_MODEL);
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.COPPER_SPEAR_IN_HAND_MODEL);
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.IRON_SPEAR_IN_HAND_MODEL);
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.GOLDEN_SPEAR_IN_HAND_MODEL);
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.DIAMOND_SPEAR_IN_HAND_MODEL);
        this.loadSpecialItemModelAndDependencies(MomBackportClient.ItemRenderer.NETHERITE_SPEAR_IN_HAND_MODEL);
    }
}
