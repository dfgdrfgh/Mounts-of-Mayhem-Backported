package zzik2.barched.mixin.sounds;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import zzik2.zreflex.mixin.ModifyAccess;


@Mixin(SoundEvents.class)
public abstract class SoundEventsMixin {

    @Shadow
    private static SoundEvent register(String string) {
        return null;
    }

    @Shadow
    private static Holder.Reference<SoundEvent> registerForHolder(String string) {
        return null;
    }

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARCHED_AMBIENT = register("entity.parched.ambient");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARCHED_DEATH = register("entity.parched.death");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARCHED_HURT = register("entity.parched.hurt");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARCHED_STEP = register("entity.parched.step");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARROT_IMITATE_PARCHED = register("entity.parrot.imitate.parched");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARROT_IMITATE_CAMEL_HUSK = register("entity.parrot.imitate.camel_husk");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARROT_IMITATE_ZOMBIE_HORSE = register("entity.parrot.imitate.zombie_horse");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_AMBIENT = register("entity.camel_husk.ambient");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_DASH = register("entity.camel_husk.dash");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_DASH_READY = register("entity.camel_husk.dash_ready");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_DEATH = register("entity.camel_husk.death");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_EAT = register("entity.camel_husk.eat");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_HURT = register("entity.camel_husk.hurt");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_SADDLE = register("entity.camel_husk.saddle");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_SIT = register("entity.camel_husk.sit");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_STAND = register("entity.camel_husk.stand");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_STEP = register("entity.camel_husk.step");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent CAMEL_HUSK_STEP_SAND = register("entity.camel_husk.step_sand");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_HORSE_ANGRY = register("entity.zombie_horse.angry");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_HORSE_EAT = register("entity.zombie_horse.eat");

    private static final Holder<SoundEvent> LUNGE = registerForHolder("item.spear.lunge");
    private static final Holder<SoundEvent> LUNGE_1 = registerForHolder("item.spear.lunge_1");
    private static final Holder<SoundEvent> LUNGE_2 = registerForHolder("item.spear.lunge_2");
    private static final Holder<SoundEvent> LUNGE_3 = registerForHolder("item.spear.lunge_3");
    private static final Holder<SoundEvent> SPEAR_USE = registerForHolder("item.spear.use");
    private static final Holder<SoundEvent> SPEAR_HIT = registerForHolder("item.spear.hit");
    private static final Holder<SoundEvent> SPEAR_ATTACK = registerForHolder("item.spear.attack");
    private static final Holder<SoundEvent> SPEAR_WOOD_USE = registerForHolder("item.spear_wood.use");
    private static final Holder<SoundEvent> SPEAR_WOOD_HIT = registerForHolder("item.spear_wood.hit");
    private static final Holder<SoundEvent> SPEAR_WOOD_ATTACK = registerForHolder("item.spear_wood.attack");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_AMBIENT = register("entity.nautilus.ambient");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_AMBIENT_LAND = register("entity.nautilus.ambient_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_DEATH = register("entity.nautilus.death");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_DEATH_LAND = register("entity.nautilus.death_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_EAT = register("entity.nautilus.eat");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_HURT = register("entity.nautilus.hurt");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_HURT_LAND = register("entity.nautilus.hurt_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_SWIM = register("entity.nautilus.swim");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_DASH = register("entity.nautilus.dash");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_DASH_LAND = register("entity.nautilus.dash_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_DASH_READY = register("entity.nautilus.dash_ready");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_DASH_READY_LAND = register("entity.nautilus.dash_ready_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_AMBIENT = register("entity.zombie_nautilus.ambient");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_AMBIENT_LAND = register("entity.zombie_nautilus.ambient_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_DEATH = register("entity.zombie_nautilus.death");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_DEATH_LAND = register("entity.zombie_nautilus.death_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_EAT = register("entity.zombie_nautilus.eat");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_HURT = register("entity.zombie_nautilus.hurt");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_HURT_LAND = register("entity.zombie_nautilus.hurt_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_SWIM = register("entity.zombie_nautilus.swim");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_DASH = register("entity.zombie_nautilus.dash");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_DASH_LAND = register("entity.zombie_nautilus.dash_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_DASH_READY = register("entity.zombie_nautilus.dash_ready");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent ZOMBIE_NAUTILUS_DASH_READY_LAND = register("entity.zombie_nautilus.dash_ready_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_AMBIENT = register("entity.baby_nautilus.ambient");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_AMBIENT_LAND = register("entity.baby_nautilus.ambient_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_DEATH = register("entity.baby_nautilus.death");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_DEATH_LAND = register("entity.baby_nautilus.death_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_EAT = register("entity.baby_nautilus.eat");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_HURT = register("entity.baby_nautilus.hurt");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_HURT_LAND = register("entity.baby_nautilus.hurt_land");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent BABY_NAUTILUS_SWIM = register("entity.baby_nautilus.swim");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_RIDING = register("entity.nautilus.riding");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_ARMOR_EQUIP = register("item.armor.equip_nautilus");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_ARMOR_UNEQUIP = register("item.armor.unequip_nautilus");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent SADDLE_UNEQUIP = register("item.saddle.unequip");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_SADDLE_EQUIP = register("item.nautilus_saddle_equip");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent NAUTILUS_SADDLE_UNDERWATER_EQUIP = register("item.nautilus_saddle_underwater_equip");

    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SoundEvent PARROT_IMITATE_ZOMBIE_NAUTILUS = register("entity.parrot.imitate.zombie_nautilus");

}
