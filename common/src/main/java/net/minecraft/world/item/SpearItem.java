package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import zzik2.barched.Barched;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SpearItem extends TieredItem {

    public SpearItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack repairStack) {
        TagKey<Item> repairTag;
        Tier tier = this.getTier();
        if (tier == Tiers.WOOD) {
            repairTag = Barched.ItemTags.WOODEN_TOOL_MATERIALS;
        } else if (tier == Tiers.STONE) {
            repairTag = Barched.ItemTags.STONE_TOOL_MATERIALS;
        } else if (tier == Barched.Tiers.COPPER) {
            repairTag = Barched.ItemTags.COPPER_TOOL_MATERIALS;
        } else if (tier == Tiers.IRON) {
            repairTag = Barched.ItemTags.IRON_TOOL_MATERIALS;
        } else if (tier == Tiers.GOLD) {
            repairTag = Barched.ItemTags.GOLD_TOOL_MATERIALS;
        } else if (tier == Tiers.DIAMOND) {
            repairTag = Barched.ItemTags.DIAMOND_TOOL_MATERIALS;
        } else if (tier == Tiers.NETHERITE) {
            repairTag = Barched.ItemTags.NETHERITE_TOOL_MATERIALS;
        } else {
            return super.isValidRepairItem(itemStack, repairStack);
        }

        return repairStack.is(repairTag);
    }

    @Override
    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return false;
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
        itemStack.hurtAndBreak(1, livingEntity2, EquipmentSlot.MAINHAND);
    }
}
