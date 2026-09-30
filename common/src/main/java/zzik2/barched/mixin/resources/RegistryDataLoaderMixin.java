package zzik2.barched.mixin.resources;

import net.minecraft.resources.RegistryDataLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.nautilus.ZombieNautilusVariant;

import java.util.ArrayList;
import java.util.List;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Shadow @Final @Mutable public static List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES;
    @Shadow @Final @Mutable public static List<RegistryDataLoader.RegistryData<?>> SYNCHRONIZED_REGISTRIES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void barched$registerNautilusVariants(CallbackInfo ci) {
        List<RegistryDataLoader.RegistryData<?>> worldgen = new ArrayList<>(WORLDGEN_REGISTRIES);
        worldgen.add(new RegistryDataLoader.RegistryData<>(ZombieNautilusVariant.REGISTRY, ZombieNautilusVariant.DIRECT_CODEC, true));
        WORLDGEN_REGISTRIES = List.copyOf(worldgen);
        List<RegistryDataLoader.RegistryData<?>> network = new ArrayList<>(SYNCHRONIZED_REGISTRIES);
        network.add(new RegistryDataLoader.RegistryData<>(ZombieNautilusVariant.REGISTRY, ZombieNautilusVariant.NETWORK_CODEC, true));
        SYNCHRONIZED_REGISTRIES = List.copyOf(network);
    }
}
