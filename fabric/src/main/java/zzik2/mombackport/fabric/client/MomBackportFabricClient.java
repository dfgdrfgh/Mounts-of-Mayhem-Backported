package zzik2.mombackport.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import zzik2.mombackport.MomBackportClient;

public final class MomBackportFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        MomBackportClient.init();
    }
}
