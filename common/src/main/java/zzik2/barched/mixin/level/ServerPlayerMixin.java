package zzik2.barched.mixin.level;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import zzik2.barched.bridge.entity.EntityBridge;
import zzik2.barched.bridge.entity.PlayerBridge;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerBridge, zzik2.barched.nautilus.NautilusInventoryBridge {

    @Shadow private Vec3 lastKnownClientMovement;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Override
    public Vec3 getKnownSpeed() {
        Entity entity = this.getVehicle();
        return entity != null && entity.getControllingPassenger() != this ? ((EntityBridge) entity).getKnownSpeed() : this.lastKnownClientMovement;
    }

    @Shadow private int containerCounter;
    @Shadow private void nextContainerCounter() { throw new AssertionError(); }
    @Shadow private void initMenu(net.minecraft.world.inventory.AbstractContainerMenu menu) { throw new AssertionError(); }
    @Shadow public net.minecraft.server.network.ServerGamePacketListenerImpl connection;

    @Override
    public void barched$openNautilusInventory(net.minecraft.world.entity.animal.nautilus.AbstractNautilus nautilus) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        if (this.containerMenu != this.inventoryMenu) self.closeContainer();
        this.nextContainerCounter();
        this.connection.send(new net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket(this.containerCounter, 0, nautilus.getId()));
        this.containerMenu = new zzik2.barched.nautilus.NautilusInventoryMenu(this.containerCounter, this.getInventory(), nautilus.getInventory(), nautilus);
        this.initMenu(this.containerMenu);
    }
}
