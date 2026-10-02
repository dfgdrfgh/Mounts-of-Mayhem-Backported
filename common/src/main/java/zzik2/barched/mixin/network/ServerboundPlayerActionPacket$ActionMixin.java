package zzik2.barched.mixin.network;

import java.util.Arrays;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings({"target", "unused"})
@Mixin(ServerboundPlayerActionPacket.Action.class)
public abstract class ServerboundPlayerActionPacket$ActionMixin {

    @Shadow
    @Final
    @Mutable
    private static ServerboundPlayerActionPacket.Action[] $VALUES;

    static {
        int ordinal = $VALUES.length;
        ServerboundPlayerActionPacket.Action[] expanded = Arrays.copyOf($VALUES, ordinal + 1);
        expanded[ordinal] = barched$create("STAB", ordinal);
        $VALUES = expanded;
    }

    @Invoker("<init>")
    public static ServerboundPlayerActionPacket.Action barched$create(String name, int ordinal) {
        throw new AssertionError("Mixin invoker was not transformed");
    }
}
