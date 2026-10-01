package zzik2.barched.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import zzik2.barched.Barched;

public final class SpearAwareMeleeAttack {
    private SpearAwareMeleeAttack() {
    }

    public static OneShot<Mob> create(int cooldownBetweenAttacks) {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.LOOK_TARGET),
                instance.present(MemoryModuleType.ATTACK_TARGET),
                instance.absent(MemoryModuleType.ATTACK_COOLING_DOWN),
                instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
        ).apply(instance, (lookTarget, attackTarget, attackCoolingDown, nearestEntities) -> (level, body, timestamp) -> {
            LivingEntity target = instance.get(attackTarget);
            if (!isHoldingUsableNonMeleeWeapon(body)
                    && body.isWithinMeleeAttackRange(target)
                    && instance.get(nearestEntities).contains(target)) {
                lookTarget.set(new EntityTracker(target, true));
                body.swing(InteractionHand.MAIN_HAND);
                body.doHurtTarget(target);
                attackCoolingDown.setWithExpiry(true, cooldownBetweenAttacks);
                return true;
            }
            return false;
        }));
    }

    private static boolean isHoldingUsableNonMeleeWeapon(Mob body) {
        return body.isHolding(stack -> {
            if (stack.has(Barched.DataComponents.KINETIC_WEAPON)) {
                return true;
            }
            Item item = stack.getItem();
            return item instanceof ProjectileWeaponItem projectileWeapon && body.canFireProjectileWeapon(projectileWeapon);
        });
    }
}
