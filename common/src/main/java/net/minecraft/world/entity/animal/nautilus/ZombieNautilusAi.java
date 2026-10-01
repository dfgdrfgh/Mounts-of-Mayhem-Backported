package net.minecraft.world.entity.animal.nautilus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import java.util.Set;
import zzik2.barched.Barched;
import zzik2.barched.nautilus.NautilusRegistries;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ChargeAttack;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.nautilus.NautilusAi;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.schedule.Activity;

public class ZombieNautilusAi {
    private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 1.0f;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 0.9f;
    private static final float SPEED_WHEN_ATTACKING = 0.5f;
    private static final float ATTACK_KNOCKBACK_FORCE = 2.0f;
    private static final int TIME_BETWEEN_ATTACKS = 80;
    private static final double MAX_CHARGE_DISTANCE = 12.0;
    private static final double MAX_TARGET_DETECTION_DISTANCE = 11.0;
    protected static final ImmutableList<SensorType<? extends Sensor<? super ZombieNautilus>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, NautilusRegistries.TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING, MemoryModuleType.ATTACK_TARGET, Barched.MemoryModuleType.CHARGE_COOLDOWN_TICKS, MemoryModuleType.HURT_BY, MemoryModuleType.ANGRY_AT, Barched.MemoryModuleType.ATTACK_TARGET_COOLDOWN);

    protected static Brain.Provider<ZombieNautilus> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected static Brain<?> makeBrain(Brain<ZombieNautilus> $$0) {
        ZombieNautilusAi.initCoreActivity($$0);
        ZombieNautilusAi.initIdleActivity($$0);
        ZombieNautilusAi.initFightActivity($$0);
        $$0.setCoreActivities(ImmutableSet.of(Activity.CORE));
        $$0.setDefaultActivity(Activity.IDLE);
        $$0.useDefaultActivity();
        return $$0;
    }

    private static void initCoreActivity(Brain<ZombieNautilus> $$0) {
        $$0.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(Barched.MemoryModuleType.CHARGE_COOLDOWN_TICKS), new CountDownCooldownTicks(Barched.MemoryModuleType.ATTACK_TARGET_COOLDOWN)));
    }

    private static void initIdleActivity(Brain<ZombieNautilus> $$02) {
        $$02.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(1, new FollowTemptation($$0 -> Float.valueOf(0.9f), $$0 -> $$0.isBaby() ? 2.5 : 3.5)), Pair.of(2, StartAttacking.<ZombieNautilus>create(mob -> NautilusAi.findNearestValidAttackTarget((ServerLevel)mob.level(), mob))), Pair.of(3, new GateBehavior(ImmutableMap.of(MemoryModuleType.WALK_TARGET, (MemoryStatus.VALUE_ABSENT)), ImmutableSet.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL, ImmutableList.of(Pair.of(RandomStroll.swim(1.0f), 2), Pair.of(SetWalkTargetFromLookTarget.create(1.0f, 3), 3))))));
    }

    private static void initFightActivity(Brain<ZombieNautilus> $$0) {
        $$0.addActivityWithConditions(Activity.FIGHT, ImmutableList.of(Pair.of(0, new ChargeAttack(80, NautilusAi.ATTACK_TARGET_CONDITIONS, 0.5f, 2.0f, 12.0, 11.0, Barched.SoundEvents.ZOMBIE_NAUTILUS_DASH))), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, (MemoryStatus.VALUE_PRESENT)), Pair.of(MemoryModuleType.TEMPTING_PLAYER, (MemoryStatus.VALUE_ABSENT)), Pair.of(MemoryModuleType.BREED_TARGET, (MemoryStatus.VALUE_ABSENT)), Pair.of(Barched.MemoryModuleType.CHARGE_COOLDOWN_TICKS, (MemoryStatus.VALUE_ABSENT))));
    }

    public static void updateActivity(ZombieNautilus $$0) {
        $$0.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }
}

