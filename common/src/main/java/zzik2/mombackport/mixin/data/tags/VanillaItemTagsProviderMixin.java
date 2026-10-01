package zzik2.mombackport.mixin.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;

import java.util.concurrent.CompletableFuture;

@Mixin(VanillaItemTagsProvider.class)
public abstract class VanillaItemTagsProviderMixin extends ItemTagsProvider {

    public VanillaItemTagsProviderMixin(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> completableFuture2) {
        super(packOutput, completableFuture, completableFuture2);
    }

    @Inject(method = "addTags", at = @At("TAIL"))
    private void mombackport$addTags(HolderLookup.Provider provider, CallbackInfo ci) {
        this.tag(MomBackport.ItemTags.CAMEL_HUSK_FOOD).add(Items.RABBIT_FOOT);
        this.tag(MomBackport.ItemTags.ZOMBIE_HORSE_FOOD).add(Items.RED_MUSHROOM);
        this.tag(MomBackport.ItemTags.SPEARS).add(MomBackport.Items.DIAMOND_SPEAR, MomBackport.Items.STONE_SPEAR, MomBackport.Items.GOLDEN_SPEAR, MomBackport.Items.NETHERITE_SPEAR, MomBackport.Items.WOODEN_SPEAR, MomBackport.Items.IRON_SPEAR, MomBackport.Items.COPPER_SPEAR);
        this.tag(MomBackport.ItemTags.LUNGE_ENCHANTABLE).addTag(MomBackport.ItemTags.SPEARS);
        this.tag(MomBackport.ItemTags.MELEE_WEAPON_ENCHANTABLE).addTag(ItemTags.SWORDS).addTag(MomBackport.ItemTags.SPEARS);
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).addTag(MomBackport.ItemTags.SPEARS);
        this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(MomBackport.ItemTags.SPEARS);
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(MomBackport.ItemTags.SPEARS);
    }
}
