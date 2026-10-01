package zzik2.mombackport.mixin.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.camel.CamelAi;
import org.spongepowered.asm.mixin.Mixin;
import zzik2.mombackport.bridge.entity.AbstractHorseBridge;

@Mixin(CamelAi.CamelPanic.class)
public abstract class CamelAi$CamelPanicMixin extends AnimalPanic<Camel> {

    public CamelAi$CamelPanicMixin(float f) {
        super(f);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Camel pathfinderMob) {
        return super.checkExtraStartConditions(serverLevel, pathfinderMob) && !((AbstractHorseBridge) pathfinderMob).isMobControlled();
    }
}
