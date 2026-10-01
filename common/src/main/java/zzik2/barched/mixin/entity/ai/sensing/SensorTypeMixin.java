package zzik2.barched.mixin.entity.ai.sensing;
import net.minecraft.world.entity.ai.sensing.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import zzik2.barched.Barched;
import zzik2.zreflex.mixin.ModifyAccess;
import java.util.function.Supplier;
@Mixin(SensorType.class)
public abstract class SensorTypeMixin {
    @Shadow private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) { return null; }
    @ModifyAccess(access = Opcodes.ACC_PUBLIC)
    private static final SensorType<TemptingSensor> NAUTILUS_TEMPTATIONS = register("nautilus_temptations", () -> new TemptingSensor(stack -> stack.is(Barched.ItemTags.NAUTILUS_FOOD)));
}
