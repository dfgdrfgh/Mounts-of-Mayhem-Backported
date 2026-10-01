package zzik2.barched.mixin.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import zzik2.barched.Barched;
import zzik2.barched.bridge.item.Item$PropertiesBridge;
import zzik2.barched.item.BackportedHorseArmorItem;
import zzik2.zreflex.mixin.ModifyAccess;

@Mixin(Items.class)
public abstract class ItemsMixin {

    @Shadow
    public static Item registerItem(String string, Item arg) {
        return null;
    }


    @ModifyArgs(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MobBucketItem;<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/item/Item$Properties;)V")
    )
    private static void barched$addFishBucketFood(Args args) {
        EntityType<?> type = args.get(0);
        Item.Properties properties = args.get(3);

        if (type == EntityType.PUFFERFISH) {
            args.set(3, properties.food(Foods.PUFFERFISH));
        } else if (type == EntityType.SALMON) {
            args.set(3, properties.food(Foods.SALMON));
        } else if (type == EntityType.COD) {
            args.set(3, properties.food(Foods.COD));
        } else if (type == EntityType.TROPICAL_FISH) {
            args.set(3, properties.food(Foods.TROPICAL_FISH));
        }
    }

    @ModifyArgs(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=zombie_horse_spawn_egg")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/SpawnEggItem;<init>(Lnet/minecraft/world/entity/EntityType;IILnet/minecraft/world/item/Item$Properties;)V"))
    private static void barched$modifyEggColor(Args args) {
        if (args.get(0) == EntityType.ZOMBIE_HORSE) {
            args.set(1, 0xFFFFFF);
            args.set(2, 0xFFFFFF);
        }
    }

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item NETHERITE_HORSE_ARMOR = registerItem("netherite_horse_armor", new BackportedHorseArmorItem(
            ArmorMaterials.NETHERITE,
            19,
            3.0F,
            0.1F,
            net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/netherite.png"),
            new Item.Properties().fireResistant()
    ));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item PARCHED_SPAWN_EGG = registerItem("parched_spawn_egg", new SpawnEggItem(Barched.EntityType.PARCHED, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item CAMEL_HUSK_SPAWN_EGG = registerItem("camel_husk_spawn_egg", new SpawnEggItem(Barched.EntityType.CAMEL_HUSK, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item WOODEN_SPEAR = registerItem("wooden_spear", new SpearItem(Tiers.WOOD, ((Item$PropertiesBridge) new Item.Properties()).spear(Tiers.WOOD, 0.65F, 0.7F, 0.75F, 5.0F, 14.0F, 10.0F, 5.1F, 15.0F, 4.6F)));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item STONE_SPEAR = registerItem("stone_spear", new SpearItem(Tiers.STONE, ((Item$PropertiesBridge) new Item.Properties()).spear(Tiers.STONE, 0.75F, 0.82F, 0.7F, 4.5F, 10.0F, 9.0F, 5.1F, 13.75F, 4.6F)));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item COPPER_SPEAR = registerItem("copper_spear", new SpearItem(Barched.Tiers.COPPER, ((Item$PropertiesBridge) new Item.Properties()).spear(Barched.Tiers.COPPER, 0.85F, 0.82F, 0.65F, 4.0F, 9.0F, 8.25F, 5.1F, 12.5F, 4.6F)));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item IRON_SPEAR = registerItem("iron_spear", new SpearItem(Tiers.IRON, ((Item$PropertiesBridge) new Item.Properties()).spear(Tiers.IRON, 0.95F, 0.95F, 0.6F, 2.5F, 8.0F, 6.75F, 5.1F, 11.25F, 4.6F)));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item GOLDEN_SPEAR = registerItem("golden_spear", new SpearItem(Tiers.GOLD, ((Item$PropertiesBridge) new Item.Properties()).spear(Tiers.GOLD, 0.95F, 0.7F, 0.7F, 3.5F, 10.0F, 8.5F, 5.1F, 13.75F, 4.6F)));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item DIAMOND_SPEAR = registerItem("diamond_spear", new SpearItem(Tiers.DIAMOND, ((Item$PropertiesBridge) new Item.Properties()).spear(Tiers.DIAMOND, 1.05F, 1.075F, 0.5F, 3.0F, 7.5F, 6.5F, 5.1F, 10.0F, 4.6F)));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item NETHERITE_SPEAR = registerItem("netherite_spear", new SpearItem(Tiers.NETHERITE, ((Item$PropertiesBridge) new Item.Properties()).spear(Tiers.NETHERITE, 1.15F, 1.2F, 0.4F, 2.5F, 7.0F, 5.5F, 5.1F, 8.75F, 4.6F).fireResistant()));


    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item COPPER_NAUTILUS_ARMOR = registerItem("copper_nautilus_armor", new zzik2.barched.item.NautilusArmorItem("copper", 4, 0F, 0F, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item IRON_NAUTILUS_ARMOR = registerItem("iron_nautilus_armor", new zzik2.barched.item.NautilusArmorItem("iron", 5, 0F, 0F, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item GOLDEN_NAUTILUS_ARMOR = registerItem("golden_nautilus_armor", new zzik2.barched.item.NautilusArmorItem("gold", 7, 0F, 0F, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item DIAMOND_NAUTILUS_ARMOR = registerItem("diamond_nautilus_armor", new zzik2.barched.item.NautilusArmorItem("diamond", 11, 2F, 0F, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item NETHERITE_NAUTILUS_ARMOR = registerItem("netherite_nautilus_armor", new zzik2.barched.item.NautilusArmorItem("netherite", 19, 3F, 0.1F, new Item.Properties().fireResistant()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item NAUTILUS_SPAWN_EGG = registerItem("nautilus_spawn_egg", new SpawnEggItem(Barched.EntityType.NAUTILUS, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final Item ZOMBIE_NAUTILUS_SPAWN_EGG = registerItem("zombie_nautilus_spawn_egg", new SpawnEggItem(Barched.EntityType.ZOMBIE_NAUTILUS, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
}
