package zzik2.barched.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class BackportedHorseArmorItem extends AnimalArmorItem {

    private final int defense;
    private final float toughness;
    private final ResourceLocation texture;
    private final ItemAttributeModifiers modifiers;

    public BackportedHorseArmorItem(
            Holder<ArmorMaterial> material,
            int defense,
            float toughness,
            float knockbackResistance,
            ResourceLocation texture,
            Item.Properties properties
    ) {
        super(material, BodyType.EQUESTRIAN, false, properties.stacksTo(1));
        this.defense = defense;
        this.toughness = toughness;
        this.texture = texture;

        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(EquipmentSlot.BODY);
        ResourceLocation modifierId = ResourceLocation.withDefaultNamespace("armor.body");
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(modifierId, defense, AttributeModifier.Operation.ADD_VALUE), slotGroup)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifierId, toughness, AttributeModifier.Operation.ADD_VALUE), slotGroup);

        if (knockbackResistance > 0.0F) {
            builder.add(
                    Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(modifierId, knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    slotGroup
            );
        }

        this.modifiers = builder.build();

        net.minecraft.world.level.block.DispenserBlock.registerBehavior(this, new net.minecraft.core.dispenser.OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(net.minecraft.core.dispenser.BlockSource source, ItemStack stack) {
                net.minecraft.core.BlockPos pos = source.pos().relative(source.state().getValue(net.minecraft.world.level.block.DispenserBlock.FACING));
                java.util.List<net.minecraft.world.entity.animal.horse.AbstractHorse> horses = source.level().getEntitiesOfClass(
                        net.minecraft.world.entity.animal.horse.AbstractHorse.class,
                        new net.minecraft.world.phys.AABB(pos),
                        horse -> horse.isAlive() && horse.canUseSlot(EquipmentSlot.BODY)
                );
                for (net.minecraft.world.entity.animal.horse.AbstractHorse horse : horses) {
                    if (horse.isBodyArmorItem(stack) && !horse.isWearingBodyArmor() && horse.isTamed()) {
                        horse.setBodyArmorItem(stack.split(1));
                        this.setSuccess(true);
                        return stack;
                    }
                }
                return super.execute(source, stack);
            }
        });
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.modifiers;
    }

    @Override
    public int getDefense() {
        return this.defense;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.HORSE_ARMOR);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }
}
