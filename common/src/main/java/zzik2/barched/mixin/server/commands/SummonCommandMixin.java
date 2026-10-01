package zzik2.barched.mixin.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.Barched;

@Mixin(SummonCommand.class)
public abstract class SummonCommandMixin {

    @Unique
    private static final SimpleCommandExceptionType BARCHED$ERROR_FAILED_PEACEFUL =
            new SimpleCommandExceptionType(Component.literal("Parched cannot be summoned in Peaceful difficulty"));

    @Inject(method = "createEntity", at = @At("HEAD"))
    private static void barched$rejectParchedInPeaceful(
            CommandSourceStack source,
            Holder.Reference<EntityType<?>> type,
            Vec3 pos,
            CompoundTag nbt,
            boolean finalize,
            CallbackInfoReturnable<Entity> cir
    ) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        if (level.getDifficulty() == Difficulty.PEACEFUL && type.value() == Barched.EntityType.PARCHED) {
            throw BARCHED$ERROR_FAILED_PEACEFUL.create();
        }
    }
}
