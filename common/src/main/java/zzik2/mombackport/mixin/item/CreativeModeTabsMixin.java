package zzik2.mombackport.mixin.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin {

    @Inject(method = "method_51318", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;HUSK_SPAWN_EGG:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC, shift = At.Shift.AFTER))
    private static void mombackport$acceptSpawnEgg(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output, CallbackInfo ci) {
        output.accept(MomBackport.Items.PARCHED_SPAWN_EGG);
        output.accept(MomBackport.Items.CAMEL_HUSK_SPAWN_EGG);
    }

    @Inject(method = "method_51325", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;NETHERITE_SWORD:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC, shift = At.Shift.AFTER))
    private static void mombackport$acceptCombat(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output, CallbackInfo ci) {
        output.accept(MomBackport.Items.WOODEN_SPEAR);
        output.accept(MomBackport.Items.STONE_SPEAR);
        output.accept(MomBackport.Items.COPPER_SPEAR);
        output.accept(MomBackport.Items.IRON_SPEAR);
        output.accept(MomBackport.Items.GOLDEN_SPEAR);
        output.accept(MomBackport.Items.DIAMOND_SPEAR);
        output.accept(MomBackport.Items.NETHERITE_SPEAR);
    }
}
