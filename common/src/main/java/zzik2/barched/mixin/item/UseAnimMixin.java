package zzik2.barched.mixin.item;

import java.util.Arrays;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings({"target", "unused"})
@Mixin(UseAnim.class)
public abstract class UseAnimMixin {

    @Shadow
    @Final
    @Mutable
    private static UseAnim[] $VALUES;

    static {
        int ordinal = $VALUES.length;
        UseAnim[] expanded = Arrays.copyOf($VALUES, ordinal + 1);
        expanded[ordinal] = barched$create("BARCHED$SPEAR", ordinal);
        $VALUES = expanded;
    }

    @Invoker("<init>")
    public static UseAnim barched$create(String name, int ordinal) {
        throw new AssertionError("Mixin invoker was not transformed");
    }
}
