package zzik2.barched.mixin.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Horse.class)
public abstract class HorseMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void barched$match12111HazardPathfinding(EntityType<? extends Horse> entityType, Level level, CallbackInfo ci) {
        Horse self = (Horse) (Object) this;
        self.setPathfindingMalus(PathType.DANGER_OTHER, -1.0F);
        self.setPathfindingMalus(PathType.DAMAGE_OTHER, -1.0F);
    }
}
