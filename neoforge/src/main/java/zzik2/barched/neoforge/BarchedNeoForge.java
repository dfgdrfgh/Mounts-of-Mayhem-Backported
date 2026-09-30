package zzik2.barched.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import zzik2.barched.Barched;
import zzik2.barched.BarchedClient;
import zzik2.barched.nautilus.ZombieNautilusVariant;

@Mod(Barched.MOD_ID)
public final class BarchedNeoForge {
    public BarchedNeoForge(IEventBus modBus) {
        modBus.addListener(BarchedNeoForge::registerVariantRegistry);
        // Run our common setup.
        Barched.init();

        if (FMLEnvironment.dist.isClient()) {
            BarchedClient.init();
        }
    }

    private static void registerVariantRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ZombieNautilusVariant.REGISTRY, ZombieNautilusVariant.DIRECT_CODEC, ZombieNautilusVariant.NETWORK_CODEC);
    }
}
