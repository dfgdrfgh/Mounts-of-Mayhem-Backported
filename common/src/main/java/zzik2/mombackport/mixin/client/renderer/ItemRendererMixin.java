package zzik2.mombackport.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;
import zzik2.zreflex.mixin.ModifyAccess;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModelShaper itemModelShaper;

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation WOODEN_SPEAR_MODEL = mombackport$spear("wooden");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation WOODEN_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("wooden");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation STONE_SPEAR_MODEL = mombackport$spear("stone");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation STONE_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("stone");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation COPPER_SPEAR_MODEL = mombackport$spear("copper");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation COPPER_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("copper");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation IRON_SPEAR_MODEL = mombackport$spear("iron");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation IRON_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("iron");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation GOLDEN_SPEAR_MODEL = mombackport$spear("golden");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation GOLDEN_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("golden");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation DIAMOND_SPEAR_MODEL = mombackport$spear("diamond");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation DIAMOND_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("diamond");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation NETHERITE_SPEAR_MODEL = mombackport$spear("netherite");
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final ModelResourceLocation NETHERITE_SPEAR_IN_HAND_MODEL = mombackport$spear_in_hand("netherite");

    @Unique private ItemDisplayContext mombackport$itemDisplayContext;
    @Unique private ItemStack mombackport$itemStack;

    @Inject(method = "render", at = @At("HEAD"))
    private void mombackport$captureItemStack(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        this.mombackport$itemStack = itemStack;
        this.mombackport$itemDisplayContext = itemDisplayContext;
    }


    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER, ordinal = 0), argsOnly = true, index = 8)
    private BakedModel mombackport$render(BakedModel bakedModel) {
        boolean bl2 = mombackport$itemDisplayContext == ItemDisplayContext.GUI || mombackport$itemDisplayContext == ItemDisplayContext.GROUND || mombackport$itemDisplayContext == ItemDisplayContext.FIXED;
        if (bl2) {
            if (mombackport$itemStack.is(MomBackport.Items.WOODEN_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(WOODEN_SPEAR_MODEL);
            } else if (mombackport$itemStack.is(MomBackport.Items.STONE_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(STONE_SPEAR_MODEL);
            } else if (mombackport$itemStack.is(MomBackport.Items.COPPER_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(COPPER_SPEAR_MODEL);
            } else if (mombackport$itemStack.is(MomBackport.Items.IRON_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(IRON_SPEAR_MODEL);
            } else if (mombackport$itemStack.is(MomBackport.Items.GOLDEN_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(GOLDEN_SPEAR_MODEL);
            } else if (mombackport$itemStack.is(MomBackport.Items.DIAMOND_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(DIAMOND_SPEAR_MODEL);
            } else if (mombackport$itemStack.is(MomBackport.Items.NETHERITE_SPEAR)) {
                return this.itemModelShaper.getModelManager().getModel(NETHERITE_SPEAR_MODEL);
            }
        }
        return bakedModel;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 2))
    private boolean mombackport$render(boolean original) {
        return original && !mombackport$itemStack.is(MomBackport.ItemTags.SPEARS);
    }

    @Redirect(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel mombackport$getModel(ItemModelShaper instance, ItemStack bakedModel) {
        if (bakedModel.is(MomBackport.Items.WOODEN_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(WOODEN_SPEAR_IN_HAND_MODEL);
        } else if (bakedModel.is(MomBackport.Items.STONE_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(STONE_SPEAR_IN_HAND_MODEL);
        } else if (bakedModel.is(MomBackport.Items.COPPER_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(COPPER_SPEAR_IN_HAND_MODEL);
        } else if (bakedModel.is(MomBackport.Items.IRON_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(IRON_SPEAR_IN_HAND_MODEL);
        } else if (bakedModel.is(MomBackport.Items.GOLDEN_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(GOLDEN_SPEAR_IN_HAND_MODEL);
        } else if (bakedModel.is(MomBackport.Items.DIAMOND_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(DIAMOND_SPEAR_IN_HAND_MODEL);
        } else if (bakedModel.is(MomBackport.Items.NETHERITE_SPEAR)) {
            return this.itemModelShaper.getModelManager().getModel(NETHERITE_SPEAR_IN_HAND_MODEL);
        }
        return instance.getItemModel(bakedModel);
    }

    @Unique
    private static ModelResourceLocation mombackport$spear(String prefix) {
        return ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace(prefix + "_spear"));
    }

    @Unique
    private static ModelResourceLocation mombackport$spear_in_hand(String prefix) {
        return ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace(prefix + "_spear_in_hand"));
    }
}
