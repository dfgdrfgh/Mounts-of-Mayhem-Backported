package zzik2.barched.mixin.client.renderer;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.BarchedClient;

@Mixin(value = ItemModelShaper.class, priority = 2000)
public abstract class ItemModelShaperMixin {

    @Shadow
    @Final
    private ModelManager modelManager;

    @Inject(
            method = "getItemModel(Lnet/minecraft/world/item/Item;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void barched$forceZombieHorseEggItemModel(Item item, CallbackInfoReturnable<BakedModel> cir) {
        if (item == Items.ZOMBIE_HORSE_SPAWN_EGG) {
            cir.setReturnValue(this.modelManager.getModel(BarchedClient.ItemRenderer.ZOMBIE_HORSE_SPAWN_EGG_MODEL));
        }
    }

    @Inject(
            method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void barched$forceZombieHorseEggStackModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.is(Items.ZOMBIE_HORSE_SPAWN_EGG)) {
            cir.setReturnValue(this.modelManager.getModel(BarchedClient.ItemRenderer.ZOMBIE_HORSE_SPAWN_EGG_MODEL));
        }
    }
}
