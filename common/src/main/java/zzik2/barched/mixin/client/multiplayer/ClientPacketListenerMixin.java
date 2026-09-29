package zzik2.barched.mixin.client.multiplayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.client.nautilus.NautilusInventoryScreen;
import zzik2.barched.nautilus.NautilusInventoryMenu;
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "handleHorseScreenOpen", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", shift = At.Shift.AFTER), cancellable = true)
    private void barched$openNautilusInventory(ClientboundHorseScreenOpenPacket packet, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null && client.level.getEntity(packet.getEntityId()) instanceof AbstractNautilus nautilus) {
            NautilusInventoryMenu menu = new NautilusInventoryMenu(packet.getContainerId(), client.player.getInventory(), new SimpleContainer(2), nautilus);
            client.player.containerMenu = menu;
            client.setScreen(new NautilusInventoryScreen(menu, client.player.getInventory()));
            ci.cancel();
        }
    }
}
