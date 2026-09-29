package zzik2.barched.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import zzik2.barched.Barched;

public class NautilusArmorItem extends Item implements Equipable {
    private final ResourceLocation texture;
    public NautilusArmorItem(String material, int defense, float toughness, float knockbackResistance, Properties properties) {
        super(properties.stacksTo(1).component(DataComponents.ATTRIBUTE_MODIFIERS, modifiers(defense, toughness, knockbackResistance)));
        net.minecraft.world.level.block.DispenserBlock.registerBehavior(this, new net.minecraft.core.dispenser.DefaultDispenseItemBehavior() {
            @Override protected net.minecraft.world.item.ItemStack execute(net.minecraft.core.dispenser.BlockSource source, net.minecraft.world.item.ItemStack stack) {
                net.minecraft.core.BlockPos pos = source.pos().relative(source.state().getValue(net.minecraft.world.level.block.DispenserBlock.FACING));
                var mounts = source.level().getEntitiesOfClass(net.minecraft.world.entity.animal.nautilus.AbstractNautilus.class, new net.minecraft.world.phys.AABB(pos), mount -> mount.isSaddleable() && mount.getBodyArmorItem().isEmpty());
                if (mounts.isEmpty()) return super.execute(source, stack);
                var mount = mounts.getFirst();
                mount.setBodyArmorItem(stack.split(1));
                mount.setPersistenceRequired();
                return stack;
            }
        });
        this.texture = ResourceLocation.withDefaultNamespace("textures/entity/equipment/nautilus_body/" + material + ".png");
    }
    private static ItemAttributeModifiers modifiers(int defense, float toughness, float resistance) {
        ResourceLocation id = ResourceLocation.withDefaultNamespace("armor.body");
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(id, defense, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.BODY)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.BODY);
        if (resistance > 0.0F) builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, resistance, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.BODY);
        return builder.build();
    }
    @Override public EquipmentSlot getEquipmentSlot() { return EquipmentSlot.BODY; }
    @Override public Holder<SoundEvent> getEquipSound() { return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(Barched.SoundEvents.NAUTILUS_ARMOR_EQUIP); }
    public ResourceLocation getTexture() { return this.texture; }
}
