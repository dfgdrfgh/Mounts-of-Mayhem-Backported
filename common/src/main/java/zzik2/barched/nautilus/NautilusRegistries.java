package zzik2.barched.nautilus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import zzik2.zreflex.reflection.ZReflectionTool;
public final class NautilusRegistries {
    public static final TagKey<EntityType<?>> HOSTILES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("nautilus_hostiles"));
    public static final SensorType<TemptingSensor> TEMPTATIONS = ZReflectionTool.getStaticFieldValue(SensorType.class, "NAUTILUS_TEMPTATIONS");
    private NautilusRegistries() {}
}
