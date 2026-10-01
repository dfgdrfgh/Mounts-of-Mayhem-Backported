package zzik2.mombackport.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import zzik2.mombackport.MomBackport;
import zzik2.mombackport.MomBackportClient;

@Mod(MomBackport.MOD_ID)
public final class MomBackportNeoForge {
    public MomBackportNeoForge() {
        // Run our common setup.
        MomBackport.init();

        if (FMLEnvironment.dist.isClient()) {
            MomBackportClient.init();
        }
    }
}
