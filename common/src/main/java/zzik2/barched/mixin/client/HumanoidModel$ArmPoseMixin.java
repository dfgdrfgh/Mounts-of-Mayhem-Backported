package zzik2.barched.mixin.client;

import java.util.Arrays;
import net.minecraft.client.model.HumanoidModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import zzik2.barched.bridge.client.HumanoidModel$ArmPoseBridge;

@SuppressWarnings({"target", "unused"})
@Mixin(HumanoidModel.ArmPose.class)
public abstract class HumanoidModel$ArmPoseMixin implements HumanoidModel$ArmPoseBridge {

    @Shadow
    @Final
    @Mutable
    private static HumanoidModel.ArmPose[] $VALUES;

    static {
        int ordinal = $VALUES.length;
        HumanoidModel.ArmPose[] expanded = Arrays.copyOf($VALUES, ordinal + 1);
        expanded[ordinal] = barched$create("SPEAR", ordinal, false);
        $VALUES = expanded;
    }

    @Invoker("<init>")
    public static HumanoidModel.ArmPose barched$create(String name, int ordinal, boolean twoHanded) {
        throw new AssertionError("Mixin invoker was not transformed");
    }
}
