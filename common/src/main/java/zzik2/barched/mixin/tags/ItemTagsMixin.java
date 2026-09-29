package zzik2.barched.mixin.tags;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import zzik2.zreflex.mixin.ModifyAccess;


@Mixin(ItemTags.class)
public abstract class ItemTagsMixin {

    @Shadow
    private static TagKey<Item> bind(String string) {
        return null;
    }

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> CAMEL_HUSK_FOOD = bind("camel_husk_food");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> ZOMBIE_HORSE_FOOD = bind("zombie_horse_food");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> NAUTILUS_FOOD = bind("nautilus_food");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> NAUTILUS_BUCKET_FOOD = bind("nautilus_bucket_food");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> NAUTILUS_TAMING_ITEMS = bind("nautilus_taming_items");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> SPEARS = bind("spears");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> LUNGE_ENCHANTABLE = bind("enchantable/lunge");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> MELEE_WEAPON_ENCHANTABLE = bind("enchantable/melee_weapon");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> SKELETON_PREFERRED_WEAPONS = bind("skeleton_preferred_weapons");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> DROWNED_PREFERRED_WEAPONS = bind("drowned_preferred_weapons");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> PIGLIN_PREFERRED_WEAPONS = bind("piglin_preferred_weapons");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final TagKey<Item> PILLAGER_PREFERRED_WEAPONS = bind("pillager_preferred_weapons");
}
