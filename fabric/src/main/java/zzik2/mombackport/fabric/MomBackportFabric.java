package zzik2.mombackport.fabric;

import net.fabricmc.api.ModInitializer;
import zzik2.mombackport.MomBackport;

public final class MomBackportFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        MomBackport.init();
    }
}
