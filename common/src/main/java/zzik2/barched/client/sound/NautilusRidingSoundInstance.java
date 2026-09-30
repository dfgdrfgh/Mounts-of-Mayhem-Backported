package zzik2.barched.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.player.Player;
import zzik2.barched.Barched;

public class NautilusRidingSoundInstance extends AbstractTickableSoundInstance {
    private final Player player;
    private final AbstractNautilus nautilus;

    public NautilusRidingSoundInstance(Player player, AbstractNautilus nautilus) {
        super(Barched.SoundEvents.NAUTILUS_RIDING, nautilus.getSoundSource(), SoundInstance.createUnseededRandom());
        this.player = player;
        this.nautilus = nautilus;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public boolean canPlaySound() {
        return !this.nautilus.isSilent();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.nautilus.isRemoved() || !this.player.isPassenger() || this.player.getVehicle() != this.nautilus) {
            this.stop();
            return;
        }

        if (!this.nautilus.isUnderWater()) {
            this.volume = 0.0F;
            return;
        }

        float speed = (float)this.nautilus.getDeltaMovement().length();
        this.volume = speed >= 0.01F ? 5.0F * Mth.clampedLerp(speed, 0.0F, 1.0F) : 0.0F;
    }
}
