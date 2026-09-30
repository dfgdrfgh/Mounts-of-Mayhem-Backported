package zzik2.barched.mixin.util.datafix;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.fixes.TridentAnimationFix;
import net.minecraft.util.datafix.schemas.V4656;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DataFixers.class)
public class DataFixersMixin {

    @Inject(method = "addFixers", at = @At("TAIL"))
    private static void barched$addFixers(DataFixerBuilder dataFixerBuilder, CallbackInfo ci) {
        Schema schema = dataFixerBuilder.addSchema(3955, V4656::new);
        dataFixerBuilder.addFixer(new TridentAnimationFix(schema));
        dataFixerBuilder.addFixer(new AddNewChoices(schema, "Added Mounts of Mayhem entities", References.ENTITY));
    }
}
