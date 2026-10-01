package zzik2.barched.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import zzik2.barched.Barched;
import zzik2.barched.nautilus.ZombieNautilusVariant;

public final class BarchedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(ZombieNautilusVariant.REGISTRY, ZombieNautilusVariant.DIRECT_CODEC, ZombieNautilusVariant.NETWORK_CODEC);
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Barched.init();
    }
}
