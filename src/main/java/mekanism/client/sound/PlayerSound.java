package mekanism.client.sound;

import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.config.MekanismConfig;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

//TODO - 1.16.2: Should this extend EntityTickableSound
public abstract class PlayerSound extends MovingSoundInstance {

    @Nonnull
    private WeakReference<PlayerEntity> playerReference;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float fadeUpStep = 0.1f;
    private float fadeDownStep = 0.1f;

    public PlayerSound(@Nonnull PlayerEntity player, @Nonnull SoundEvent sound) {
        super(sound, SoundCategory.PLAYERS);
        this.playerReference = new WeakReference<>(player);
        this.lastX = (float) player.getX();
        this.lastY = (float) player.getY();
        this.lastZ = (float) player.getZ();
        this.repeat = true;
        this.repeatDelay = 0;

        // N.B. the volume must be > 0 on first time it's processed by sound system or else it will not
        // get registered for tick events.
        this.volume = 0.1F;
    }

    @Nullable
    private PlayerEntity getPlayer() {
        return playerReference.get();
    }

    protected void setFade(float fadeUpStep, float fadeDownStep) {
        this.fadeUpStep = fadeUpStep;
        this.fadeDownStep = fadeDownStep;
    }

    @Override
    public double getX() {
        //Gracefully handle the player becoming null if this object is kept around after update marks us as donePlaying
        PlayerEntity player = getPlayer();
        if (player != null) {
            this.lastX = (float) player.getX();
        }
        return this.lastX;
    }

    @Override
    public double getY() {
        //Gracefully handle the player becoming null if this object is kept around after update marks us as donePlaying
        PlayerEntity player = getPlayer();
        if (player != null) {
            this.lastY = (float) player.getY();
        }
        return this.lastY;
    }

    @Override
    public double getZ() {
        //Gracefully handle the player becoming null if this object is kept around after update marks us as donePlaying
        PlayerEntity player = getPlayer();
        if (player != null) {
            this.lastZ = (float) player.getZ();
        }
        return this.lastZ;
    }

    @Override
    public void tick() {
        PlayerEntity player = getPlayer();
        if (player == null || !player.isAlive()) {
            //TODO - 1.16.2: Re-evaluate sounds because I feel like we may not be properly reinitializing the sounds after we mark it as being done playing
            setDone();
            this.volume = 0.0F;
            return;
        }

        if (shouldPlaySound(player)) {
            if (volume < 1.0F) {
                // If we weren't max volume, start fading up
                volume = Math.min(1.0F, volume + fadeUpStep);
            }
        } else if (volume > 0.0F) {
            // Not yet fully muted, fade down
            volume = Math.max(0.0F, volume - fadeDownStep);
        }
    }

    public abstract boolean shouldPlaySound(@Nonnull PlayerEntity player);

    @Override
    public float getVolume() {
        return super.getVolume() * MekanismConfig.client.baseSoundVolume.get();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean canPlay() {
        PlayerEntity player = getPlayer();
        if (player == null) {
            return super.canPlay();
        }
        return !player.isSilent();
    }

    public enum SoundType {
        FLAMETHROWER,
        JETPACK,
        SCUBA_MASK,
        GRAVITATIONAL_MODULATOR
    }
}