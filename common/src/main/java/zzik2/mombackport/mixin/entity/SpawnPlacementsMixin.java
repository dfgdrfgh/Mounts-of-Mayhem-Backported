package zzik2.mombackport.mixin.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zzik2.mombackport.MomBackport;


@Mixin(SpawnPlacements.class)
public abstract class SpawnPlacementsMixin {

    @Shadow
    @Deprecated
    private static <T extends Mob> void register(EntityType<T> arg, SpawnPlacementType arg2, Heightmap.Types arg3, SpawnPlacements.SpawnPredicate<T> arg4) {
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SpawnPlacements;register(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/SpawnPlacementType;Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/entity/SpawnPlacements$SpawnPredicate;)V"))
    private static <T extends Mob> void mombackport$redirectCheckRule(EntityType<T> spawnplacements$data, SpawnPlacementType arg, Heightmap.Types arg2, SpawnPlacements.SpawnPredicate<T> arg3) {
        if (spawnplacements$data == EntityType.ZOMBIE_HORSE) {
            register(EntityType.ZOMBIE_HORSE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MomBackport.Monster::checkMonsterSpawnRules0);
            return;
        }
        register(spawnplacements$data, arg, arg2, arg3);
    }

    static {
        register(MomBackport.EntityType.PARCHED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MomBackport.Monster::checkSurfaceMonstersSpawnRules);
        register(MomBackport.EntityType.CAMEL_HUSK, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MomBackport.Monster::checkSurfaceMonstersSpawnRules);
    }
}
